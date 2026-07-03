import {Navigate, Route, Routes} from "react-router-dom";
import {RegisterPage} from "../features/auth/pages/RegisterPage";
import {AuthCallbackPage} from "../features/auth/pages/AuthCallbackPage.tsx";
import {LoginPageDispatcher} from "../features/auth/pages/LoginPageDispatcher.tsx";
import {DashboardPage} from "../features/auth/pages/DashboardPage.tsx";
import {APP_ROUTES} from "@repo/routes";

export const AppRoutes = () => {
	return (
		<Routes>
			{/* Vào trang chủ mặc định đá thẳng sang trang LoginPage */}
			<Route path="/" element={<Navigate to={APP_ROUTES.AUTH.children.LOGIN.path} replace/>}/>

			{/* Định nghĩa các Router Auth */}
			<Route path={APP_ROUTES.AUTH.children.LOGIN.path} element={<LoginPageDispatcher/>}/>
			<Route path={APP_ROUTES.AUTH.children.REGISTER.path} element={<RegisterPage/>}/>
			<Route path={APP_ROUTES.AUTH.children.CALLBACK.path} element={<AuthCallbackPage/>}/>
			<Route path={"/dashboard"} element={<DashboardPage/>}/>

			{/* Bắt bài tất cả các URL bậy bạ không tồn tại */}
			<Route path={APP_ROUTES.ERROR.basePath}
				   element={<Navigate to={APP_ROUTES.AUTH.children.LOGIN.path} replace/>}/>
		</Routes>
	);
}