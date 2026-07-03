import {RegisterForm} from "../components/register/RegisterForm.tsx";
import {APP_ROUTES} from "@repo/routes";

export const RegisterPage = () => {
	return (
		<div className="grid min-h-screen grid-cols-1 lg:grid-cols-[4.5fr_5.5fr] bg-bg-main">

			{/* 🌟 BÊN TRÁI: Khối hình ảnh / Quảng cáo thương hiệu (Chiếm 45% không gian, gọn gàng hơn) */}
			<div
				className="hidden lg:flex bg-brand p-12 flex-col justify-between relative overflow-hidden select-none">
				{/* Khối decor trừu tượng tạo cảm giác công nghệ */}
				<div
					className="absolute -top-20 -left-20 w-80 h-80 bg-brand-dark rounded-full opacity-50 blur-3xl"></div>
				<div
					className="absolute -bottom-40 -right-20 w-96 h-96 bg-brand-light rounded-full opacity-10 blur-2xl"></div>

				{/* Logo hệ thống tổng */}
				<div className="flex items-center gap-2 text-white font-black text-2xl tracking-wider relative z-10">
					<span>🍔</span> FOOD DELIVERY HUB
				</div>

				{/* Slogan định vị phân hệ */}
				<div className="text-white relative z-10 my-auto py-10">
					<h2 className="text-4xl xl:text-5xl font-black leading-tight mb-4">
						Một tài khoản.<br/>Mở khóa mọi đặc quyền.
					</h2>
					<p className="text-white/80 text-base xl:text-lg font-medium max-w-sm leading-relaxed">
						Hệ thống xác thực tập trung dành cho Khách hàng, Đối tác nhà hàng và Tài xế công nghệ.
					</p>
				</div>

				{/* Footer bản quyền */}
				<div className="text-white/40 text-xs relative z-10">
					© 2026 Food Delivery Ecosystem. All rights reserved.
				</div>
			</div>

			{/* 🌟 BÊN PHẢI: Khối chứa Form Đăng ký (Đã được nới rộng chiếm 55% không gian, cực kỳ thoáng) */}
			<div className="w-full flex items-center justify-center p-6 sm:p-12 md:p-16 lg:p-20">
				{/* Tăng max-w-md (448px) lên max-w-xl (576px) giúp form register có thêm không gian hiển thị */}
				<div
					className="w-full max-w-xl bg-white p-8 sm:p-10 rounded-custom-2xl shadow-2xl shadow-gray-200/50 border border-gray-100/80 animate-fade-in">

					{/* Header của Form */}
					<div className="mb-6 text-left">
						<h1 className="text-3xl font-black text-gray-900 tracking-tight">
							Bắt đầu ngay hôm nay
						</h1>
						<p className="mt-2 text-sm text-gray-500 font-medium">
							Vui lòng chọn vai trò thích hợp để hệ thống phân luồng thiết lập.
						</p>
					</div>

					{/* Form cốt lõi (Giao diện 3D Duolingo đã sửa ở bước trước) */}
					<RegisterForm/>

					{/* Footer chuyển đổi */}
					<div className="mt-8 text-center text-sm text-gray-500 border-t border-gray-100 pt-5">
						Bạn đã có tài khoản?{" "}
						<a
							href={APP_ROUTES.AUTH.children.LOGIN.path}
							className="font-bold text-brand hover:text-brand-dark hover:underline transition-smooth"
						>
							Đăng nhập qua Keycloak
						</a>
					</div>

				</div>
			</div>

		</div>
	);
};