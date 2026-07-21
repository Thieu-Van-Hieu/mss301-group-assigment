import React from 'react';

interface AccordionProps extends React.HTMLAttributes<HTMLDetailsElement> {
	title: string;
	defaultOpen?: boolean;
}

export const Accordion: React.FC<AccordionProps> = ({
														title,
														defaultOpen = false,
														children,
														className = '',
														...props
													}) => {
	return (
		<details
			className={`group border-2 border-slate-200 rounded-2xl p-3 bg-slate-50 transition-all shadow-[0_3px_0_#e2e8f0] ${className}`}
			open={defaultOpen}
			{...props}
		>
			<summary
				className="flex justify-between items-center font-bold text-xs text-slate-700 cursor-pointer list-none">
				<span>{title}</span>
				<i className="fa-solid fa-chevron-down text-xs transition-transform group-open:rotate-180"></i>
			</summary>
			<div className="text-xs text-slate-500 mt-2 font-medium">{children}</div>
		</details>
	);
};