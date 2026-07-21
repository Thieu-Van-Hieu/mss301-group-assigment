import React from 'react';

interface ProgressBarProps extends React.HTMLAttributes<HTMLDivElement> {
	value: number;
	max?: number;
	label?: string;
	showPercentage?: boolean;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({
															value,
															max = 100,
															label,
															showPercentage = true,
															className = '',
															...props
														}) => {
	const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

	return (
		<div className={`space-y-1 ${className}`} {...props}>
			<div className="flex justify-between text-xs font-bold text-slate-600">
				{label && <span>{label}</span>}
				{showPercentage && <span>{Math.round(percentage)}%</span>}
			</div>
			<div
				className="w-full bg-slate-200 rounded-full h-3 border-2 border-slate-300 shadow-inner overflow-hidden">
				<div
					className="bg-orange-500 h-full rounded-full transition-all duration-300"
					style={{width: `${percentage}%`}}
				></div>
			</div>
		</div>
	);
};