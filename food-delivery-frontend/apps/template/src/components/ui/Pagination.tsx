import React from 'react';
import {Button} from './Button';

interface PaginationProps extends React.HTMLAttributes<HTMLDivElement> {
	currentPage: number;
	totalPages: number;
	onPageChange: (page: number) => void;
	siblingCount?: number;
}

export const Pagination: React.FC<PaginationProps> = ({
														  currentPage,
														  totalPages,
														  onPageChange,
														  siblingCount = 1,
														  className = '',
														  ...props
													  }) => {
	const getPageNumbers = () => {
		const totalNumbers = siblingCount * 2 + 3;
		const totalBlocks = totalNumbers + 2;

		if (totalPages <= totalBlocks) {
			return Array.from({length: totalPages}, (_, i) => i + 1);
		}

		const leftSiblingIndex = Math.max(currentPage - siblingCount, 1);
		const rightSiblingIndex = Math.min(currentPage + siblingCount, totalPages);

		const shouldShowLeftDots = leftSiblingIndex > 2;
		const shouldShowRightDots = rightSiblingIndex < totalPages - 1;

		if (!shouldShowLeftDots && shouldShowRightDots) {
			const leftRange = Array.from({length: 3 + 2 * siblingCount}, (_, i) => i + 1);
			return [...leftRange, '...', totalPages];
		}

		if (shouldShowLeftDots && !shouldShowRightDots) {
			const rightRange = Array.from(
				{length: 3 + 2 * siblingCount},
				(_, i) => totalPages - (3 + 2 * siblingCount) + i + 1
			);
			return [1, '...', ...rightRange];
		}

		if (shouldShowLeftDots && shouldShowRightDots) {
			const middleRange = Array.from(
				{length: 2 * siblingCount + 1},
				(_, i) => leftSiblingIndex + i
			);
			return [1, '...', ...middleRange, '...', totalPages];
		}

		return [];
	};

	const pageNumbers = getPageNumbers();

	const handlePrev = () => {
		if (currentPage > 1) onPageChange(currentPage - 1);
	};

	const handleNext = () => {
		if (currentPage < totalPages) onPageChange(currentPage + 1);
	};

	return (
		<div className={`flex items-center gap-2 ${className}`} {...props}>
			{/* Prev button */}
			<Button
				variant="ghost"
				size="sm"
				className="w-9 h-9 !p-0 !shadow-[0_3px_0_#cbd5e1] active:!shadow-none disabled:opacity-50 disabled:!shadow-[0_3px_0_#cbd5e1] hover:bg-slate-50 !text-slate-700 !bg-white"
				disabled={currentPage === 1}
				onClick={handlePrev}
			>
				<i className="fa-solid fa-chevron-left"></i>
			</Button>

			{pageNumbers.map((page, index) => {
				if (page === '...') {
					return (
						<span key={`dots-${index}`} className="text-xs font-bold text-slate-400 px-1">
              …
            </span>
					);
				}

				const isActive = page === currentPage;
				return (
					<Button
						key={page}
						variant={isActive ? 'brand' : 'ghost'}
						size="sm"
						className={`w-9 h-9 !p-0 ${
							isActive
								? '!shadow-[0_3px_0_#bd2d00]'
								: '!shadow-[0_3px_0_#cbd5e1] hover:bg-slate-50 active:!shadow-none !text-slate-700 !bg-white'
						}`}
						onClick={() => onPageChange(page as number)}
					>
						{page}
					</Button>
				);
			})}

			{/* Next button */}
			<Button
				variant="ghost"
				size="sm"
				className="w-9 h-9 !p-0 !shadow-[0_3px_0_#cbd5e1] active:!shadow-none disabled:opacity-50 disabled:!shadow-[0_3px_0_#cbd5e1] hover:bg-slate-50 !text-slate-700 !bg-white"
				disabled={currentPage === totalPages}
				onClick={handleNext}
			>
				<i className="fa-solid fa-chevron-right"></i>
			</Button>
		</div>
	);
};