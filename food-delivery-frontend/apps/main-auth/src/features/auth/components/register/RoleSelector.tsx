type RegisterRole = "CUSTOMER" | "MERCHANT" | "SHIPPER";

interface RoleSelectorProps {
	role: RegisterRole;
	onChange: (role: RegisterRole) => void;
	disabled?: boolean;
}

export const RoleSelector = ({role, onChange, disabled}: RoleSelectorProps) => {
	return (
		<div className="relative mb-6 grid grid-cols-3 gap-0 rounded-2xl bg-gray-100 p-1 user-select-none">
			{/* Khung nền Slider chuyển động */}
			<div
				className="absolute top-1 bottom-1 left-1 rounded-xl bg-white shadow-sm transition-all duration-300 ease-out"
				style={{
					width: "calc(33.3333% - 4px)",
					transform: `translateX(${role === "CUSTOMER" ? "0%" : role === "MERCHANT" ? "100%" : "200%"})`
				}}
			/>

			{(["CUSTOMER", "MERCHANT", "SHIPPER"] as RegisterRole[]).map((r) => (
				<button
					key={r}
					type="button"
					disabled={disabled}
					onClick={() => onChange(r)}
					className={`relative z-10 w-full rounded-xl py-2.5 text-xs font-bold transition-colors duration-200 cursor-pointer disabled:cursor-not-allowed ${
						role === r ? "text-brand" : "text-gray-500 hover:text-gray-900"
					}`}
				>
					{r === "CUSTOMER" ? "Khách Hàng" : r === "MERCHANT" ? "Đối Tác Quán" : "Tài Xế"}
				</button>
			))}
		</div>
	);
};