import React from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
	label?: string;
	error?: string;
	required?: boolean;
	icon?: React.ReactNode;
	iconPosition?: 'left' | 'right';
}

export const Input: React.FC<InputProps> = ({
												label,
												error,
												required = false,
												icon,
												iconPosition = 'left',
												className = '',
												...props
											}) => {
	const inputClasses =
		'w-full rounded-2xl border-2 border-slate-200 bg-[#f5f7fa] px-4 py-3 text-sm font-medium text-slate-700 outline-none transition-all duration-75 shadow-[0_4px_0_#e2e8f0] focus:bg-white focus:border-slate-400 focus:translate-y-0.5 focus:shadow-[0_2px_0_#cbd5e1] disabled:opacity-60 disabled:cursor-not-allowed';

	const errorClasses = error
		? 'border-rose-500 focus:border-rose-500 shadow-[0_4px_0_#f43f5e]'
		: '';

	return (
		<div className="flex flex-col gap-1.5 w-full">
			{label && (
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">
					{label} {required && <span className="text-rose-500">*</span>}
				</label>
			)}
			<div className="relative">
				{icon && iconPosition === 'left' && (
					<span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">{icon}</span>
				)}
				<input
					className={`${inputClasses} ${errorClasses} ${icon && iconPosition === 'left' ? 'pl-10' : ''} ${icon && iconPosition === 'right' ? 'pr-10' : ''} ${className}`}
					{...props}
				/>
				{icon && iconPosition === 'right' && (
					<span className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">{icon}</span>
				)}
			</div>
			{error && <p className="text-xs font-bold text-rose-500 pl-1">{error}</p>}
		</div>
	);
};