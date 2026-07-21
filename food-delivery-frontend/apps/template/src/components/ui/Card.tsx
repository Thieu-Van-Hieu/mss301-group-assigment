import React from 'react';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
	status?: 'active' | 'inactive';
	statusLabel?: string;
	badge?: string;
	footer?: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({
											  status,
											  statusLabel,
											  badge,
											  footer,
											  children,
											  className = '',
											  ...props
										  }) => {
	return (
		<div
			className={`bg-white rounded-3xl border-2 border-slate-200 p-6 shadow-[0_4px_0_#e2e8f0] flex flex-col justify-between space-y-4 ${className}`}
			{...props}
		>
			<div className="space-y-2">
				<div className="flex items-center justify-between">
					{status && (
						<span
							className={`px-3 py-1 rounded-xl text-xs font-black uppercase tracking-wider border ${
								status === 'active'
									? 'bg-emerald-100 text-emerald-700 border-emerald-200'
									: 'bg-slate-100 text-slate-500 border-slate-200'
							}`}
						>
              {statusLabel || status}
            </span>
					)}
					{badge && <span className="text-xs font-bold text-slate-400">{badge}</span>}
				</div>
				{children}
			</div>
			{footer && <div className="mt-2">{footer}</div>}
		</div>
	);
};