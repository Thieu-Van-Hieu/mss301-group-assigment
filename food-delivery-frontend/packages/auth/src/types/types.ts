/**
 * Đại diện cho thông tin chi tiết của người dùng sau khi giải mã (decode) từ JWT Token.
 */
export interface UserInfo {
	/** Định danh duy nhất (UUID hoặc ID hệ thống) của người dùng */
	id: string;

	/** Địa chỉ email chính thức của tài khoản */
	email: string;

	/** Họ và tên đầy đủ của người dùng */
	fullName: string;

	/** Số điện thoại (tùy chọn, có thể không tồn tại trong payload token) */
	phoneNumber?: string;

	/** Danh sách các quyền/vai trò được cấp phép của người dùng trong hệ thống */
	roles: string[];

	/** Thời điểm token hết hạn tính bằng số giây (Unix Epoch timestamp) */
	expiresIn: number;
}

/**
 * Định nghĩa cấu trúc dữ liệu và các hàm tương tác được cung cấp bởi `AuthContext`.
 */
export interface AuthContextType {
	/** Access Token hiện tại đang được lưu giữ trong RAM hệ thống (hoặc `null` nếu chưa đăng nhập) */
	accessToken: string | null;

	/** Thông tin chi tiết của người dùng hiện tại (hoặc `null` nếu phiên làm việc chưa xác thực) */
	user: UserInfo | null;

	/**
	 * Hàm cập nhật Access Token thủ công vào hệ thống.
	 *
	 * Hàm này sẽ tự động ghi đè bộ nhớ RAM cho Axios Interceptor, giải mã JWT mới
	 * để cập nhật lại thông tin `user` và kích hoạt re-render UI.
	 *
	 * @param {string | null} token Chuỗi JWT Token mới nhận từ Backend, hoặc truyền `null` để xóa bỏ phiên đăng nhập.
	 */
	setAccessToken: (token: string | null) => void;

	/**
	 * Hàm xử lý đăng xuất hệ thống.
	 *
	 * Thực hiện gọi API hủy phiên làm việc ngầm lên máy chủ, xóa trắng trạng thái
	 * token/user trong RAM và tự động chuyển hướng giao diện về trang đăng nhập (`/login`).
	 */
	logout: () => void;

	/**
	 * Trạng thái khởi tạo của hệ thống Auth.
	 * Đạt giá trị `true` khi ứng dụng đang trong quá trình tự động gia hạn phiên đăng nhập ngầm bằng Cookie khi người dùng ấn F5 (Silent Refresh).
	 */
	isInitializing: boolean;
}