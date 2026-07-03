import React, {useEffect, useState} from "react";
import {AuthContext} from "./authContext";
import {UserInfo} from "@/types";
import {registerStoreListener, updateAccessTokenGlobal} from "./authStore";
import {apiClient, authApiClient} from "@repo/api";

/**
 * Định nghĩa thuộc tính cấu hình (Props) cho thành phần `AuthProvider`.
 */
export interface AuthProviderProps {
	/** Các thành phần con (Components) nhận quyền truy cập vào phân vùng dữ liệu xác thực toàn cục */
	children: React.ReactNode;
}

const PUBLIC_ROUTES = ["/login", "/register", "/auth/callback"];

/**
 * Thành phần bọc (Provider Component) quản lý vòng đời và trạng thái xác thực của toàn bộ ứng dụng.
 *
 * Thành phần này chịu trách nhiệm:
 * 1. Khởi tạo trạng thái phiên đăng nhập ngầm khi người dùng tải lại trang hoặc ấn F5 (Silent Refresh).
 * 2. Lắng nghe và đồng bộ dữ liệu mã thông báo mới (`accessToken`) từ Axios Interceptor đổ về RAM để cập nhật React State.
 * 3. Cung cấp bộ công cụ điều phối trạng thái xác thực (`AuthContextType`) cho các thành phần con thông qua React Context.
 *
 * @param {AuthProviderProps} props Đối tượng chứa các thuộc tính cấu hình truyền vào Component.
 * @returns {React.JSX.Element} React Element bao bọc bởi `AuthContext.Provider`.
 *
 * @example
 * ```tsx
 * import { AuthProvider } from "@repo/auth";
 *
 * ReactDOM.createRoot(document.getElementById('root')!).render(
 *   <AuthProvider>
 *     <App />
 *   </AuthProvider>
 * );
 * ```
 */
export const AuthProvider = ({children}: AuthProviderProps): React.JSX.Element => {
	const [accessToken, _setTokenState] = useState<string | null>(null);
	const [user, setUser] = useState<UserInfo | null>(null);
	const [isInitializing, setIsInitializing] = useState(true);

	// Lắng nghe sự thay đổi token từ phía ngoài (Axios Interceptor) đổ về để cập nhật React State
	useEffect(() => {
		const unsubscribe = registerStoreListener((token, decodedUser) => {
			_setTokenState(token);
			setUser(decodedUser);
		});
		return () => unsubscribe();
	}, []);

	/**
	 * Cập nhật Access Token mới vào hệ thống.
	 * Kích hoạt đồng bộ cả bộ nhớ RAM cục bộ và kích hoạt cập nhật React State.
	 *
	 * @param {string | null} token Chuỗi JWT Token mới hoặc `null` nếu xóa phiên làm việc.
	 */
	const setAccessToken = (token: string | null) => {
		updateAccessTokenGlobal(token);
	};

	/**
	 * Thực hiện đăng xuất tài khoản người dùng khỏi hệ thống.
	 *
	 * Quy trình đăng xuất bao gồm:
	 * 1. Gửi yêu cầu POST lên endpoint `/api/v1/auth/logout` để xóa cookie phía máy chủ.
	 * 2. Xóa sạch dữ liệu token và thông tin người dùng trong bộ nhớ RAM ứng dụng.
	 * 3. Chuyển hướng trình duyệt về trang đăng nhập bằng cách gán `window.location.href`.
	 *
	 * @returns {Promise<void>} Kết thúc tác vụ bất đồng bộ xử lý đăng xuất.
	 */
	const logout = async (): Promise<void> => {
		try {
			await apiClient.post("/api/v1/auth/logout");
		} catch (e) {
			console.error("Lỗi logout:", e);
		} finally {
			setAccessToken(null);
			window.location.href = "/login";
		}
	};

	// Xử lý Silent Refresh khi F5
	useEffect(() => {
		(async () => {
			const isPublicRoute = PUBLIC_ROUTES.some(route => window.location.pathname.includes(route));

			if (isPublicRoute) {
				setIsInitializing(false);
				return;
			}

			try {
				const response = await authApiClient.refreshToken();
				const token = response.data?.accessToken;
				if (token) {
					setAccessToken(token);
				}
			} catch (err) {
				console.warn("⚠️ Không thể tự động gia hạn phiên khi F5. Lỗi: ", err);
			} finally {
				setIsInitializing(false);
			}
		})();
	}, []);

	return (
		<AuthContext.Provider value={{accessToken, user, setAccessToken, logout, isInitializing}}>
			{children}
		</AuthContext.Provider>
	);
};