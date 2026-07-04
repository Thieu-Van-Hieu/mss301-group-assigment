import {Navigate, Route, Routes} from "react-router-dom";
import {RegisterPage} from "../features/auth/pages/RegisterPage";
import {AuthCallbackPage} from "../features/auth/pages/AuthCallbackPage.tsx";
import {LoginPageDispatcher} from "../features/auth/pages/LoginPageDispatcher.tsx";
import {DashboardPage} from "../features/auth/pages/DashboardPage.tsx";
import {APP_ROUTES} from "@repo/routes";
import {UnAuthorizedPage} from "../features/error/page/UnAuthorizedPage.tsx";
import {ForbiddenPage} from "../features/error/page/ForbiddenPage.tsx";
import {NotFoundPage} from "../features/error/page/NotFoundPage.tsx";
import {AppRouteGuard} from "@repo/guards";

export const AppRoutes = () => {
	return (
		<Routes>
			{/* Vào trang chủ mặc định đá thẳng sang trang LoginPage */}
			<Route path="/" element={<Navigate to={APP_ROUTES.AUTH.children.LOGIN.path} replace/>}/>

			{/* ============================================================
			    CỤM AUTH: Áp dụng ANONYMOUS_ONLY cho Login và Register
			   ============================================================ */}
			<Route element={<AppRouteGuard node={APP_ROUTES.AUTH}/>}>
				<Route path={APP_ROUTES.AUTH.children.LOGIN.path} element={<LoginPageDispatcher/>}/>
				<Route path={APP_ROUTES.AUTH.children.REGISTER.path} element={<RegisterPage/>}/>
			</Route>

			{/* ============================================================
			    ROUTE ĐẶC BIỆT: Callback từ Keycloak (Cấu hình PUBLIC trong cây)
			   ============================================================ */}
			<Route element={<AppRouteGuard node={APP_ROUTES.AUTH.children.CALLBACK}/>}>
				<Route path={APP_ROUTES.AUTH.children.CALLBACK.path} element={<AuthCallbackPage/>}/>
			</Route>

			{/* ============================================================
			    CỤM PROTECTED: Ví dụ trang Dashboard test (Ăn theo quyền CUSTOMER)
			   ============================================================ */}
			{/* NOTE: Sửa lại sau khi mọi người thiết lập xong url */}
			<Route element={<AppRouteGuard node={APP_ROUTES.CUSTOMER}/>}>
				{/* Chỉ cần login đúng role CUSTOMER là vào được toàn bộ các trang con ở đây */}
				<Route path="/dashboard" element={<DashboardPage/>}/>
				{/* Phen có thể viết thêm các route như /customer/users, /customer/products... vào cụm này thoải mái */}
			</Route>

			{/* ============================================================
			    CỤM ERROR PAGES: Tự động PUBLIC nhờ node cha ERRORS
			   ============================================================ */}
			<Route element={<AppRouteGuard node={APP_ROUTES.ERRORS}/>}>
				<Route path={APP_ROUTES.ERRORS.children.UNAUTHORIZED.path} element={<UnAuthorizedPage/>}/>
				<Route path={APP_ROUTES.ERRORS.children.FORBIDDEN.path} element={<ForbiddenPage/>}/>
				<Route path={APP_ROUTES.ERRORS.children.NOT_FOUND.path} element={<NotFoundPage/>}/>
			</Route>

			{/* ============================================================
			    BẤT KỲ URL BẬY BẠ KHÔNG TỒN TẠI -> Đẩy thẳng về trang 404 động
			   ============================================================ */}
			<Route
				path={APP_ROUTES.NOT_FOUND_WILDCARD.path}
				element={<Navigate to={APP_ROUTES.ERRORS.children.NOT_FOUND.path} replace/>}
			/>
		</Routes>
	);
};