import React, {useState} from "react";
import {toast} from "sonner";
import {authApiClient} from "@repo/api";
import {RoleSelector} from "./RoleSelector.tsx";
import {AppInput} from "../../../../components/AppInput.tsx";
import {AppButton} from "../../../../components/AppButton.tsx";
import {useNavigate} from "react-router-dom";
import type {UserRegisterRequest} from "@repo/api/generated/identity-service-api";
import {APP_ROUTES} from "@repo/routes";

type RegisterRole = "CUSTOMER" | "MERCHANT" | "SHIPPER";

type RegisterFormData = UserRegisterRequest & { confirmPassword?: string };

export const RegisterForm = () => {
	const [role, setRole] = useState<RegisterRole>("CUSTOMER");
	const [isLoading, setIsLoading] = useState<boolean>(false);
	const [formData, setFormData] = useState<RegisterFormData>({
		fullName: "",
		email: "",
		phoneNumber: "",
		password: "",
		confirmPassword: "",
	});
	const navigate = useNavigate();

	const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setFormData({...formData, [e.target.name]: e.target.value});
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();

		if (formData.password !== formData.confirmPassword) {
			toast.error("Mật khẩu và xác nhận mật khẩu không khớp!");
			return;
		}

		const userRegisterRequest: UserRegisterRequest = {
			...formData,
			role: role
		};

		setIsLoading(true);

		try {
			const response = await authApiClient.register(userRegisterRequest);
			if (response.status === 201) {
				const roleName = role === "CUSTOMER" ? "Khách Hàng" : role === "MERCHANT" ? "Đối Tác" : "Tài Xế";
				toast.success(`Tạo tài khoản ${roleName} thành công! Vui lòng đăng nhập qua Keycloak.`);
				APP_ROUTES.AUTH.children.LOGIN.goTo(navigate);
			}
		} catch (err) {
			console.error("Luồng đăng ký thất bại đã được Interceptor xử lý toast dữ liệu:", err);
		} finally {
			setIsLoading(false);
		}
	};

	return (
		<form onSubmit={handleSubmit} className="space-y-5">

			{/* Áp dụng Component chọn Vai trò */}
			<RoleSelector role={role} onChange={setRole} disabled={isLoading}/>

			{/* Họ và Tên */}
			<AppInput
				label="Họ và Tên"
				type="text"
				name="fullName"
				required
				disabled={isLoading}
				value={formData.fullName}
				onChange={handleInputChange}
				placeholder="Nguyễn Văn A"
			/>

			{/* Email & Số điện thoại */}
			<div className="grid grid-cols-2 gap-4">
				<AppInput
					label="Địa chỉ Email"
					type="email"
					name="email"
					required
					disabled={isLoading}
					value={formData.email}
					onChange={handleInputChange}
					placeholder="a@gmail.com"
				/>
				<AppInput
					label="Số điện thoại"
					type="tel"
					name="phoneNumber"
					required
					disabled={isLoading}
					value={formData.phoneNumber}
					onChange={handleInputChange}
					placeholder="0912345678"
				/>
			</div>

			{/* Mật khẩu & Xác nhận mật khẩu */}
			<div className="grid grid-cols-2 gap-4">
				<AppInput
					label="Mật khẩu"
					type="password"
					name="password"
					required
					disabled={isLoading}
					value={formData.password}
					onChange={handleInputChange}
					placeholder="••••••••"
				/>
				<AppInput
					label="Xác nhận mật khẩu"
					type="password"
					name="confirmPassword"
					required
					disabled={isLoading}
					value={formData.confirmPassword}
					onChange={handleInputChange}
					placeholder="••••••••"
				/>
			</div>

			{/* Áp dụng Component AppButton Đăng ký kiểu Generic */}
			<div className="pt-4 flex flex-col">
				<AppButton
					type="submit"
					variant="brand"
					isLoading={isLoading}
					className="w-full"
				>
					Tạo Tài Khoản {role === "CUSTOMER" ? "Khách Hàng" : role === "MERCHANT" ? "Đối Tác" : "Tài Xế"}
				</AppButton>
			</div>

		</form>
	);
};