import React from 'react';

interface SwitchProps extends React.InputHTMLAttributes<HTMLInputElement> {
	label?: string;
}

export const Switch: React.FC<SwitchProps> = ({label, className = '', ...props}) => {
	return (
		<label className={`flex items-center gap-3 cursor-pointer select-none ${className}`}>
			<div
				className="w-12 h-7 bg-slate-200 border-2 border-slate-300 rounded-full shadow-[0_3px_0_#cbd5e1] relative transition-all duration-200 flex-shrink-0">
				<input type="checkbox" className="sr-only peer" {...props} />
				<div
					className="absolute inset-0 bg-emerald-500 border-2 border-emerald-600 rounded-full shadow-[0_3px_0_#047857] opacity-0 peer-checked:opacity-100 transition-opacity duration-200"
					style={{margin: '-2px'}}
				></div>
				<div
					className="w-5 h-5 bg-white rounded-full absolute top-0.5 left-0.5 z-10 transition-transform duration-200 ease-in-out shadow-sm peer-checked:translate-x-[20px]"></div>
			</div>
			{label && <span className="text-sm font-bold text-slate-700">{label}</span>}
		</label>
	);
};