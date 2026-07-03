import {useNavigate, useSearchParams} from "react-router-dom";
import {useEffect, useRef} from "react";
import {useAuth} from "@repo/auth";
import {authApiClient} from "@repo/api";
import type {ExchangeCodeRequest} from "@repo/api/generated/identity-service-api";

/**
 * Trang xử lý phản hồi (Callback Page) sau khi người dùng đăng nhập thành công qua Identity Provider (Keycloak).
 *
 * Thành phần này chịu trách nhiệm:
 * 1. Bóc tách mã `code` từ các tham số truy vấn URL (Query Parameters).
 * 2. Gửi mã `code` xuống dịch vụ Backend để thực hiện quy trình trao đổi lấy cặp mã thông báo JWT.
 * 3. Đồng bộ Access Token nhận được vào bộ nhớ RAM của hệ thống thông qua `useAuth`.
 * 4. Tự động điều hướng người dùng vào vùng quản trị (Dashboard) sau khi xác thực thành công.
 *
 * @returns {React.JSX.Element} Giao diện màn hình chờ hiển thị hiệu ứng xoay (Loading Spinner).
 */
export const AuthCallbackPage = (): React.JSX.Element => {
	const [searchParams] = useSearchParams();
	const navigate = useNavigate();

	// Trích xuất hàm nạp token vào hệ thống quản lý RAM và React UI toàn cục
	const {setAccessToken} = useAuth();

	/**
	 * Cờ useRef dùng để ngăn chặn cơ chế React StrictMode (môi trường Development)
	 * tự động kích hoạt hook useEffect 2 lần liên tiếp.
	 *
	 * Lưu ý bảo mật: Nếu mã 'code' bị gửi lên Backend đổi token 2 lần, Keycloak
	 * sẽ lập tức hủy bỏ và báo lỗi 400/401 do mã code chỉ có giá trị sử dụng một lần duy nhất.
	 */
	const isProcessed = useRef<boolean>(false);

	useEffect(() => {
		const code = searchParams.get("code");

		// Địa chỉ redirect_uri bắt buộc phải khớp 100% từng ký tự với lúc bạn tạo Link Login ban đầu
		const redirectUri = `${window.location.origin}${window.location.pathname}`;

		if (code && !isProcessed.current) {
			isProcessed.current = true; // Đánh dấu đã khóa luồng xử lý

			const exchangeRequest: ExchangeCodeRequest = {
				code: code,
				redirectUri: redirectUri,
			};

			// Gọi API xuống tầng Backend thông qua Axios Instance cô lập của package auth
			authApiClient.exchangeCode(exchangeRequest)
				.then(response => {
					// Backend của bạn đã đính sẵn Refresh Token vào HttpOnly Cookie
					// Lúc này ta chỉ cần lấy Access Token từ Body phản hồi đổ vào RAM hệ thống công khai
					const token = response.data?.accessToken;

					if (token) {
						// Nạp trực tiếp vào RAM cho Axios Interceptor dùng và cập nhật React State cho UI hiển thị
						setAccessToken(token);

						// Chuyển hướng người dùng vào trang Dashboard bằng Router của SPA (giữ nguyên bộ nhớ RAM)
						navigate("/dashboard");
					} else {
						console.error("⚠️ Không tìm thấy thuộc tính accessToken trong phản hồi của máy chủ.");
					}
				})
				.catch((error) => {
					console.error("❌ Lỗi đổi mã Code lấy Token thất bại:");
					if (error.response) {
						console.error("Dữ liệu phản hồi lỗi từ Backend:", error.response.data);
					} else {
						console.error("Chi tiết lỗi kết nối:", error.message);
					}
				});
		} else if (!code) {
			console.warn("⚠️ Không tìm thấy tham số 'code' trên đường dẫn URL.");
		}
	}, [searchParams, navigate, setAccessToken]);

	return (
		<div className="min-h-screen bg-bg-main flex flex-col items-center justify-center space-y-4">
			<div className="w-12 h-12 border-4 border-brand border-t-transparent rounded-full animate-spin"></div>
			<p className="text-gray-500 font-bold tracking-tight animate-pulse">
				Đang đồng bộ quyền truy cập hệ thống...
			</p>
		</div>
	);
};
