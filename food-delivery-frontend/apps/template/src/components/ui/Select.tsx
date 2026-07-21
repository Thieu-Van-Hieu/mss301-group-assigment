import React from 'react';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
	label?: string;
	error?: string;
	required?: boolean;
	options: Array<{ value: string; label: string }>;
}

export const Select: React.FC<SelectProps> = ({
												  label,
												  error,
												  required = false,
												  options,
												  className = '',
												  ...props
											  }) => {
	return (
		<div className="flex flex-col gap-1.5 w-full">
			{label && (
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">
					{label} {required && <span className="text-rose-500">*</span>}
				</label>
			)}
			<div className="relative">
				<select
					className={`w-full appearance-none rounded-2xl border-2 border-slate-200 bg-[#f5f7fa] px-4 py-3 text-sm font-bold text-slate-700 outline-none transition-all duration-75 shadow-[0_4px_0_#e2e8f0] focus:bg-white focus:border-slate-400 cursor-pointer ${error ? 'border-rose-500 focus:border-rose-500 shadow-[0_4px_0_#f43f5e]' : ''} ${className}`}
					{...props}
				>
					{options.map((opt) => (
						<option key={opt.value} value={opt.value}>
							{opt.label}
						</option>
					))}
				</select>
				<i className="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-xs"></i>
			</div>
			{error && <p className="text-xs font-bold text-rose-500 pl-1">{error}</p>}
		</div>
	);
};