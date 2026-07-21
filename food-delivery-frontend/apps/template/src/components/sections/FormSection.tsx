import React from 'react';
import {Input} from '../ui/Input';
import {Textarea} from '../ui/Textarea';
import {Select} from '../ui/Select';
import {Checkbox} from '../ui/Checkbox';
import {Radio} from '../ui/Radio';
import {Switch} from '../ui/Switch';

export const FormSection: React.FC = () => {
	const [otp, setOtp] = React.useState(['4', '2', '9', '']);

	const handleOtpChange = (index: number, value: string) => {
		const newOtp = [...otp];
		newOtp[index] = value;
		setOtp(newOtp);
	};

	return (
		<div
			className="bg-white p-8 rounded-3xl border-2 border-slate-200 shadow-[0_4px_0_#e2e8f0] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
			<Input label="Tên đăng nhập" required placeholder="nguyenvana"/>
			<Input label="Mật khẩu" type="password" value="secret123" icon={<i className="fa-regular fa-eye"></i>}
				   iconPosition="right"/>

			<div className="flex flex-col gap-1.5 w-full">
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">Mã xác thực
					OTP</label>
				<div className="flex gap-2">
					{otp.map((val, idx) => (
						<input
							key={idx}
							type="text"
							maxLength={1}
							value={val}
							placeholder={idx === 3 ? '•' : ''}
							onChange={(e) => handleOtpChange(idx, e.target.value)}
							className="w-12 h-12 text-center text-lg font-black rounded-2xl border-2 border-slate-200 bg-[#f5f7fa] shadow-[0_4px_0_#e2e8f0] focus:bg-white focus:border-orange-500 outline-none"
						/>
					))}
				</div>
			</div>

			<Textarea label="Mô tả dự án" rows={2} placeholder="Nhập ghi chú ngắn..." className="md:col-span-2"/>

			<Select
				label="Chuyên ngành"
				options={[
					{value: 'se', label: 'Software Engineering'},
					{value: 'java', label: 'Java Web Development'},
					{value: 'react', label: 'Frontend ReactJS'},
				]}
			/>

			<div className="flex flex-col gap-3 md:col-span-3 pt-2 border-t-2 border-slate-100">
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">Controls &
					Toggles</label>
				<div className="flex flex-wrap gap-8 items-center">
					<Checkbox label="Ghi nhớ đăng nhập" defaultChecked/>
					<Radio label="Thanh toán PayOS" name="plan" defaultChecked/>
					<Switch label="Bật thông báo"/>
				</div>
			</div>

			<div className="flex flex-col gap-2 md:col-span-2">
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">Slider Giá
					trị (Range)</label>
				<input type="range" className="w-full accent-orange-500 h-2 bg-slate-200 rounded-lg cursor-pointer"/>
			</div>

			<div className="flex flex-col gap-1.5 w-full">
				<label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider pl-1">Tải file tài
					liệu</label>
				<div
					className="border-2 border-dashed border-slate-300 rounded-2xl p-3 bg-slate-50 text-center hover:bg-slate-100 transition-all cursor-pointer shadow-[0_3px_0_#e2e8f0]">
          <span className="text-xs font-bold text-slate-500">
            <i className="fa-solid fa-cloud-arrow-up mr-1 text-orange-500"></i> Upload File
          </span>
				</div>
			</div>
		</div>
	);
};