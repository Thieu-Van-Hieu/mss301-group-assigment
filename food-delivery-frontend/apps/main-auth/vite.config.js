import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// 🌟 Mẹo JSDoc này giúp bạn gõ code đến đâu tự động gợi ý (IntelliSense) đến đó như file .ts
/** @type {import("vite").UserConfig} */
export default defineConfig({
	plugins: [
		tailwindcss(),
		react(),
	],
	server: {
		// Đọc Port động từ file .env ở gốc Monorepo mà Turborepo đã nạp ngầm
		port: Number(process.env.VITE_PORT_MAIN_AUTH) || 3000,
		strictPort: true, // Nếu bị trùng port thì báo lỗi chứ không tự động nhảy sang port khác
	}
});