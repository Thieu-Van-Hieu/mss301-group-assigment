import {UserInfo} from "@/types";
import {setupApiAuthBridge} from "@repo/api/config/apiClient";
import {authApiClient} from "@repo/api";

let _memoryToken: string | null = null;
let _onTokenChangeCallback: ((token: string | null, user: UserInfo | null) => void) | null = null;

export const getAccessTokenFromMemory = (): string | null => _memoryToken;

export const registerStoreListener = (callback: (token: string | null, user: UserInfo | null) => void) => {
	_onTokenChangeCallback = callback;
	return () => {
		_onTokenChangeCallback = null;
	};
};

export const updateAccessTokenGlobal = (token: string | null) => {
	_memoryToken = token;
	const decodedUser = token ? parseJwt(token) : null;

	if (_onTokenChangeCallback) {
		_onTokenChangeCallback(token, decodedUser);
	}
};

// --- THỰC HIỆN BẮT TAY (BRIDGE) SỬ DỤNG CHUNG APICLIENT ---
setupApiAuthBridge({
	/** Cung cấp hàm đọc Token từ RAM cho Request Interceptor của api package */
	getToken: () => getAccessTokenFromMemory(),

	/** * Sử dụng chung apiClient và đính kèm cấu hình cờ `_isRefreshRequest`
	 * để báo hiệu cho Interceptor xử lý riêng biệt biệt lập, cắt đứt hoàn toàn vòng lặp vô hạn.
	 */
	callRefresh: () => {
		return authApiClient.refreshToken(undefined, {
			_isRefreshRequest: true
		});
	},

	/** Đăng ký callback xử lý lưu trữ và đồng bộ lại hệ thống khi api package refresh thành công */
	onRefreshSuccess: (newToken) => {
		updateAccessTokenGlobal(newToken);
	}
});

export const parseJwt = (token: string): UserInfo | null => {
	try {
		const base64Url = token.split(".")[1];
		if (!base64Url) return null;

		const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
		const jsonPayload = decodeURIComponent(
			atob(base64)
				.split("")
				.map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
				.join("")
		);

		const payload = JSON.parse(jsonPayload);

		return {
			id: payload.sub || payload.id,
			email: payload.email,
			fullName: payload.fullName || payload.name,
			phoneNumber: payload.phoneNumber || payload.phone_number,
			roles: payload.roles || payload.realm_access?.roles || [],
			expiresIn: payload.exp
		};
	} catch (e) {
		console.error("❌ Lỗi giải mã JWT:", e);
		return null;
	}
};