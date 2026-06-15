export const AuthCallbackPage = () => {
	return (
		<div className="min-h-screen bg-bg-main flex flex-col items-center justify-center space-y-4">
			{/* Hiệu ứng loading quay tròn giả lập Duolingo/FoodHub của bạn */}
			<div className="w-12 h-12 border-4 border-brand border-t-transparent rounded-full animate-spin"></div>
			<p className="text-gray-500 font-bold tracking-tight animate-pulse">
				Đang đồng bộ quyền truy cập hệ thống...
			</p>
		</div>
	);
};