import React from 'react';

interface ModalProps extends React.HTMLAttributes<HTMLDivElement> {
	title?: string;
	onClose?: () => void;
	onConfirm?: () => void;
	confirmText?: string;
	cancelText?: string;
	confirmVariant?: 'brand' | 'destructive';
}

export const Modal: React.FC<ModalProps> = ({
												title,
												onClose,
												onConfirm,
												confirmText = 'Đồng ý',
												cancelText = 'Hủy',
												confirmVariant = 'brand',
												children,
												className = '',
												...props
											}) => {
	return (
		<div
			className={`border-2 border-slate-200 rounded-2xl p-4 bg-slate-50 space-y-3 shadow-[0_3px_0_#e2e8f0] ${className}`}
			{...props}
		>
			<div className="flex justify-between items-center">
				{title && <h5 className="font-extrabold text-sm text-slate-800">{title}</h5>}
				{onClose && (
					<button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition-colors">
						<i className="fa-solid fa-xmark text-lg"></i>
					</button>
				)}
			</div>
			<div className="text-xs text-slate-500 font-medium">{children}</div>
			<div className="flex justify-end gap-2 pt-2">
				<button
					onClick={onClose}
					className="px-4 py-2 rounded-xl text-xs font-bold bg-slate-200 text-slate-700 hover:bg-slate-300 transition-colors"
				>
					{cancelText}
				</button>
				<button
					onClick={onConfirm}
					className={`px-4 py-2 rounded-xl text-xs font-bold text-white shadow-[0_3px_0_${confirmVariant === 'destructive' ? '#be123c' : '#bd2d00'}] ${
						confirmVariant === 'destructive' ? 'bg-rose-500 hover:bg-rose-600' : 'bg-orange-500 hover:bg-orange-600'
					} transition-colors`}
				>
					{confirmText}
				</button>
			</div>
		</div>
	);
};