import React from 'react';

interface AvatarProps extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;
	size?: 'sm' | 'md' | 'lg' | 'xl';
	variant?: 'orange' | 'sky' | 'slate' | 'emerald' | 'rose';
}

export const Avatar: React.FC<AvatarProps> = ({
												  children,
												  size = 'md',
												  variant = 'orange',
												  className = '',
												  ...props
											  }) => {
	const sizeClasses = {
		sm: 'w-8 h-8 text-xs rounded-xl',
		md: 'w-10 h-10 text-sm rounded-2xl',
		lg: 'w-12 h-12 text-lg rounded-2xl',
		xl: 'w-16 h-16 text-2xl rounded-3xl',
	};

	const variantClasses = {
		orange: 'bg-orange-500 text-white shadow-[0_3px_0_#bd2d00]',
		sky: 'bg-sky-500 text-white shadow-[0_3px_0_#0369a1]',
		slate: 'bg-slate-700 text-white shadow-[0_3px_0_#1e293b]',
		emerald: 'bg-emerald-500 text-white shadow-[0_3px_0_#047857]',
		rose: 'bg-rose-500 text-white shadow-[0_3px_0_#be123c]',
	};

	return (
		<div
			className={`flex items-center justify-center font-black ${sizeClasses[size]} ${variantClasses[variant]} ${className}`}
			{...props}
		>
			{children}
		</div>
	);
};