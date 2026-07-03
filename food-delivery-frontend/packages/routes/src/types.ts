import {RouteNode} from "./RouteNode";

/** Định nghĩa kiểu dữ liệu cho các sub-routes lồng nhau */
export type SubRoutesConfig<T> = {
	readonly [K in keyof T]: RouteNode<T[K]>;
};