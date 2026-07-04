import React from "react";
import {useAuth} from "@repo/auth";
import {Navigate} from "react-router-dom";
import {APP_ROUTES} from "@repo/routes";

/**
 * Trang Dashboard / Home Page hiển thị thông tin chi tiết của người dùng hiện tại.
 * Bảo vệ nghiêm ngặt: Nếu chưa đăng nhập (user === null), tự động đẩy ngược về trang /login.
 *
 * @returns {React.JSX.Element} Giao diện thông tin tài khoản người dùng.
 */
export const DashboardPage = (): React.JSX.Element => {
	const {user, accessToken, logout, isInitializing} = useAuth();

	// 1. Nếu hệ thống đang chạy Silent Refresh ngầm (F5 lấy lại token từ Cookie), hiển thị màn hình chờ
	if (isInitializing) {
		return (
			<div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center space-y-4">
				<div
					className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
				<p className="text-gray-500 font-medium animate-pulse">Đang kiểm tra phiên đăng nhập...</p>
			</div>
		);
	}

	// 2. CHẶN BẢO MẬT: Nếu không có user (chưa đăng nhập), chuyển hướng ngay lập tức về trang login
	if (!user) {
		return <Navigate to={APP_ROUTES.AUTH.children.LOGIN.path} replace/>;
	}

	// 3. Tính toán thời gian hết hạn của Token để hiển thị ra UI
	const expiryDate = new Date(user.expiresIn * 1000).toLocaleString("vi-VN");

	return (
		<div className="min-h-screen bg-gray-50 text-gray-800 p-6 md:p-12">
			<div className="max-w-4xl mx-auto space-y-6">

				{/* --- HEADER --- */}
				<div
					className="flex flex-col sm:flex-row sm:items-center sm:justify-between bg-white p-6 rounded-2xl shadow-sm border border-gray-100 gap-4">
					<div className="flex items-center space-x-4">
						<div
							className="w-16 h-16 bg-blue-100 text-blue-600 font-bold text-2xl flex items-center justify-center rounded-2xl">
							{user.fullName ? user.fullName.charAt(0).toUpperCase() : "U"}
						</div>
						<div>
							<h1 className="text-xl font-bold tracking-tight text-gray-900">
								Xin chào, {user.fullName || "Người dùng"}!
							</h1>
							<p className="text-sm text-gray-500">{user.email}</p>
						</div>
					</div>

					<button
						onClick={logout}
						className="cursor-pointer px-5 py-2.5 bg-red-50 hover:bg-red-100 text-red-600 font-semibold rounded-xl text-sm transition-colors duration-200 self-start sm:self-center"
					>
						Đăng xuất tài khoản
					</button>
				</div>

				{/* --- MAIN CONTENT --- */}
				<div className="grid grid-cols-1 md:grid-cols-3 gap-6">

					{/* Cột trái: Chi tiết tài khoản */}
					<div className="md:col-span-2 bg-white p-6 rounded-2xl shadow-sm border border-gray-100 space-y-4">
						<h2 className="text-lg font-bold text-gray-900 border-b border-gray-100 pb-3">
							Thông tin cá nhân
						</h2>

						<div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
							<div>
								<span className="text-gray-400 block mb-1">Mã định danh (User ID)</span>
								<code
									className="text-xs bg-gray-100 px-2 py-1 rounded text-gray-700 font-mono block break-all">
									{user.id}
								</code>
							</div>
							<div>
								<span className="text-gray-400 block mb-1">Số điện thoại</span>
								<span className="font-medium text-gray-900 block">
									{user.phoneNumber || "Chưa cập nhật"}
								</span>
							</div>
							<div className="sm:col-span-2">
								<span className="text-gray-400 block mb-1">Thời gian hết hạn phiên (Token Exp)</span>
								<span className="font-medium text-amber-600 block">
									{expiryDate}
								</span>
							</div>
						</div>
					</div>

					{/* Cột phải: Danh sách Roles */}
					<div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 space-y-4">
						<h2 className="text-lg font-bold text-gray-900 border-b border-gray-100 pb-3">
							Vai trò hệ thống (Roles)
						</h2>

						<div className="flex flex-wrap gap-2">
							{user.roles && user.roles.length > 0 ? (
								user.roles.map((role: string, idx: number) => (
									<span
										key={idx}
										className="px-3 py-1 bg-green-50 text-green-700 text-xs font-bold rounded-lg border border-green-100 uppercase tracking-wider"
									>
										{role}
									</span>
								))
							) : (
								<span className="text-sm text-gray-400 italic">Không có vai trò nào</span>
							)}
						</div>
					</div>
				</div>

				{/* --- DEBUG ZONE (Tùy chọn: Để DEV xem chuỗi JWT thực tế trong RAM) --- */}
				<div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 space-y-3">
					<h2 className="text-sm font-bold text-gray-400 uppercase tracking-wider">
						Dành cho lập trình viên (Raw Access Token)
					</h2>
					<div
						className="bg-gray-900 text-green-400 p-4 rounded-xl text-xs font-mono overflow-x-auto max-h-32 whitespace-pre-wrap break-all">
						{accessToken || "Không tìm thấy token trong RAM"}
					</div>
				</div>

			</div>
		</div>
	);
};