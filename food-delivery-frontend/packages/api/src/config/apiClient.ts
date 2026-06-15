import axios from "axios";
import {ErrorResponse} from "@/types";
import {toast} from "sonner";

const DEFAULT_URL = "http://localhost:8080";

/**
 * Instance Axios được cấu hình sẵn để thực hiện các yêu cầu HTTP trong hệ thống.
 * Tự động đính kèm thông tin cấu hình credentials và định dạng nội dung JSON.
 */
export const apiClient = axios.create({
	baseURL: DEFAULT_URL,
	headers: {
		"Content-Type": "application/json",
	},
	withCredentials: true,
});

/**
 * Hàm callback lưu trữ sự kiện thay đổi URL cấu hình của API Gateway.
 */
let onUrlChangeCallback: ((newUrl: string) => void) | null = null;

/**
 * Đăng ký một bộ lắng nghe (listener) khi URL của API Gateway có sự thay đổi.
 * Thường được sử dụng để đồng bộ hóa các bộ mã nguồn API tự động tạo (Generated Controllers).
 * * @param callback Hàm thực thi nhận vào chuỗi URL mới.
 */
export const _registerUrlChangeListener = (callback: (newUrl: string) => void) => {
	onUrlChangeCallback = callback;
};

/**
 * Cập nhật cấu hình URL API Gateway thủ công từ tầng ứng dụng (ví dụ: các Sub-App từ Vite).
 * Thực hiện thay đổi cấu hình gốc của apiClient và kích hoạt thông báo đồng bộ ra bên ngoài.
 * * @param customUrl Chuỗi URL mới của API Gateway cần thiết lập.
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
 * Cấu hình bộ chặn (Interceptor) cho phản hồi HTTP từ `apiClient`.
 * Tự động xử lý điều hướng phiên làm việc hết hạn (401), phân tích dữ liệu lỗi từ Backend
 * và hiển thị thông báo lỗi đồng bộ lên giao diện thông qua Sonner Toast.
 */
apiClient.interceptors.response.use(
	(response) => response,
	(error) => {
		if (error.response) {
			const status = error.response.status;
			const data = error.response.data;

			if (status === 401) {
				window.location.href = "/login";
				return Promise.reject(error);
			}

			let realBackendMessage = "";

			if (data) {
				if (typeof data === "string") {
					realBackendMessage = data;
				} else if (typeof data === "object") {
					const backendError = data as ErrorResponse & Record<string, any>;

					realBackendMessage =
						backendError.message ||
						backendError.error_description ||
						backendError.error ||
						"";

					if (!realBackendMessage) {
						realBackendMessage = JSON.stringify(data);
					}
				}
			}

			if (!realBackendMessage) {
				realBackendMessage = error.message;
			}

			toast.error(`Lỗi hệ thống (${status})`, {
				description: realBackendMessage,
				duration: 4000
			});
		} else {
			toast.error("Lỗi kết nối", {
				description: "Không thể kết nối tới máy chủ. Vui lòng kiểm tra lại mạng hoặc thử lại sau.",
				duration: 3000
			});
		}

		return Promise.reject(error);
	}
);