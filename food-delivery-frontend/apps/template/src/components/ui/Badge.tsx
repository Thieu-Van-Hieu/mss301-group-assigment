import React from 'react';

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
	variant?: 'orange' | 'sky' | 'rose' | 'emerald' | 'slate';
}

export const Badge: React.FC<BadgeProps> = ({variant = 'orange', children, className = '', ...props}) => {
	const variantClasses = {
		orange: 'bg-orange-100 text-orange-600',
		sky: 'bg-sky-100 text-sky-600',
		rose: 'bg-rose-100 text-rose-600',
		emerald: 'bg-emerald-100 text-emerald-700',
		slate: 'bg-slate-100 text-slate-600',
	};

	return (
		<span
			className={`px-3 py-1 rounded-xl text-xs font-bold ${variantClasses[variant]} ${className}`}
			{...props}
		>
      {children}
    </span>
	);
};