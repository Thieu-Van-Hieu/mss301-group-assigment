import {useEffect} from "react";
import {authApiClient} from "@repo/api";

export const LoginPageDispatcher = () => {
	useEffect(() => {
		(async () => {
			const response = await authApiClient.getLoginUrl();
			const loginUrlResponse = response.data;
			if (loginUrlResponse.loginUrl) {
				window.location.href = loginUrlResponse.loginUrl;
			}
		})();
	}, []);

	return (
		<div className="min-h-screen bg-bg-main flex flex-col items-center justify-center space-y-4">
			{/* Hiển thị màn hình chờ đồng bộ tinh tế trong lúc API chạy ngầm (chỉ diễn ra trong mili-giây) */}
			<div className="w-12 h-12 border-4 border-brand border-t-transparent rounded-full animate-spin"></div>
			<p className="text-gray-500 font-bold tracking-tight animate-pulse">
				Đang kết nối tới cổng xác thực an toàn...
			</p>
		</div>
	);
};
