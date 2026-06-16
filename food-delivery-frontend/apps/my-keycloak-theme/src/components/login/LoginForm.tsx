import {useState} from "react";
import type {PageProps} from "keycloakify/login/pages/PageProps";
import type {KcContext} from "../../login/KcContext.ts";
import type {I18n} from "../../login/i18n.ts";
import {AppButton} from "../common/AppButton.tsx";
import {AppInput} from "../common/AppInput.tsx";

type LoginFormProps = PageProps<Extract<KcContext, { pageId: "login.ftl" }>, I18n>;

export const LoginForm = (props: LoginFormProps) => {
	const {kcContext} = props;
	const {url, login, realm, message} = kcContext;

	const [isLoading, setIsLoading] = useState<boolean>(false);

	const handleSubmit = () => {
		setIsLoading(true);
	};

	return (
		<div className="w-full max-w-md mx-auto space-y-6">
			<form
				action={url.loginAction}
				method="post"
				encType="application/x-www-form-urlencoded"
				onSubmit={handleSubmit}
				className="space-y-5"
			>
				<input
					type="hidden"
					id="id-hidden-input"
					name="credentialId"
					value={""}
				/>

				<AppInput
					id="username"
					label="Tài khoản hoặc Email"
					type="text"
					name="username"
					required
					defaultValue={login.username || ""}
					placeholder="name@example.com"
				/>

				<div className="space-y-2">
					<AppInput
						id="password"
						label="Mật khẩu"
						type="password"
						name="password"
						required
						placeholder="••••••••"
					/>
				</div>

				<div className="flex items-center justify-between pt-1">
					{realm.rememberMe && (
						<input
							hidden={true}
							type="checkbox"
							id="rememberMe"
							name="rememberMe"
							defaultChecked={!!login.rememberMe}
							className="w-4 h-4 rounded text-brand border-gray-300 focus:ring-brand"
						/>
					)}

					{realm?.resetPasswordAllowed && (
						<a href={url.loginResetCredentialsUrl}
						   className="text-sm font-semibold text-brand hover:underline ml-auto">
							Quên mật khẩu?
						</a>
					)}
				</div>

				{/* Hiển thị lỗi hệ thống từ Keycloak */}
				{message !== undefined && message.type === "error" && (
					<div className="p-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg font-medium">
						{message.summary}
					</div>
				)}

				<div className="pt-2">
					<AppButton
						type="submit"
						name="login"
						id="kc-login"
						variant="brand"
						isLoading={isLoading}
						className="w-full py-3 font-bold text-white bg-brand rounded-xl"
					>
						Đăng Nhập
					</AppButton>
				</div>
			</form>

			{realm?.registrationAllowed && (
				<div className="text-center text-sm text-gray-600 pt-4 border-t border-gray-100">
					Chưa có tài khoản?{" "}
					<a
						href={import.meta.env.VITE_MAIN_APP_REGISTER_URL || url.registrationUrl}
						className="font-medium text-brand hover:underline"
					>
						Đăng ký ngay
					</a>
				</div>
			)}
		</div>
	);
};