import {useContext} from "react";
import {AuthContext} from "./authContext";
import {AuthContextType} from "@/types";

/**
 * Custom Hook giúp truy cập nhanh vào trạng thái Xác thực (Authentication) toàn cục của ứng dụng.
 *
 * Hook này đóng vai trò là một lớp bảo vệ (Guard), tự động kiểm tra xem Component gọi nó
 * có được bao bọc bên trong phạm vi của `<AuthProvider>` hay không. Nếu không, hệ thống
 * sẽ lập tức ném ra lỗi (Exception) nhằm phát hiện sớm sai sót trong quá trình phát triển.
 *
 * @returns {AuthContextType} Đối tượng chứa toàn bộ bộ công cụ, trạng thái mã thông báo (Token) và thông tin người dùng đã xác thực.
 * @throws {Error} Nếu hook được gọi từ một Component nằm ngoài phạm vi bao bọc của `<AuthProvider>`.
 *
 * @example
 * ```tsx
 * import { useAuth } from "@repo/auth";
 *
 * const MyComponent = () => {
 *   const { user, logout } = useAuth();
 *
 *   return (
 *     <div>
 *       <p>Xin chào, {user?.fullName}</p>
 *       <button onClick={logout}>Đăng xuất</button>
 *     </div>
 *   );
 * };
 * ```
 */
export const useAuth = (): AuthContextType => {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error("❌ useAuth phải được sử dụng bên trong một AuthProvider");
	}
	return context;
};