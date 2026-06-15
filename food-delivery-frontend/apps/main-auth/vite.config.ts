import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
	plugins: [
		react(),
		tailwindcss(),
	],
	server: {
		// Đọc Port động từ file .env ở gốc Monorepo mà Turborepo đã nạp ngầm
		port: Number(import.meta["env"].VITE_PORT_MAIN_AUTH) || 3000,
		strictPort: true, // Nếu bị trùng port thì báo lỗi chứ không tự động nhảy sang port khác
	}
});
