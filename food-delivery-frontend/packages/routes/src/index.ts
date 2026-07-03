import {RouteAccessLevel} from "./enums";
import {RouteNode} from "./RouteNode";

export const APP_ROUTES = {
	// TODO: Sửa các route bên dưới cho đúng
	ADMIN: new RouteNode("admin", {
		basePath: "http://localhost:3001", // Cổng của App Admin
		rolesAllowed: ["ADMIN"],
		children: {DASHBOARD: new RouteNode("dashboard")}
	}),
	SHIPPER: new RouteNode("shipper", {
		basePath: "http://localhost:3002", // Cổng của App Shipper
		rolesAllowed: ["SHIPPER"],
		children: {DASHBOARD: new RouteNode("dashboard")}
	}),
	WAREHOUSE: new RouteNode("warehouse", {
		basePath: "http://localhost:3003", // Cổng của App Warehouse
		rolesAllowed: ["WAREHOUSE"],
		children: {DASHBOARD: new RouteNode("dashboard")}
	}),
	AUTH: new RouteNode("auth", {
		basePath: "http://localhost:5000",
		accessLevel: RouteAccessLevel.ANONYMOUS_ONLY,
		children: {
			LOGIN: new RouteNode("login"),
			REGISTER: new RouteNode("register"),
			CALLBACK: new RouteNode("callback", {accessLevel: RouteAccessLevel.PUBLIC}),
			LOGOUT: new RouteNode("logout"),
		}
	}),
	ERROR: new RouteNode("/*", {})
}