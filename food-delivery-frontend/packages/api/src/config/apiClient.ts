import axios from "axios";

const getApiBaseUrl = (): string => {
    // 1. Kiểm tra môi trường Node.js / Bundler cũ trước
    if (typeof process !== "undefined" && process.env?.["VITE_API_GATEWAY_URL"]) {
        return process.env["VITE_API_GATEWAY_URL"] as string;
    }

    // 2. Nếu không có, kiểm tra môi trường Vite / ESM hiện đại
    // Dùng gán qua biến trung gian hoặc check typeof để tránh lỗi biên dịch của một số bundler
    const meta = (import.meta as any);
    if (meta && meta.env && meta.env.VITE_API_GATEWAY_URL) {
        return meta.env.VITE_API_GATEWAY_URL;
    }

    // 3. Giá trị mặc định (Fallback)
    return "http://localhost:8080/api/v1";
};

const API_BASE_URL = getApiBaseUrl();

export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            window.location.href = "/login";
        }
        return Promise.reject(error);
    }
);
