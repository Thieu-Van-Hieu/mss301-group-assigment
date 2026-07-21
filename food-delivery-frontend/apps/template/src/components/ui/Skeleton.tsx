import React from 'react';

interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
	variant?: 'text' | 'circle' | 'rect' | 'avatar';
	width?: string | number;
	height?: string | number;
	count?: number;
}

export const Skeleton: React.FC<SkeletonProps> = ({
													  variant = 'text',
													  width,
													  height,
													  count = 1,
													  className = '',
													  ...props
												  }) => {
	const baseClasses = 'animate-pulse bg-slate-200 rounded';

	const variantClasses = {
		text: 'h-3 rounded',
		circle: 'rounded-full',
		rect: 'rounded-2xl',
		avatar: 'rounded-2xl h-10 w-10',
	};

	const style: React.CSSProperties = {};
	if (width) style.width = typeof width === 'number' ? `${width}px` : width;
	if (height) style.height = typeof height === 'number' ? `${height}px` : height;
	if (variant === 'text' && !height) style.height = '0.75rem';
	if (variant === 'text' && !width) style.width = '100%';

	const items = Array.from({length: count}, (_, i) => i);

	return (
		<>
			{items.map((i) => (
				<div
					key={i}
					className={`${baseClasses} ${variantClasses[variant]} ${className}`}
					style={style}
					{...props}
				/>
			))}
		</>
	);
};