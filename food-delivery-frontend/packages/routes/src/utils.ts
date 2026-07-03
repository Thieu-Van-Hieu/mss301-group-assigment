// @repo/constants/src/routes/utils.ts
import {RouteNode} from "./RouteNode";
import {APP_ROUTES} from "./index";
import {RouteAccessLevel} from "./enums";

export const extractWhitelistPaths = (routeTree: object): string[] => {
	const paths: string[] = [];

	const traverse = (node: any) => {
		if (node instanceof RouteNode) {
			if (node.accessLevel === RouteAccessLevel.PUBLIC || node.accessLevel === RouteAccessLevel.ANONYMOUS_ONLY) {
				paths.push(node.path);
			}
			if (node.children) {
				Object.values(node.children).forEach(traverse);
			}
		} else if (typeof routeTree === "object") {
			Object.values(node).forEach(traverse);
		}
	};

	traverse(routeTree);
	return paths;
};

// 🎯 Xuất bản Whitelist tự động từ APP_ROUTES
export const AUTH_SILENT_REFRESH_WHITELIST = extractWhitelistPaths(APP_ROUTES);

/**
 * Mảng định nghĩa thứ tự ưu tiên của các Role trong hệ thống.
 * Nếu một User sở hữu nhiều Role cùng lúc (ví dụ vừa là ADMIN vừa là SHIPPER),
 * hệ thống sẽ ưu tiên đưa họ vào Dashboard có quyền cao nhất từ trên xuống dưới.
 */
const ROLE_DASHBOARD_MAPPING = [
	{role: "ADMIN", route: APP_ROUTES.ADMIN.children.DASHBOARD},
	{role: "WAREHOUSE_MANAGER", route: APP_ROUTES.WAREHOUSE.children.DASHBOARD},
	{role: "SHIPPER", route: APP_ROUTES.SHIPPER.children.DASHBOARD},
] as const;

/**
 * Hàm tối ưu tự động tìm kiếm và trả về URL Dashboard chính xác dựa vào danh sách Roles của User.
 * @param userRoles Mảng chứa các role của user đăng nhập thành công (ví dụ: user.roles)
 * @returns Chuỗi URL tuyệt đối (Kèm port nếu ở local, kèm domain nếu ở prod)
 */
export const resolveDashboardUrl = (userRoles: string[] | undefined): string => {
	if (!userRoles || userRoles.length === 0) {
		return "/"; // Trả về trang chủ mặc định nếu không có quyền
	}

	// Chuyển tất cả quyền về chữ hoa để so sánh chính xác tuyệt đối
	const normalizedRoles = userRoles.map(r => r.toUpperCase());

	// Tìm kiếm phân hệ phù hợp dựa trên độ ưu tiên cấu hình sẵn
	const matched = ROLE_DASHBOARD_MAPPING.find(item => normalizedRoles.includes(item.role));

	// Trả về fullPath (bao gồm domain/port phù hợp)
	return matched ? matched.route.fullPath : "/";
};