import {createContext} from "react";
import {AuthContextType} from "@/types";

/**
 * Context quản lý trạng thái xác thực (Authentication) toàn cục của ứng dụng.
 *
 * Context này cung cấp các thông tin liên quan đến phiên đăng nhập hiện tại bao gồm:
 * - `accessToken`: Chuỗi JWT Token đang lưu giữ trong bộ nhớ RAM.
 * - `user`: Đối tượng chứa thông tin chi tiết người dùng (`UserInfo`) đã được giải mã từ Token.
 * - `isInitializing`: Cờ trạng thái cho biết hệ thống đang thực hiện Silent Refresh khi khởi chạy hay không.
 * - Các hàm xử lý cốt lõi: `setAccessToken` (Cập nhật token mới) và `logout` (Đăng xuất hệ thống).
 *
 * @example
 * ```tsx
 * const context = useContext(AuthContext);
 * if (context) {
 *   const { user, logout } = context;
 *   console.log(user?.fullName);
 * }
 * ```
 *
 * @type {React.Context<AuthContextType>}
 */
export const AuthContext: React.Context<AuthContextType> = createContext<AuthContextType>({
	accessToken: null,
	user: null,
	setAccessToken: () => {
	},
	logout: () => {
	},
	isInitializing: true,
});