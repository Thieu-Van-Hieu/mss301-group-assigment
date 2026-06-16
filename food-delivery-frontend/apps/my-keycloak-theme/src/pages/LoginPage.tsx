import type {PageProps} from "keycloakify/login/pages/PageProps";
import {LoginForm} from "../components/login/LoginForm.tsx";
import type {KcContext} from "../login/KcContext.ts";
import type {I18n} from "../login/i18n.ts";
import {Toaster} from "sonner";

type LoginPageProps = PageProps<Extract<KcContext, { pageId: "login.ftl" }>, I18n>;

export const LoginPage = (props: LoginPageProps) => {
	return (
		/* 1. Layout tràn viền làm chủ hoàn toàn không gian */
		<>
			<div
				className="fixed inset-0 z-50 grid min-h-screen grid-cols-1 lg:grid-cols-[4.5fr_5.5fr] bg-bg-main overflow-y-auto">

				{/* 🍔 BÊN TRÁI: Khối thương hiệu */}
				<div
					className="hidden lg:flex bg-brand p-12 flex-col justify-between relative overflow-hidden select-none">
					<div
						className="absolute -top-20 -left-20 w-80 h-80 bg-brand-dark rounded-full opacity-50 blur-3xl"></div>
					<div
						className="absolute -bottom-40 -right-20 w-96 h-96 bg-brand-light rounded-full opacity-10 blur-2xl"></div>

					<div
						className="flex items-center gap-2 text-white font-black text-2xl tracking-wider relative z-10">
						<span>🍔</span> FOOD DELIVERY HUB
					</div>

					<div className="text-white relative z-10 my-auto py-10">
						<h2 className="text-4xl xl:text-5xl font-black leading-tight mb-4">
							Một tài khoản.<br/>Mở khóa mọi đặc quyền.
						</h2>
						<p className="text-white/80 text-base xl:text-lg font-medium max-w-sm leading-relaxed">
							Hệ thống xác thực tập trung dành cho Khách hàng, Đối tác nhà hàng và Tài xế công nghệ.
						</p>
					</div>

					<div className="text-white/40 text-xs relative z-10">
						© 2026 Food Delivery Ecosystem. All rights reserved.
					</div>
				</div>

				{/* 🚪 BÊN PHẢI: Khối chứa Form Đăng nhập */}
				<div className="w-full flex items-center justify-center p-6 sm:p-12 md:p-16 lg:p-20">
					<div
						className="w-full max-w-xl bg-white p-8 sm:p-10 rounded-custom-2xl shadow-2xl shadow-gray-200/50 border border-gray-100/80 animate-fade-in">

						<div className="mb-6 text-left">
							<h1 className="text-3xl font-black text-gray-900 tracking-tight">
								Chào mừng trở lại
							</h1>
							<p className="mt-2 text-sm text-gray-500 font-medium">
								Vui lòng đăng nhập tài khoản hệ thống tập trung của bạn.
							</p>
						</div>

						{/* 🌟 VỨT BỎ TEMPLATE: Gọi thẳng LoginForm và chuyển tiếp props xuống. 
					    Bên trong LoginForm đã có thẻ <form action={url.loginAction}> lo liệu việc submit lên Keycloak rồi. */}
						<LoginForm {...props} />

					</div>
				</div>

			</div>
			<Toaster
				position="top-right"
				richColors
				expand={true}
				closeButton={true}
				duration={2000}
			/>
		</>
	);
};