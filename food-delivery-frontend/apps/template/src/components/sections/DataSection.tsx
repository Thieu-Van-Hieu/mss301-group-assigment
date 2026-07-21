import React from 'react';
import {Card} from '../ui/Card';
import {Avatar} from '../ui/Avatar';
import {Accordion} from '../ui/Accordion';
import {Table} from '../ui/Table';
import {Button} from '../ui/Button';
import {Badge} from "../ui/Badge";

export const DataSection: React.FC = () => {
	const tableData = [
		{id: 1, name: 'Nguyễn Văn A', role: 'Frontend Developer', status: 'Active'},
		{id: 2, name: 'Trần Thị B', role: 'Backend Developer', status: 'Inactive'},
		{id: 3, name: 'Lê Văn C', role: 'Fullstack Developer', status: 'Active'},
	];

	const columns = [
		{key: 'name', header: 'Thành viên', className: 'font-extrabold text-slate-800'},
		{key: 'role', header: 'Chức vụ', className: 'text-slate-500'},
		{
			key: 'status',
			header: 'Trạng thái',
			render: (item: any) => (
				<span
					className={`px-2.5 py-1 rounded-lg text-[10px] font-bold ${item.status === 'Active' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
          {item.status}
        </span>
			),
		},
		{
			key: 'actions',
			header: 'Thao tác',
			render: () => (
				<button
					className="px-3 py-1.5 rounded-xl border-2 border-slate-200 bg-white shadow-[0_2px_0_#cbd5e1] hover:bg-slate-100 active:translate-y-0.5 active:shadow-none text-xs font-bold">
					Sửa
				</button>
			),
		},
	];

	return (
		<>
			<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
				<Card
					status="active"
					statusLabel="Active"
					badge="#PRJ-2026"
					footer={<Button variant="brand" fullWidth size="sm">Chi tiết dự án</Button>}
				>
					<h4 className="text-lg font-extrabold text-slate-800">Spring Boot 3D Portal</h4>
					<p className="text-slate-500 text-xs font-medium">
						Hệ thống quản lý tích hợp Spring Security JWT & PayOS Gateway.
					</p>
				</Card>

				<div className="bg-white rounded-3xl border-2 border-slate-200 p-6 shadow-[0_4px_0_#e2e8f0] space-y-4">
					<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Avatars & Status
						Badges</h4>
					<div className="flex items-center gap-3">
						<Avatar variant="orange" size="lg">SE</Avatar>
						<Avatar variant="sky" size="md">JV</Avatar>
						<Avatar variant="slate" size="sm">AI</Avatar>
					</div>
					<div className="flex flex-wrap gap-2 pt-2">
						<Badge variant="orange">Badge Orange</Badge>
						<Badge variant="sky">Badge Sky</Badge>
						<Badge variant="rose">Badge Rose</Badge>
					</div>
				</div>

				<div className="bg-white rounded-3xl border-2 border-slate-200 p-6 shadow-[0_4px_0_#e2e8f0] space-y-3">
					<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Accordion / FAQ</h4>
					<Accordion title="Thư viện hỗ trợ Framework nào?">
						Hỗ trợ đầy đủ ReactJS, VueJS và HTML/Tailwind CSS nguyên bản.
					</Accordion>
					<Accordion title="Có hỗ trợ TypeScript không?">
						Có, tất cả component đều được viết bằng TypeScript với typing đầy đủ.
					</Accordion>
				</div>
			</div>

			<Table
				columns={columns}
				data={tableData}
				rowKey="id"
				className="mt-6"
			/>
		</>
	);
};