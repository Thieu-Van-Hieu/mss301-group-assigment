// menu.js
const { spawn } = require("child_process");
const readline = require("readline");

// ==============================================================================
// CẤU HÌNH DANH SÁCH SERVICES & HÀNH ĐỘNG
// ==============================================================================
const BACKEND_SERVICES = [
  "config-server",
  "eureka-server",
  "api-gateway",
  "identity-service",
  "delivery-service",
];
const INFRA_SERVICES = [
  "keycloak",
  "kafka",
  "postgres",
  "config-processor",
  "prometheus",
  "grafana",
  "loki",
  "promtail",
];

const SERVICE_ACTIONS = [
  {
    name: "⚡ Deploy Full (Build Image + Recreate Container)",
    taskSuffix: "deploy",
  },
  {
    name: "🔄 Chỉ Recreate (Tắt đi bật lại nhanh, giữ nguyên code cũ)",
    taskSuffix: "recreate",
  },
  {
    name: "🛠️  Chỉ Build Image (Không làm gián đoạn container đang chạy)",
    taskSuffix: "build",
  },
  { name: "🔙 Quay lại menu trước", taskSuffix: "back" },
];

const MAIN_OPTIONS = [
  { name: "🚀 Khởi động FULL hệ thống (dev)", type: "direct", task: "dev" },
  {
    name: "⚙️  Quản lý cụm BACKEND Services... (Hỗ trợ chọn nhiều)",
    type: "submenu",
    target: "backend",
  },
  {
    name: "🐳 Quản lý cụm INFRA (Hạ tầng)... (Hỗ trợ chọn nhiều)",
    type: "submenu",
    target: "infra",
  },
  {
    name: "💻 Khởi động Frontend Dev Server",
    type: "direct",
    task: "dev-frontend",
  },
  {
    name: "📝 Sinh code API Client cho Frontend",
    type: "direct",
    task: "api-generator",
  },
  {
    name: "🔑 Cấu hình Keycloak (Export/Theme)...",
    type: "submenu",
    target: "keycloak",
  },
  { name: "🛑 Dừng & Xóa (Down/Clean)...", type: "submenu", target: "system" },
];

const KEYCLOAK_OPTIONS = [
  { name: "🚀 Build và áp dụng Theme mới vào Container", task: "deploy-theme" },
  { name: "🛠️  Chỉ Build Theme Keycloak", task: "build-theme" },
  { name: "🔑 Export cấu hình Realm & Users", task: "export-keycloak" },
  { name: "🔙 Quay lại menu chính", task: "back" },
];

const SYSTEM_OPTIONS = [
  { name: "🛑 Dừng toàn bộ hệ thống (down)", task: "down" },
  { name: "🔄 Tắt đi và Khởi động lại (down-dev)", task: "down-dev" },
  {
    name: "🧹 XÓA SẠCH DỮ LIỆU & Khởi động lại (clean-dev)",
    task: "clean-dev",
  },
  { name: "🔙 Quay lại menu chính", task: "back" },
];

let currentIndex = 0;
let selectedItems = new Set();
let currentMenuScope = "main"; // Theo dõi vị trí menu hiện tại để xử lý phím ESC
let currentSubmenuTarget = "";

function showMenu({ title, list, isMultiSelect = false, callback, onEsc }) {
  readline.emitKeypressEvents(process.stdin);
  if (process.stdin.isTTY) process.stdin.setRawMode(true);

  const render = () => {
    console.clear();
    console.log(`\x1b[36m=== ${title} ===\x1b[0m`);
    let instruction = `(Di chuyển: ⬆️ ⬇️  hoặc j/k | Quay lại/Thoát: Esc)`;
    if (isMultiSelect) {
      instruction = `(Di chuyển: ⬆️ ⬇️  hoặc j/k | Chọn: Space | Tiếp tục: Enter | Quay lại: Esc)`;
    }
    console.log(`\x1b[90m${instruction}\x1b[0m\n`);

    list.forEach((opt, idx) => {
      const isString = typeof opt === "string";
      const displayName = isString ? opt : opt.name;
      const isCurrent = idx === currentIndex;

      let prefix = "   ";
      if (isMultiSelect && isString && opt !== "🔙 Quay lại menu chính") {
        prefix = selectedItems.has(opt) ? " \x1b[32m[x]\x1b[0m " : " [ ] ";
      }

      if (isCurrent) {
        console.log(`\x1b[32m  >\x1b[0m${prefix}\x1b[32m${displayName}\x1b[0m`);
      } else {
        console.log(`   ${prefix}${displayName}`);
      }
    });
  };

  render();

  const handleKey = (str, key) => {
    if (key.ctrl && key.name === "c") {
      process.exit(0);
    }

    // Xử lý phím ESC thông minh
    if (key.name === "escape") {
      process.stdin.off("keypress", handleKey);
      if (process.stdin.isTTY) process.stdin.setRawMode(false);
      return onEsc();
    }

    if (key.name === "up" || str === "k") {
      currentIndex = (currentIndex - 1 + list.length) % list.length;
      render();
    } else if (key.name === "down" || str === "j") {
      currentIndex = (currentIndex + 1) % list.length;
      render();
    } else if (key.name === "space" && isMultiSelect) {
      const item = list[currentIndex];
      if (typeof item === "string" && item !== "🔙 Quay lại menu chính") {
        if (selectedItems.has(item)) selectedItems.delete(item);
        else selectedItems.add(item);
        render();
      }
    } else if (key.name === "return") {
      process.stdin.off("keypress", handleKey);
      if (process.stdin.isTTY) process.stdin.setRawMode(false);
      callback(currentIndex);
    }
  };

  process.stdin.on("keypress", handleKey);
}

function openMainMenu() {
  currentIndex = 0;
  selectedItems.clear();
  currentMenuScope = "main";

  showMenu({
    title: "HỆ THỐNG ĐIỀU KHIỂN FOOD DELIVERY",
    list: MAIN_OPTIONS,
    callback: (index) => {
      const choice = MAIN_OPTIONS[index];
      if (choice.type === "direct") runTask(choice.task);
      else if (choice.type === "submenu") openSubMenu(choice.target);
    },
    onEsc: () => process.exit(0), // Menu chính nhấn ESC là thoát hẳn
  });
}

function openSubMenu(target) {
  currentIndex = 0;
  currentMenuScope = "submenu";
  currentSubmenuTarget = target;

  if (target === "backend" || target === "infra") {
    const isBackend = target === "backend";
    const services = isBackend ? BACKEND_SERVICES : INFRA_SERVICES;
    const title = isBackend
      ? "CHỌN CÁC BACKEND SERVICES"
      : "CHỌN CÁC INFRA SERVICES";
    const menuList = [...services, "🔙 Quay lại menu chính"];

    showMenu({
      title,
      list: menuList,
      isMultiSelect: true,
      callback: (sIdx) => {
        if (sIdx === services.length) return openMainMenu();

        if (selectedItems.size === 0 && sIdx < services.length) {
          selectedItems.add(services[sIdx]);
        }

        if (selectedItems.size === 0) {
          console.log("\x1b[31m⚠️ Bạn chưa chọn service nào cả!\x1b[0m");
          setTimeout(() => openSubMenu(target), 1000);
          return;
        }

        openActionMenu(target);
      },
      onEsc: openMainMenu, // Nhấn ESC quay lại Menu chính
    });
  } else if (target === "keycloak") {
    showMenu({
      title: "QUẢN LÝ KEYCLOAK UTILITIES",
      list: KEYCLOAK_OPTIONS,
      callback: (kIdx) => {
        const choice = KEYCLOAK_OPTIONS[kIdx];
        if (choice.task === "back") return openMainMenu();
        runTask(choice.task);
      },
      onEsc: openMainMenu,
    });
  } else if (target === "system") {
    showMenu({
      title: "DỪNG / DỌN DẸP HỆ THỐNG",
      list: SYSTEM_OPTIONS,
      callback: (sysIdx) => {
        const choice = SYSTEM_OPTIONS[sysIdx];
        if (choice.task === "back") return openMainMenu();
        runTask(choice.task);
      },
      onEsc: openMainMenu,
    });
  }
}

function openActionMenu(type) {
  currentIndex = 0;
  currentMenuScope = "action";
  const servicesArray = Array.from(selectedItems);
  const title = `HÀNH ĐỘNG CHO CỤM: [ ${servicesArray.join(", ").toUpperCase()} ]`;

  showMenu({
    title,
    list: SERVICE_ACTIONS,
    callback: (aIdx) => {
      const action = SERVICE_ACTIONS[aIdx];
      if (action.taskSuffix === "back") return openSubMenu(type);

      const finalTaskName = `${action.taskSuffix}-${type}`;
      const servicesString = servicesArray.join(" ");
      runTask(finalTaskName, { SERVICE: `"${servicesString}"` });
    },
    onEsc: () => openSubMenu(type), // Nhấn ESC quay lại menu chọn service trước đó
  });
}

function runTask(taskName, vars = {}) {
  const args = [taskName];
  Object.entries(vars).forEach(([k, v]) => args.push(`${k}=${v}`));

  console.clear();
  console.log(`\x1b[36m🤖 Đang thực thi: task ${args.join(" ")}\x1b[0m\n`);

  const child = spawn(
    process.platform === "win32" ? "task.exe" : "task",
    args,
    { stdio: "inherit", shell: true },
  );
  child.on("exit", () => {
    console.log(
      `\n\x1b[32m✅ Đã xử lý xong hành động. Bấm Enter để quay lại Menu...\x1b[0m`,
    );
    process.stdin.once("data", () => openMainMenu());
  });
}

// Khởi chạy ứng dụng
openMainMenu();
