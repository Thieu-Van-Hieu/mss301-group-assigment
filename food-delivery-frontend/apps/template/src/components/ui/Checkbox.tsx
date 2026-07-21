import React from 'react';

interface CheckboxProps extends React.InputHTMLAttributes<HTMLInputElement> {
	label?: string;
}

export const Checkbox: React.FC<CheckboxProps> = ({label, className = '', ...props}) => {
	return (
		<label className={`flex items-center gap-2.5 cursor-pointer select-none ${className}`}>
			<input type="checkbox" className="sr-only peer" {...props} />
			<div
				className="w-6 h-6 rounded-xl border-2 border-slate-300 bg-white shadow-[0_3px_0_#cbd5e1] peer-checked:bg-orange-500 peer-checked:border-orange-600 peer-checked:shadow-[0_3px_0_#bd2d00] transition-all flex items-center justify-center flex-shrink-0">
				<i className="fa-solid fa-check text-white text-xs opacity-0 peer-checked:opacity-100 transition-opacity"></i>
			</div>
			{label && <span className="text-sm font-bold text-slate-700">{label}</span>}
		</label>
	);
};