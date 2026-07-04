import React from "react";
import {useNavigate} from "react-router-dom";
import {APP_ROUTES} from "@repo/routes";
import {AppButton} from "../../../components/AppButton.tsx";

export const NotFoundPage = (): React.JSX.Element => {
	const navigate = useNavigate();

	const handleGoHome = () => {
		// Mặc định dẫn thẳng về luồng login/điều phối chính để hệ thống tự phân luồng lại
		APP_ROUTES.AUTH.children.LOGIN.goTo(navigate, {replace: true});
	};

	return (
		<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-6 text-gray-800">
			<div
				className="max-w-md w-full bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center space-y-6">

				{/* Mã lỗi & Icon */}
				<div className="space-y-2">
					<h1 className="text-6xl font-black tracking-tight text-blue-600 font-mono">404</h1>
					<h2 className="text-xl font-bold tracking-tight text-gray-900">
						Trang không tìm thấy
					</h2>
					<p className="text-sm text-gray-500 leading-relaxed">
						Đường dẫn bạn đang truy cập không tồn tại, đã bị xóa hoặc di chuyển sang một phân hệ khác trong
						hệ thống Food Delivery.
					</p>
				</div>

				{/* Hình minh họa kính lúp tìm kiếm */}
				<div className="py-4 flex justify-center">
					<div
						className="w-20 h-20 bg-blue-50 text-blue-600 rounded-2xl flex items-center justify-center text-3xl shadow-inner">
						🔍
					</div>
				</div>

				{/* Nút hành động */}
				<div className="flex flex-col pt-2">
					<AppButton
						type="button"
						variant="brand"
						onClick={handleGoHome}
						className="w-full"
					>
						Về Trang Chủ
					</AppButton>
				</div>

			</div>
		</div>
	);
};