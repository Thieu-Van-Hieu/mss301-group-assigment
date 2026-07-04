import React from "react";
import {useNavigate} from "react-router-dom";
import {APP_ROUTES} from "@repo/routes";
import {AppButton} from "../../../components/AppButton.tsx";

export const UnAuthorizedPage = (): React.JSX.Element => {
	const navigate = useNavigate();

	const handleGoToLogin = () => {
		APP_ROUTES.AUTH.children.LOGIN.goTo(navigate, {replace: true});
	};

	return (
		<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-6 text-gray-800">
			<div
				className="max-w-md w-full bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center space-y-6">

				{/* Mã lỗi & Icon */}
				<div className="space-y-2">
					<h1 className="text-6xl font-black tracking-tight text-amber-500 font-mono">401</h1>
					<h2 className="text-xl font-bold tracking-tight text-gray-900">
						Phiên làm việc hết hạn
					</h2>
					<p className="text-sm text-gray-500 leading-relaxed">
						Hệ thống không tìm thấy thông tin đăng nhập của bạn hoặc phiên làm việc đã kết thúc. Vui lòng
						đăng nhập lại để tiếp tục.
					</p>
				</div>

				{/* Hình minh họa ổ khóa tối giản */}
				<div className="py-4 flex justify-center">
					<div
						className="w-20 h-20 bg-amber-50 text-amber-600 rounded-2xl flex items-center justify-center text-3xl shadow-inner">
						🔒
					</div>
				</div>

				{/* Nút hành động */}
				<div className="flex flex-col pt-2">
					<AppButton
						type="button"
						variant="brand"
						onClick={handleGoToLogin}
						className="w-full"
					>
						Đăng nhập ngay
					</AppButton>
				</div>

			</div>
		</div>
	);
};