import React from "react";

const VARIANT_STYLES = {
	brand: {
		active: "bg-brand top-0 shadow-[0_4px_0_#bd2d00] hover:bg-brand-dark active:top-1 active:shadow-none",
		loading: "bg-brand-dark top-1 shadow-none"
	},
	slate: {
		active: "bg-slate-600 top-0 shadow-[0_4px_0_#334155] hover:bg-slate-700 active:top-1 active:shadow-none",
		loading: "bg-slate-700 top-1 shadow-none"
	},
	green: {
		active: "bg-emerald-500 top-0 shadow-[0_4px_0_#047857] hover:bg-emerald-600 active:top-1 active:shadow-none",
		loading: "bg-emerald-600 top-1 shadow-none"
	}
};

interface AppButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	isLoading?: boolean;
	variant?: keyof typeof VARIANT_STYLES;
	children: React.ReactNode;
}

export const AppButton = ({
							  isLoading = false,
							  variant = "brand",
							  children,
							  className = "",
							  disabled,
							  ...props
						  }: AppButtonProps) => {
	const currentStyle = VARIANT_STYLES[variant];

	return (
		<button
			disabled={disabled || isLoading}
			{...props}
			className={`relative rounded-2xl font-bold text-white text-sm py-3.5 transition-all duration-75 select-none cursor-pointer disabled:cursor-not-allowed ${
				isLoading
					? `${currentStyle.loading} opacity-80`
					: `${currentStyle.active}`
			} ${className}`}
		>
			{/* Khối hiệu ứng Loading nằm tuyệt đối ở tâm nút, không làm thay đổi kích thước ban đầu của nút */}
			{isLoading && (
				<span className="absolute inset-0 flex items-center justify-center gap-2 z-10 animate-fade-in">
					<svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
						<circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
						<path className="opacity-75" fill="currentColor"
							  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
					</svg>
					<span className="text-sm">Đang xử lý...</span>
				</span>
			)}

			{/* Nội dung gốc (Chữ của nút) sẽ ẩn tạm thời khi load nhưng giữ nguyên layout diện tích */}
			<span
				className={`flex items-center justify-center gap-2 transition-opacity duration-100 ${isLoading ? "opacity-0" : "opacity-100"}`}>
				{children}
			</span>
		</button>
	);
};