import axios from "axios";
import {execSync} from "child_process";
import * as fs from "fs";
import * as path from "path";
import {fileURLToPath} from "url";
import * as process from "node:process";

// 🌟 Thay thế __dirname của CommonJS theo chuẩn ES Module / TypeScript hiện đại
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Định nghĩa Interface cấu hình trả về từ Swagger UI để code có Type an toàn
interface SwaggerServiceUrl {
	url: string;
	name: string;
	displayName?: string;
}

// Đọc biến môi trường từ file .env gốc
const GATEWAY_URL = process.env.VITE_API_GATEWAY_URL;
if (!GATEWAY_URL) {
	console.error("❌ Lỗi: Không tìm thấy biến VITE_API_GATEWAY_URL trong môi trường!");
	process.exit(1);
}

const SWAGGER_CONFIG_ENDPOINT = `${GATEWAY_URL}/v3/api-docs/swagger-config`;

// 🌟 ĐƯỜNG DẪN MỚI: Vì file nằm ở 'packages/shared/src', thư mục đích 'generated' nằm ngay bên cạnh
const OUTPUT_BASE_DIR = path.join(__dirname, "generated");

async function generateAllApis(): Promise<void> {
	try {
		console.log(`🔄 Đang kết nối tới API Gateway tại [${GATEWAY_URL}] để lấy cấu hình Swagger...`);

		// Thực hiện gọi API với kiểu dữ liệu định nghĩa sẵn
		const response = await axios.get<{ urls: SwaggerServiceUrl[] }>(SWAGGER_CONFIG_ENDPOINT);
		const services = response.data.urls;

		if (!services || services.length === 0) {
			console.log("⚠️ Không tìm thấy dịch vụ nào được cấu hình trong Swagger UI!");
			return;
		}

		for (const service of services) {
			const fullOpenApiUrl = `${GATEWAY_URL}${service.url}`;
			const folderName = service.name.toLowerCase().replace(/[^a-z0-9]/g, "-");
			const targetFolder = path.join(OUTPUT_BASE_DIR, folderName);

			// Dọn dẹp thư mục cũ để tránh xung đột file thừa
			if (fs.existsSync(targetFolder)) {
				fs.rmSync(targetFolder, {recursive: true, force: true});
			}
			fs.mkdirSync(targetFolder, {recursive: true});

			console.log(`\n⏳ [${service.name.toUpperCase()}] Đang sinh code TypeScript...`);

			// Lệnh thực thi CLI sinh code từ OpenAPI Spec
			const command = `npx @openapitools/openapi-generator-cli generate ` +
				`-i ${fullOpenApiUrl} ` +
				`-g typescript-axios ` +
				`-o "${targetFolder}" ` +
				`--skip-validate-spec`;

			execSync(command, {stdio: "inherit"});
		}

		// 🌟 SỬA ĐOẠN NÀY: Sinh file index.ts dạng Tường Minh (Explicit Named Object Export) để ép IDE gợi ý Auto-Import
		let importsContent = "";
		let exportsContent = "\n// 🌟 EXPORT CÁC HẰNG SỐ OBJECT ĐỂ KÍCH HOẠT AUTO-IMPORT TRÊN IDE\n";

		services.forEach((service: SwaggerServiceUrl) => {
			const folderName = service.name.toLowerCase().replace(/[^a-z0-9]/g, "-");

			// 🌟 1. Cắt bỏ đuôi "-api" ở cuối (Ví dụ: identity-service-api -> identity-service)
			const cleanFolderName = folderName.replace(/-api$/, "");

			// 🌟 2. Biến đổi chuỗi đã sạch thành dạng CamelCase (Ví dụ: identity-service -> identityService)
			const camelCaseName = cleanFolderName.replace(/-([a-z])/g, (_match, group: string) => group.toUpperCase());

			// Tên biến kết quả cuối cùng sẽ là: identityServiceApi
			const apiInstanceName = `${camelCaseName}Api`;

			// Tạo chuỗi import từ folder gốc ban đầu (vẫn phải giữ folderName đúng tên thư mục)
			importsContent += `import * as ${camelCaseName} from './${folderName}';\n`;

			// Tạo chuỗi export hằng số tường minh
			exportsContent += `export const ${apiInstanceName} = ${camelCaseName};\n`;
		});

		// Gộp nội dung và ghi vào file index.ts
		const finalIndexContent = `${importsContent}${exportsContent}`;
		fs.writeFileSync(path.join(OUTPUT_BASE_DIR, "index.ts"), finalIndexContent);

		console.log("\n✅ Đã đồng bộ thành công toàn bộ DTO vào packages/shared/src/generated!");
	} catch (error: any) {
		console.error("\n❌ Thất bại khi sinh code tự động:", error.message);
	}
}

(async () => {
	await generateAllApis();
})();