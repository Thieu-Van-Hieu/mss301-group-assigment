/**
 * Định nghĩa cấu trúc dữ liệu phản hồi lỗi chuẩn (Standard Error Response) từ hệ thống Backend.
 *
 * Interface này đại diện cho payload dữ liệu nhận được khi một yêu cầu HTTP thất bại (mã trạng thái >= 400),
 * giúp hệ thống hiển thị thông báo lỗi chi tiết và chính xác lên giao diện người dùng (UI Toaster).
 */
export interface ErrorResponse {
	/**
	 * Mốc thời gian xảy ra lỗi trên hệ thống máy chủ.
	 * Định dạng chuỗi thường là ISO 8601 (ví dụ: "2026-06-27T08:04:00.000+00:00").
	 */
	timestamp?: string;

	/**
	 * Mã trạng thái HTTP (HTTP Status Code) của lỗi.
	 * Ví dụ: 400 (Bad Request), 401 (Unauthorized), 403 (Forbidden), 500 (Internal Server Error).
	 */
	status: number;

	/**
	 * Nhãn định danh ngắn gọn của lỗi tương ứng với mã trạng thái HTTP.
	 * Ví dụ: "Unauthorized", "Bad Request", "Internal Server Error".
	 */
	error: string;

	/**
	 * Tin nhắn mô tả chi tiết nguyên nhân gây ra lỗi từ logic xử lý nghiệp vụ của Backend.
	 * Đây là chuỗi ký tự thường được dùng để hiển thị trực tiếp lên giao diện cho người dùng đọc.
	 * Ví dụ: "Tài khoản hoặc mật khẩu không chính xác", "Mã thông báo đã hết hạn phiên làm việc".
	 */
	message: string;

	/**
	 * Đường dẫn API Endpoint (URI) nơi xảy ra lỗi trên hệ thống máy chủ.
	 * Ví dụ: "/api/v1/auth/login", "/api/v1/users/profile".
	 */
	path: string;
}