import axios from "axios";

const DEFAULT_URL = "http://localhost:8080";

export const apiClient = axios.create({
	baseURL: DEFAULT_URL,
	headers: {
		"Content-Type": "application/json",
	},
	withCredentials: true,
});

// Nơi lưu trữ hàm callback để cập nhật lại các bộ API Generated
let onUrlChangeCallback: ((newUrl: string) => void) | null = null;

export const _registerUrlChangeListener = (callback: (newUrl: string) => void) => {
	onUrlChangeCallback = callback;
};

/**
 * Hàm thiết lập API Gateway thủ công từ các App Vite.
 */
export const setApiGatewayUrl = (customUrl: string): void => {
	if (customUrl) {
		// 1. Cập nhật lại cho chính instance Axios
		apiClient.defaults.baseURL = customUrl;

		// 2. Kích hoạt thông báo để cập nhật các API Controller bên ngoài
		if (onUrlChangeCallback) {
			onUrlChangeCallback(customUrl);
		}
	}
};

apiClient.interceptors.response.use(
	(response) => response,
	(error) => {
		if (error.response?.status === 401) {
			window.location.href = "/login";
		}
		return Promise.reject(error);
	}
);