export enum RouteAccessLevel {
	/** 🔴 Bắt buộc phải đăng nhập mới được xem (Dashboard, Profile,...) */
	PROTECTED = "PROTECTED",

	/** 🟡 Chỉ dành cho khách vãng lai chưa đăng nhập (Login, Register). Đăng nhập rồi sẽ bị đá ra. */
	ANONYMOUS_ONLY = "ANONYMOUS_ONLY",

	/** 🟢 Công khai hoàn toàn (Landing Page, Blog, Docs,...), ai cũng xem được. */
	PUBLIC = "PUBLIC"
}