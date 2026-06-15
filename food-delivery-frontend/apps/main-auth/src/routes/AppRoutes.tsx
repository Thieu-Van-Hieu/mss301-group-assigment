import {Navigate, Route, Routes} from "react-router-dom";
import {PATHS} from "./paths";
import {RegisterPage} from "../features/auth/pages/RegisterPage";
import {AuthCallbackPage} from "../features/auth/pages/AuthCallbackPage.tsx";
import {LoginPageDispatcher} from "../features/auth/pages/LoginPageDispatcher.tsx";

export const AppRoutes = () => {
	return (
		<Routes>
			{/* Vào trang chủ mặc định đá thẳng sang trang LoginPage */}
			<Route path="/" element={<Navigate to={PATHS.LOGIN} replace/>}/>

			{/* Định nghĩa các Router Auth */}
			<Route path={PATHS.LOGIN} element={<LoginPageDispatcher/>}/>
			<Route path={PATHS.REGISTER} element={<RegisterPage/>}/>
			<Route path={PATHS.AUTH_CALLBACK} element={<AuthCallbackPage/>}/>

			{/* Bắt bài tất cả các URL bậy bạ không tồn tại */}
			<Route path={PATHS.NOT_FOUND} element={<Navigate to={PATHS.LOGIN} replace/>}/>
		</Routes>
	);
}