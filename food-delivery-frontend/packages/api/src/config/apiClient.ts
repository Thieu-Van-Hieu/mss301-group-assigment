import axios, {AxiosInstance, InternalAxiosRequestConfig} from "axios";
import {ErrorResponse} from "@/types";
import {toast} from "sonner";
import {APP_ROUTES} from "@repo/routes";

/**
 * Mở rộng interface `AxiosRequestConfig` để tích hợp các cờ (flags) phục vụ
 * cho cơ chế tự động làm mới mã thông báo (Auto Refresh Token).
 */
declare module "axios" {
	export interface AxiosRequestConfig {
		/** Đánh dấu yêu cầu này đã từng thử lại (retry) để tránh vòng lặp vô hạn nếu refresh thất bại */
		_retry?: boolean;
		/** Đánh dấu đây chính là yêu cầu đi gọi API Refresh Token */
		_isRefreshRequest?: boolean;
	}
}

/** * Địa chỉ URL mặc định của hệ thống API Gateway nếu không cấu hình động.
 */
const DEFAULT_URL = "http://localhost:8080";

/**
 * Thực thể Axios Instance chính dùng để thực hiện các yêu cầu HTTP trong toàn bộ hệ thống.
 * Được cấu hình tự động đính kèm thông tin chứng thực (Credentials) như HttpOnly Cookies.
 */
export const apiClient: AxiosInstance = axios.create({
	baseURL: DEFAULT_URL,
	headers: {
		"Content-Type": "application/json",
	},
	withCredentials: true,
});

// --- KHAI BÁO CÁC CALLBACK ĐỂ PHÁ VỠ VÒNG LẶP DEPENDENCY ---

/** Hàm lấy Access Token hiện tại từ bộ nhớ RAM */
let getTokenFromRamFn: () => string | null = (): string | null => null;

/** Hàm gọi API Refresh Token về phía Backend */
let callRefreshApiFn: () => Promise<any> = async () => Promise.reject(new Error("No refresh handler"));

/** Callback thực thi khi làm mới mã thông báo thành công nhằm cập nhật lại State/RAM ở tầng ứng dụng */
let onRefreshSuccessCallback: ((newToken: string) => void) | null = null;

/** Callback thực thi khi địa chỉ Base URL thay đổi */
let onUrlChangeCallback: ((newUrl: string) => void) | null = null;

/**
 * Thiết lập cầu nối chứng thực (Auth Bridge) cho API Client.
 * Giúp liên kết các hàm xử lý Token từ tầng Context/Redux xuống Axios mà không bị vòng lặp dependency.
 *
 * @param configs - Cấu hình hàm xử lý chứng thực
 * @param configs.getToken - Hàm đóng gói lấy token hiện tại từ RAM
 * @param configs.callRefresh - Hàm đóng gói gọi API làm mới token
 * @param configs.onRefreshSuccess - Callback xử lý khi đã lấy được token mới thành công
 * * @example
 * ```ts
 * setupApiAuthBridge({
 * getToken: () => authStore.accessToken,
 * callRefresh: () => authService.refreshToken(),
 * onRefreshSuccess: (token) => authStore.setToken(token)
 * });
 * ```
 */
export const setupApiAuthBridge = (configs: {
	getToken: () => string | null;
	callRefresh: () => Promise<any>;
	onRefreshSuccess: (newToken: string) => void;
}): void => {
	getTokenFromRamFn = configs.getToken;
	callRefreshApiFn = configs.callRefresh;
	onRefreshSuccessCallback = configs.onRefreshSuccess;
};

/**
 * Đại diện cho một yêu cầu HTTP bị tạm dừng và đưa vào hàng đợi
 * trong khi hệ thống đang thực hiện làm mới mã thông báo ngầm.
 */
interface FailedRequestItem {
	/** Hàm giải quyết để tiếp tục thực thi yêu cầu cũ với token mới */
	resolve: (token: string) => void;
	/** Hàm từ chối yêu cầu nếu quá trình refresh thất bại */
	reject: (error: unknown) => void;
}

/** Hàng đợi lưu trữ các yêu cầu bị tạm hoãn do lỗi 401 Unauthorized */
let failedRequestsQueue: FailedRequestItem[] = [];

/** Trạng thái biểu thị hệ thống đang trong quá trình call API Refresh Token */
let isRefreshing: boolean = false;

/**
 * Đăng ký một listener để lắng nghe sự thay đổi cấu hình địa chỉ URL của API Gateway.
 * *Chú ý: Hàm này thường dùng nội bộ hoặc phục vụ cho mục đích Debug/DevTools.*
 *
 * @param callback - Hàm callback nhận vào URL mới khi có sự thay đổi
 * @internal
 */
export const _registerUrlChangeListener = (callback: (newUrl: string) => void): void => {
	onUrlChangeCallback = callback;
};

/**
 * Cập nhật động địa chỉ API Gateway (Base URL) của hệ thống trong thời gian chạy (Runtime).
 * Đồng thời kích hoạt callback thông báo cho các bên liên quan.
 *
 * @param customUrl - Chuỗi URL mới của API Gateway (Ví dụ: `https://api.production.com`)
 */
export const setApiGatewayUrl = (customUrl: string): void => {
	if (customUrl) {
		apiClient.defaults.baseURL = customUrl;
		if (onUrlChangeCallback) {
			onUrlChangeCallback(customUrl);
		}
	}
};

/**
 * **1. Request Interceptor**
 * * Tự động can thiệp vào trước khi gửi yêu cầu lên Server:
 * - Đính kèm chuỗi JWT Token vào Header `Authorization: Bearer <token>`.
 * - Bỏ qua việc đính kèm nếu yêu cầu đó chính là yêu cầu đi Refresh Token (`_isRefreshRequest`).
 */
apiClient.interceptors.request.use(
	(config: InternalAxiosRequestConfig) => {
		// Nếu là request đi làm mới token, không đính kèm Access Token cũ đang hết hạn
		if (config._isRefreshRequest) {
			return config;
		}

		const token = getTokenFromRamFn();
		if (token && config.headers) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error: unknown) => Promise.reject(error)
);

/**
 * **2. Response Interceptor**
 * * Đón đầu phản hồi trả về từ Server để xử lý tập trung:
 * - **Trường hợp lỗi 401 (Unauthorized):** Tự động kích hoạt cơ chế xếp hàng (Queueing) và
 * gọi API làm mới mã thông báo ngầm (Silent Refresh Token). Tránh làm gián đoạn trải nghiệm người dùng.
 * - **Trường hợp lỗi khác:** Tự động bắt lỗi và hiển thị thông báo Toaster (Sonner) trực quan ra màn hình.
 */
apiClient.interceptors.response.use(
	(response) => response,
	async (error) => {
		const originalRequest = error.config;

		if (error.response) {
			const status = error.response.status;
			const data = error.response.data;

			if (status === 401) {
				// Nếu chính request đi refresh bị 401 -> Token hết hạn hoàn toàn, hủy hàng đợi và chuyển hướng đăng nhập
				if (originalRequest._isRefreshRequest) {
					failedRequestsQueue = [];
					isRefreshing = false;
					window.location.href = APP_ROUTES.AUTH.children.LOGIN.path;
					return Promise.reject(error);
				}

				if (!originalRequest._retry) {
					// Cơ chế Hàng Đợi: Nếu đang có luồng khác đi làm mới mã thông báo, giữ yêu cầu này lại
					if (isRefreshing) {
						return new Promise<string>((resolve, reject) => {
							failedRequestsQueue.push({resolve, reject});
						})
							.then((token) => {
								if (originalRequest.headers) {
									originalRequest.headers.Authorization = `Bearer ${token}`;
								}
								return apiClient(originalRequest);
							})
							.catch((err) => Promise.reject(err));
					}

					originalRequest._retry = true;
					isRefreshing = true;

					return new Promise((resolve, reject) => {
						callRefreshApiFn()
							.then((res) => {
								const newToken = res.data?.accessToken;

								if (newToken) {
									if (originalRequest.headers) {
										originalRequest.headers.Authorization = `Bearer ${newToken}`;
									}

									if (onRefreshSuccessCallback) {
										onRefreshSuccessCallback(newToken);
									}

									// Giải phóng hàng đợi: Chạy lại toàn bộ các request bị kẹt với token mới
									failedRequestsQueue.forEach((promise) => promise.resolve(newToken));
									failedRequestsQueue = [];

									resolve(apiClient(originalRequest));
								} else {
									reject(error);
								}
							})
							.catch((refreshError: unknown) => {
								// Refresh thất bại hoàn toàn: Từ chối toàn bộ hàng đợi và đẩy về trang Login
								failedRequestsQueue.forEach((promise) => promise.reject(refreshError));
								failedRequestsQueue = [];
								window.location.href = APP_ROUTES.AUTH.children.LOGIN.path;
								reject(refreshError);
							})
							.finally(() => {
								isRefreshing = false;
							});
					});
				}
			}

			// --- Xử lý Toaster thông báo lỗi hệ thống dựa trên cấu trúc Data trả về ---
			let realBackendMessage = "";
			if (data) {
				if (typeof data === "string") {
					realBackendMessage = data;
				} else if (typeof data === "object") {
					const backendError = data as ErrorResponse & Record<string, unknown>;
					realBackendMessage = backendError.message || backendError.error || "";
					if (!realBackendMessage) realBackendMessage = JSON.stringify(data);
				}
			}
			if (!realBackendMessage) realBackendMessage = error.message;

			toast.error(`Lỗi hệ thống (${status})`, {
				description: realBackendMessage,
				duration: 4000
			});
		} else {
			// Xử lý khi mất kết nối mạng hoặc server sập (Không nhận được response)
			toast.error("Lỗi kết nối", {
				description: "Không thể kết nối tới máy chủ. Vui lòng kiểm tra lại mạng hoặc thử lại sau.",
				duration: 3000
			});
		}

		return Promise.reject(error);
	}
);