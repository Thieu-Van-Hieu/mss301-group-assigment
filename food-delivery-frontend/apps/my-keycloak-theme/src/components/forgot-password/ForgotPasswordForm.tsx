import {useState} from "react";
import type {PageProps} from "keycloakify/login/pages/PageProps";
import type {KcContext} from "../../login/KcContext.ts";
import type {I18n} from "../../login/i18n.ts";
import {AppInput} from "../common/AppInput.tsx";
import {AppButton} from "../common/AppButton.tsx";

type ForgotPasswordFormProps = PageProps<Extract<KcContext, { pageId: "login-reset-password.ftl" }>, I18n>;

export const ForgotPasswordForm = (props: ForgotPasswordFormProps) => {
	const {kcContext} = props;
	const {url, auth, messagesPerField} = kcContext;

	const [isLoading, setIsLoading] = useState<boolean>(false);
	const [username, setUsername] = useState<string>(auth?.attemptedUsername || "");

	const handleSubmit = () => {
		setIsLoading(true);
	};

	return (
		<div className="w-full max-w-md mx-auto space-y-6">
			<form
				action={url.loginAction}
				method="post"
				onSubmit={handleSubmit}
				className="space-y-5"
			>
				<AppInput
					label="Tài khoản hoặc Email của bạn"
					type="text"
					name="username"
					required
					disabled={isLoading}
					value={username}
					onChange={(e) => setUsername(e.target.value)}
					placeholder="name@example.com"
				/>

				{messagesPerField.existsError("username") && (
					<div
						className="p-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg animate-fade-in">
						Không tìm thấy tài khoản hợp lệ với thông tin trên.
					</div>
				)}

				<div className="pt-2">
					<AppButton
						type="submit"
						variant="brand"
						isLoading={isLoading}
						className="w-full"
					>
						Gửi liên kết đặt lại mật khẩu
					</AppButton>
				</div>
			</form>

			<div className="text-center text-sm text-gray-600 pt-4 border-t border-gray-100">
				Nhớ ra mật khẩu rồi?{" "}
				<a
					href={url.loginUrl}
					className="font-medium text-brand hover:underline"
				>
					Quay lại Đăng nhập
				</a>
			</div>
		</div>
	);
};