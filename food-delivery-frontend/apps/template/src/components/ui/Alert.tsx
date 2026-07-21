import React from 'react';

interface AlertProps extends React.HTMLAttributes<HTMLDivElement> {
	type?: 'warning' | 'success' | 'error' | 'info';
	icon?: React.ReactNode;
}

export const Alert: React.FC<AlertProps> = ({
												type = 'warning',
												icon,
												children,
												className = '',
												...props
											}) => {
	const variantClasses = {
		warning: 'bg-amber-50 border-amber-200 text-amber-800 shadow-[0_3px_0_#fde68a]',
		success: 'bg-emerald-50 border-emerald-200 text-emerald-800 shadow-[0_3px_0_#a7f3d0]',
		error: 'bg-rose-50 border-rose-200 text-rose-800 shadow-[0_3px_0_#fecdd3]',
		info: 'bg-sky-50 border-sky-200 text-sky-800 shadow-[0_3px_0_#bae6fd]',
	};

	const defaultIcons = {
		warning: <i className="fa-solid fa-triangle-exclamation text-amber-500 text-lg"></i>,
		success: <i className="fa-solid fa-circle-check text-emerald-500 text-lg"></i>,
		error: <i className="fa-solid fa-circle-xmark text-rose-500 text-lg"></i>,
		info: <i className="fa-solid fa-circle-info text-sky-500 text-lg"></i>,
	};

	return (
		<div
			className={`p-4 rounded-2xl border-2 flex items-center gap-3 text-xs font-bold ${variantClasses[type]} ${className}`}
			{...props}
		>
			{icon || defaultIcons[type]}
			<span>{children}</span>
		</div>
	);
};