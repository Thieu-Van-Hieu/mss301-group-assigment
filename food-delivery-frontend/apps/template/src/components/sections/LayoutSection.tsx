import React from 'react';

export const LayoutSection: React.FC = () => {
	return (
		<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-4">
			<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Dividers & Scroll Areas</h4>

			<div className="relative flex py-2 items-center">
				<div className="flex-grow border-t-2 border-slate-200"></div>
				<span className="flex-shrink mx-4 text-xs font-bold text-slate-400 uppercase">Hoặc đăng nhập bằng</span>
				<div className="flex-grow border-t-2 border-slate-200"></div>
			</div>

			<div
				className="h-28 overflow-y-auto border-2 border-slate-200 rounded-2xl p-3 bg-slate-50 text-xs font-medium text-slate-600 space-y-2 shadow-[0_3px_0_#e2e8f0] custom-scroll">
				<p>Đoạn văn bản 1 trong vùng cuộn tự do (Scroll Area Custom Style)...</p>
				<p>Đoạn văn bản 2: Hệ thống cuộn hỗ trợ hiển thị danh sách dài mà không làm thay đổi bố cục tổng
					thể.</p>
				<p>Đoạn văn bản 3: Cuộn xuống dưới để xem thêm các thông tin nội quy ứng dụng.</p>
				<p>Đoạn văn bản 4: Cuộn tiếp để thấy thanh cuộn tự động xuất hiện.</p>
			</div>
		</div>
	);
};