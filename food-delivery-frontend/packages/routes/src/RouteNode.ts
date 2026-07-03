// @repo/constants/src/routes/RouteNode.ts
import {SubRoutesConfig} from "./types";
import {RouteAccessLevel} from "./enums";

// Khai báo kiểu dữ liệu cho hàm navigate của react-router-dom để tránh phải phụ thuộc cứng (loose coupling)
export type RouterNavigateFn = (to: string, options?: { replace?: boolean }) => void;

export interface IRouteDetail {
	readonly basePath?: string;
	readonly path: string;
	readonly fullPath: string;
	readonly relativePath: string;
	readonly accessLevel: RouteAccessLevel;
	readonly title?: string;
	readonly rolesAllowed?: string[];
}

export class RouteNode<T = object> implements IRouteDetail {
	readonly basePath?: string;
	readonly relativePath: string;
	readonly accessLevel: RouteAccessLevel;
	readonly title: string;
	readonly rolesAllowed: string[];
	readonly children: SubRoutesConfig<T>;
	private parent: RouteNode<any> | null = null;

	constructor(
		relativePath: string,
		options?: {
			basePath?: string;
			accessLevel?: RouteAccessLevel;
			title?: string;
			rolesAllowed?: string[];
			children?: T;
		}
	) {
		this.relativePath = relativePath;
		this.basePath = options?.basePath ?? "";
		this.title = options?.title ?? "";
		this.rolesAllowed = options?.rolesAllowed ?? [];

		const childNodes: any = {};
		if (options?.children) {
			for (const [key, child] of Object.entries(options.children)) {
				if (child instanceof RouteNode) {
					child.parent = this;
					childNodes[key] = child;
				}
			}
		}
		this.children = childNodes;

		if (options?.accessLevel) {
			this.accessLevel = options.accessLevel;
		} else if (this.parent) {
			this.accessLevel = this.parent.accessLevel;
		} else {
			this.accessLevel = RouteAccessLevel.PROTECTED;
		}
	}

	/**
	 * 🎯 ĐỆ QUY: Bò ngược lên các tầng cha, ông,... để nối chuỗi tạo thành URL tuyệt đối
	 */
	get path(): string {
		const segments: string[] = [];
		let current: RouteNode<any> | null = this;

		while (current !== null) {
			const cleanSegment = current.relativePath.replace(/^\/+|\/+$/g, "");
			if (cleanSegment) {
				segments.unshift(cleanSegment);
			}
			current = current.parent;
		}

		return "/" + segments.join("/");
	}

	/**
	 * Trả về URL đầy đủ bao gồm cả Domain/Port nếu có cấu hình basePath ở gốc cây
	 */
	get fullPath(): string {
		let root: RouteNode<any> = this;
		while (root.parent !== null) {
			root = root.parent;
		}

		const base = root.basePath ? root.basePath.replace(/\/+$/, "") : "";
		return `${base}${this.path}`;
	}

	/**
	 * 🎯 HÀM ĐIỀU PHỐI ĐỊNH TUYẾN THÔNG MINH
	 * Tự động phân biệt giữa việc nhảy Port/Subdomain (Turborepo) và chuyển trang Single Page App.
	 * @param reactNavigateInstance Hàm navigate lấy từ hook useNavigate() của React Router DOM ở tầng View
	 * @param options Cấu hình bổ sung (như replace trang)
	 */
	goTo(reactNavigateInstance: RouterNavigateFn, options?: { replace?: boolean }): void {
		let root: RouteNode<any> = this;
		while (root.parent !== null) {
			root = root.parent;
		}

		const targetBasePath = root.basePath ? root.basePath.trim().replace(/\/+$/, "") : "";

		// 2. 🎯 KIỂM TRA ĐIỀU KIỆN CHUYỂN HOÀN CẢNH (CROSS-APP REDIRECT)
		if (targetBasePath !== "") {
			// Lấy domain + port hiện tại của trình duyệt (ví dụ: "http://localhost:3001")
			const currentOrigin = window.location.origin.replace(/\/+$/, "");

			// Kiểm tra xem basePath đích có khớp với Origin hiện tại hay không
			const isSameApplication = targetBasePath.toLowerCase() === currentOrigin.toLowerCase();

			if (!isSameApplication) {
				// 🚀 KHÁC APP: Tiến hành Hard Redirect nhảy Port / nhảy Subdomain
				if (options?.replace) {
					window.location.replace(this.fullPath);
				} else {
					window.location.href = this.fullPath;
				}
				return; // Ngắt luồng luôn, không chạy xuống navigate nội bộ nữa
			}
		}

		// 🚀 CÙNG APP (Hoặc không cấu hình basePath): Soft Redirect nội bộ bằng React Router để giữ trạng thái mượt mà
		reactNavigateInstance(this.path, options);
	}
}