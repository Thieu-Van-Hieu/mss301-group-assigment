import {useAuth} from "@repo/auth";
import {Navigate, Outlet, useLocation} from "react-router-dom";
import {APP_ROUTES} from "@repo/routes";
import type {RouteNode} from "@repo/routes/RouteNode";
import {RouteAccessLevel} from "@repo/routes/enums";
import React from "react";

interface AppRouteGuardProps {
	node: RouteNode;
}

export const AppRouteGuard = ({node}: AppRouteGuardProps): React.JSX.Element => {
	const {user, isInitializing} = useAuth();
	const location = useLocation();

	if (isInitializing) {
		return (
			<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center space-y-4">
				<div
					className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
				<p className="text-gray-500 font-medium animate-pulse">Đang đồng bộ phiên làm việc...</p>
			</div>
		);
	}

	// Đẩy trực tiếp mảng roles của user vào để kiểm tra kết quả tổng thể
	const hasAccess = node.canAccess(user?.roles);

	if (!hasAccess) {
		// Trường hợp không có quyền:
		// Nếu chưa đăng nhập -> Đẩy ra Login
		if (!user) {
			return <Navigate to={APP_ROUTES.AUTH.children.LOGIN.path} state={{from: location}} replace/>;
		}

		// Nếu đã đăng nhập nhưng cố tình vào lại trang Login (ANONYMOUS_ONLY) -> Đẩy ra dashboard
		if (node.accessLevel === RouteAccessLevel.ANONYMOUS_ONLY) {
			// TODO: Sửa thành RouteNode sau khi mọi người ghi đủ cho từng role
			return <Navigate to="/dashboard" replace/>;
		}

		// Đã đăng nhập nhưng sai Role -> Đẩy sang trang lỗi 403
		return <Navigate to={APP_ROUTES.ERRORS.children.FORBIDDEN.path} replace/>;
	}

	return <Outlet/>;
}