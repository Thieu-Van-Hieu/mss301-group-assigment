import type {PageProps} from "keycloakify/login/pages/PageProps";
import {KcContext} from "../login/KcContext.ts";
import {I18n} from "../login/i18n.ts";
import {ForgotPasswordForm} from "../components/forgot-password/ForgotPasswordForm.tsx";

type ForgotPasswordPageProps = PageProps<Extract<KcContext, { pageId: "login-reset-password.ftl" }>, I18n>;

export const ForgotPasswordPage = (props: ForgotPasswordPageProps) => {
	return (
		/* 1. Layout tràn viền, cố định không gian 100% giống y hệt LoginPage của phen */
		<div
			className="fixed inset-0 z-50 grid min-h-screen grid-cols-1 lg:grid-cols-[4.5fr_5.5fr] bg-bg-main overflow-y-auto">

			{/* 🍔 BÊN TRÁI: Brand Identity (Giữ nguyên cho đồng bộ hệ thống) */}
			<div className="hidden lg:flex bg-brand p-12 flex-col justify-between relative overflow-hidden select-none">
				<div
					className="absolute -top-20 -left-20 w-80 h-80 bg-brand-dark rounded-full opacity-50 blur-3xl"></div>
				<div
					className="absolute -bottom-40 -right-20 w-96 h-96 bg-brand-light rounded-full opacity-10 blur-2xl"></div>

				<div className="flex items-center gap-2 text-white font-black text-2xl tracking-wider relative z-10">
					<span>🍔</span> FOOD DELIVERY HUB
				</div>

				<div className="text-white relative z-10 my-auto py-10">
					<h2 className="text-4xl xl:text-5xl font-black leading-tight mb-4">
						Khôi phục mật khẩu.<br/>Tiếp tục trải nghiệm.
					</h2>
					<p className="text-white/80 text-base xl:text-lg font-medium max-w-sm leading-relaxed">
						Đừng lo lắng, hãy điền thông tin tài khoản và chúng tôi sẽ giúp bạn lấy lại quyền truy cập ngay
						lập tức.
					</p>
				</div>

				<div className="text-white/40 text-xs relative z-10">
					© 2026 Food Delivery Ecosystem. All rights reserved.
				</div>
			</div>

			{/* 🚪 BÊN PHẢI: Khối chứa Form Quên mật khẩu */}
			<div className="w-full flex items-center justify-center p-6 sm:p-12 md:p-16 lg:p-20">
				<div
					className="w-full max-w-xl bg-white p-8 sm:p-10 rounded-custom-2xl shadow-2xl shadow-gray-200/50 border border-gray-100/80 animate-fade-in">

					<div className="mb-6 text-left">
						<h1 className="text-3xl font-black text-gray-900 tracking-tight">
							Quên mật khẩu?
						</h1>
						<p className="mt-2 text-sm text-gray-500 font-medium">
							Nhập email hoặc tên tài khoản hệ thống tập trung để nhận hướng dẫn khôi phục.
						</p>
					</div>

					{/* 🌟 VỨT BỎ hoàn toàn cặp thẻ <Template>. Gọi thẳng Form và chuyển tiếp props xuống. 
					    Bên trong ForgotPasswordForm đã có thẻ <form action={url.loginAction}> tự xử lý logic submit lên Keycloak Engine. */}
					<ForgotPasswordForm {...props} />

				</div>
			</div>

		</div>
	);
};
