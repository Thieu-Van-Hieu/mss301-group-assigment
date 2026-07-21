import React from 'react';
import {Button} from '../ui/Button';
import {Avatar} from '../ui/Avatar';

export const GeneralSection: React.FC = () => {
	return (
		<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
			{/* Buttons */}
			<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-4">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Button Variants & States</h4>
				<div className="space-y-3">
					<Button variant="brand" fullWidth>Brand Primary</Button>
					<Button variant="secondary" fullWidth>Slate Secondary</Button>
					<Button variant="success" fullWidth>Green Success</Button>
					<Button variant="destructive" fullWidth>
						<i className="fa-solid fa-trash mr-2"></i> Destructive
					</Button>
					<Button variant="brand" loading fullWidth>Loading</Button>
					<Button variant="ghost" fullWidth>Ghost Button</Button>
				</div>
			</div>

			{/* Typography */}
			<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-3">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Typography / Headings</h4>
				<h1 className="text-2xl font-black text-slate-800">Heading 1 (24px Black)</h1>
				<h2 className="text-xl font-extrabold text-slate-800">Heading 2 (20px Extrabold)</h2>
				<h3 className="text-lg font-bold text-slate-800">Heading 3 (18px Bold)</h3>
				<p className="text-sm text-slate-600 font-medium">
					Paragraph: Văn bản chuẩn dùng cho nội dung hiển thị thông thường trên ứng dụng.
				</p>
				<blockquote
					className="p-3 bg-slate-50 border-l-4 border-orange-500 rounded-r-xl text-xs font-bold text-slate-600">
					Blockquote: Trích dẫn quan trọng hoặc ghi chú nổi bật.
				</blockquote>
			</div>

			{/* Icons & Links */}
			<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-4">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Icons & Styled Links</h4>
				<div className="flex gap-3">
					<Avatar variant="orange" size="md">
						<i className="fa-solid fa-heart"></i>
					</Avatar>
					<Avatar variant="emerald" size="md">
						<i className="fa-solid fa-check"></i>
					</Avatar>
					<Avatar variant="sky" size="md">
						<i className="fa-solid fa-star"></i>
					</Avatar>
				</div>
				<div className="space-y-2 pt-2">
					<a href="#"
					   className="inline-flex items-center text-sm font-bold text-orange-500 hover:text-orange-600 border-b-2 border-orange-500 hover:border-orange-600 transition-all">
						Tactile Action Link
						<i className="fa-solid fa-arrow-right ml-1 text-xs"></i>
					</a>
				</div>
			</div>
		</div>
	);
};