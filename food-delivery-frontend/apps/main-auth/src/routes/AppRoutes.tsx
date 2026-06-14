import {Navigate, Route, Routes} from "react-router-dom";
import {PATHS} from "./paths";
import {RegisterPage} from "../features/auth/pages/RegisterPage";

// Các Component giao diện (sẽ tạo ở bước sau)
const LoginPage = () => <div className="text-center p-12"><h2>Trang Đăng Nhập</h2></div>;

export default function AppRoutes() {
	return (
		<Routes>
			{/* Vào trang chủ mặc định đá thẳng sang trang LoginPage */}
			<Route path="/" element={<Navigate to={PATHS.LOGIN} replace/>}/>

			{/* Định nghĩa các Router Auth */}
			<Route path={PATHS.LOGIN} element={<LoginPage/>}/>
			<Route path={PATHS.REGISTER} element={<RegisterPage/>}/>

			{/* Bắt bài tất cả các URL bậy bạ không tồn tại */}
			<Route path={PATHS.NOT_FOUND} element={<Navigate to={PATHS.LOGIN} replace/>}/>
		</Routes>
	);
}