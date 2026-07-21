import React from 'react';

interface Column<T> {
	key: keyof T | string;
	header: string;
	render?: (item: T) => React.ReactNode;
	className?: string;
}

interface TableProps<T> extends React.HTMLAttributes<HTMLDivElement> {
	columns: Column<T>[];
	data: T[];
	rowKey: keyof T;
	onRowClick?: (item: T) => void;
}

export function Table<T extends Record<string, any>>({
														 columns,
														 data,
														 rowKey,
														 onRowClick,
														 className = '',
														 ...props
													 }: TableProps<T>) {
	return (
		<div
			className={`bg-white rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] overflow-hidden ${className}`} {...props}>
			<div className="overflow-x-auto">
				<table className="w-full text-left border-collapse">
					<thead>
					<tr className="bg-slate-50 border-b-2 border-slate-200 text-[11px] font-extrabold text-slate-400 uppercase tracking-wider">
						{columns.map((col, idx) => (
							<th key={idx}
								className={`p-4 ${idx === 0 ? 'pl-6' : ''} ${idx === columns.length - 1 ? 'pr-6' : ''} ${col.className || ''}`}>
								{col.header}
							</th>
						))}
					</tr>
					</thead>
					<tbody className="divide-y-2 divide-slate-100 text-xs font-bold">
					{data.map((item) => (
						<tr
							key={String(item[rowKey])}
							onClick={() => onRowClick?.(item)}
							className={`hover:bg-slate-50 transition-all ${onRowClick ? 'cursor-pointer' : ''}`}
						>
							{columns.map((col, idx) => (
								<td
									key={String(idx)}
									className={`p-4 ${idx === 0 ? 'pl-6' : ''} ${idx === columns.length - 1 ? 'pr-6' : ''} ${col.className || ''}`}
								>
									{col.render ? col.render(item) : item[col.key as keyof T]}
								</td>
							))}
						</tr>
					))}
					</tbody>
				</table>
			</div>
		</div>
	);
}