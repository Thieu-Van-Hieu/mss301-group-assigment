import React from 'react';
import {Breadcrumb} from '../ui/Breadcrumb';
import {Pagination} from '../ui/Pagination';

export const NavigationSection: React.FC = () => {
	const [currentPage, setCurrentPage] = React.useState(1);

	return (
		<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-6">
			<Breadcrumb
				items={[
					{label: 'Trang chủ', href: '#'},
					{label: 'Dự án', href: '#'},
					{label: 'Chi tiết Component', active: true},
				]}
			/>

			<div className="pt-2">
				<Pagination
					currentPage={currentPage}
					totalPages={10}
					onPageChange={setCurrentPage}
				/>
			</div>
		</div>
	);
};