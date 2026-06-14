import React from "react";

interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
	label: string;
}

export const AppInput = ({label, ...props}: FormInputProps) => {
	return (
		<div className="flex flex-col gap-1.5 w-full">
			<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">
				{label}
			</label>
			<div className="relative transition-all duration-100 ease-in-out">
				<input
					{...props}
					className="w-full rounded-2xl border-2 border-slate-200 bg-[#f5f7fa] px-4 py-3 text-sm font-medium text-slate-700 outline-none transition-all duration-75
						shadow-[0_4px_0_#e2e8f0]
						focus:bg-white focus:border-slate-400 focus:translate-y-0.5 focus:shadow-[0_2px_0_#cbd5e1]
						disabled:bg-gray-100 disabled:text-gray-400 disabled:shadow-none disabled:translate-y-0"
				/>
			</div>
		</div>
	);
};