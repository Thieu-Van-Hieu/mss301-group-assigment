import React from 'react';

interface BreadcrumbItem {
	label: string;
	href?: string;
	active?: boolean;
}

interface BreadcrumbProps extends React.HTMLAttributes<HTMLDivElement> {
	items: BreadcrumbItem[];
	separator?: React.ReactNode;
}

export const Breadcrumb: React.FC<BreadcrumbProps> = ({
														  items,
														  separator = <i
															  className="fa-solid fa-chevron-right text-[10px]"></i>,
														  className = '',
														  ...props
													  }) => {
	return (
		<div className={`flex items-center gap-2 text-xs font-extrabold text-slate-400 ${className}`} {...props}>
			{items.map((item, index) => (
				<React.Fragment key={index}>
					{index > 0 && separator}
					{item.href && !item.active ? (
						<a href={item.href} className="hover:text-orange-500 transition-colors">
							{item.label}
						</a>
					) : (
						<span className={item.active ? 'text-slate-800' : ''}>{item.label}</span>
					)}
				</React.Fragment>
			))}
		</div>
	);
};