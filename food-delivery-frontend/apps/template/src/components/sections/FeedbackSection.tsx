import React from 'react';
import {Alert} from '../ui/Alert';
import {ProgressBar} from '../ui/ProgressBar';
import {Skeleton} from '../ui/Skeleton';
import {Modal} from '../ui/Modal';

export const FeedbackSection: React.FC = () => {
	const [, setShowModal] = React.useState(true);

	return (
		<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
			<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-3">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Alert Banners</h4>
				<Alert type="warning">Cảnh báo: Vui lòng kiểm tra lại cấu hình kết nối database!</Alert>
				<Alert type="success">Thành công: Đã lưu thông tin tài khoản thành công.</Alert>
				<Alert type="error">Lỗi: Không thể kết nối đến máy chủ.</Alert>
				<Alert type="info">Thông tin: Có bản cập nhật mới cho hệ thống.</Alert>
			</div>

			<div className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] space-y-4">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Progress & Skeleton
					Loading</h4>
				<ProgressBar value={75} label="Tiến độ hoàn thành"/>
				<div className="pt-2 space-y-2">
					<Skeleton variant="text"/>
					<Skeleton variant="text" width="60%"/>
					<div className="flex items-center gap-3">
						<Skeleton variant="avatar"/>
						<div className="flex-1 space-y-2">
							<Skeleton variant="text" width="80%"/>
							<Skeleton variant="text" width="50%"/>
						</div>
					</div>
				</div>
			</div>

			<div
				className="bg-white p-6 rounded-3xl border-2 border-slate-200 shadow-[0_6px_0_#cbd5e1] space-y-4 col-span-1 md:col-span-2">
				<h4 className="text-xs font-black text-slate-400 uppercase tracking-wider">Modal Popup Component</h4>
				<Modal
					title="Xác nhận xóa tài liệu"
					onClose={() => setShowModal(false)}
					onConfirm={() => alert('Đã xóa!')}
					confirmVariant="destructive"
					confirmText="Đồng ý xóa"
				>
					Hành động này sẽ xóa vĩnh viễn tệp tin khỏi hệ thống và không thể phục hồi.
				</Modal>
			</div>
		</div>
	);
};