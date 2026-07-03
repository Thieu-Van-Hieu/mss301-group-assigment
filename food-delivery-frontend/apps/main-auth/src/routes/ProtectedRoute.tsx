import React from "react";
import {useAuth} from "@repo/auth";
import {Navigate} from "react-router-dom";

interface ProtectedRouteProps {
	children: React.ReactNode;
}

/**
 * Bộ lọc tuyến đường bảo mật (Route Guard).
 * Ngăn chặn render component con khi hệ thống chưa kiểm tra xong trạng thái đăng nhập từ Cookie.
 */
export const ProtectedRoute = ({children}: ProtectedRouteProps): React.JSX.Element => {
	const {user, isInitializing} = useAuth();

	console.log("Current user is: ", user);
	console.log("Is Initializing: ", isInitializing);
	console.log("Path: ", window.location.pathname);

	// 1. Nếu hệ thống đang chạy Silent Refresh ngầm (F5 lấy lại token từ Cookie), hiển thị màn hình chờ
	if (isInitializing) {
		return (
			<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center space-y-4">
				<div className="w-10 h-10 border-4 border-brand border-t-transparent rounded-full animate-spin"></div>
				<p className="text-gray-500 font-medium animate-pulse">Đang đồng bộ phiên làm việc...</p>
			</div>
		);
	}

	// 2. Nếu đã kiểm tra xong (isInitializing = false) mà vẫn không có user -> Đá thẳng ra trang login
	if (!user) {
		return <Navigate to="/login" replace/>;
	}

	// 3. Nếu có user hợp lệ, cho phép truy cập vào trang con bình thường
	return <>{children}</>;
};