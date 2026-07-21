import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	variant?: 'brand' | 'secondary' | 'success' | 'destructive' | 'ghost';
	loading?: boolean;
	size?: 'sm' | 'md' | 'lg';
	fullWidth?: boolean;
	children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
												  variant = 'brand',
												  loading = false,
												  size = 'md',
												  fullWidth = false,
												  children,
												  className = '',
												  ...props
											  }) => {
	const baseClasses =
		'relative rounded-2xl font-bold text-white transition-all active:translate-y-1 active:shadow-none disabled:opacity-80 disabled:cursor-not-allowed disabled:translate-y-1 disabled:shadow-none inline-flex items-center justify-center';

	const sizeClasses = {
		sm: 'text-xs py-2 px-3',
		md: 'text-sm py-3 px-5',
		lg: 'text-base py-4 px-6',
	};

	const variantClasses = {
		brand: 'bg-orange-500 shadow-[0_4px_0_#bd2d00] hover:bg-orange-600',
		secondary: 'bg-slate-600 shadow-[0_4px_0_#334155] hover:bg-slate-700',
		success: 'bg-emerald-500 shadow-[0_4px_0_#047857] hover:bg-emerald-600',
		destructive: 'bg-rose-500 shadow-[0_4px_0_#be123c] hover:bg-rose-600',
		ghost: 'bg-white text-slate-700 border-2 border-slate-200 shadow-[0_3px_0_#cbd5e1] hover:bg-slate-50',
	};

	const widthClass = fullWidth ? 'w-full' : '';

	return (
		<button
			className={`${baseClasses} ${sizeClasses[size]} ${variantClasses[variant]} ${widthClass} ${className} cursor-pointer`}
			disabled={loading}
			{...props}
		>
			{loading ? (
				<span className="flex items-center justify-center gap-2">
          <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
            <path className="opacity-75" fill="currentColor"
				  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
          </svg>
          <span>Đang xử lý...</span>
        </span>
			) : (
				children
			)}
		</button>
	);
};