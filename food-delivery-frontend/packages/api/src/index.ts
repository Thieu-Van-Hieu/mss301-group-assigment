// 🌟 1. Khởi tạo cấu hình an toàn cho SDK
import {apiClient} from "./config/apiClient";
import {identityServiceApi} from "./generated";

const apiConfig = new identityServiceApi.Configuration({
	basePath: apiClient.defaults.baseURL, // Đồng bộ tự động lấy URL từ file .env gốc
});
// 🌟 2. KHỞI TẠO SẴN CÁC INSTANCE CONTROLLER (CHẠY TRÊN AXIOS INTERCEPTOR CỦA BẠN)
export const authApiClient = new identityServiceApi.IdentityControllerApi(
	apiConfig,
	apiClient.defaults.baseURL,
	apiClient // Ép bộ code generated chạy trên Axios của bạn
);
// Nếu sau này bạn có thêm các service khác, chỉ cần khởi tạo tiếp tại đây:
// import { orderServiceApi } from "./generated";
// export const orderApiClient = new orderServiceApi.OrderControllerApi(apiConfig, apiClient.defaults.baseURL, apiClient);


// 🌟 3. EXPORT TOÀN BỘ CÁC TYPE/INTERFACE (DTO) ĐỂ FRONTEND CÓ TYPING
export * from "./generated";
export {apiClient} from "./config/apiClient";