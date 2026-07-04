import React from "react";
import {useNavigate} from "react-router-dom";
import {APP_ROUTES} from "@repo/routes";
import {AppButton} from "../../../components/AppButton.tsx";

export const ForbiddenPage = (): React.JSX.Element => {
	const navigate = useNavigate();

	const handleGoBack = () => {
		// Đá ngược về login hoặc trang chủ tùy logic hệ thống của bạn
		APP_ROUTES.AUTH.children.LOGIN.goTo(navigate, {replace: true});
	};

	return (
		<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-6 text-gray-800">
			<div
				className="max-w-md w-full bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center space-y-6">

				{/* Mã lỗi & Icon */}
				<div className="space-y-2">
					<h1 className="text-6xl font-black tracking-tight text-red-500 font-mono">403</h1>
					<h2 className="text-xl font-bold tracking-tight text-gray-900">
						Truy cập bị từ chối
					</h2>
					<p className="text-sm text-gray-500 leading-relaxed">
						Tài khoản của bạn không có đặc quyền hoặc vai trò (Role) phù hợp để xem nội dung trang này.
					</p>
				</div>

				{/* Hình minh họa chiếc khiên lỗi */}
				<div className="py-4 flex justify-center">
					<div
						className="w-20 h-20 bg-red-50 text-red-600 rounded-2xl flex items-center justify-center text-3xl shadow-inner">
						🛡️
					</div>
				</div>

				{/* Nút hành động */}
				<div className="flex flex-col pt-2">
					<AppButton
						type="button"
						variant="brand"
						onClick={handleGoBack}
						className="w-full"
					>
						Quay lại Đăng nhập
					</AppButton>
				</div>

			</div>
		</div>
	);
};