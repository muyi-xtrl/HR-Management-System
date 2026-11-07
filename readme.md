太棒了！从截图来看，你的项目目录结构非常规范，典型的**Spring Boot 前后端分离 + 模块化（多模块聚合）**的标准结构。

我已经帮你写好了一份**专业、高颜值、适合放在 GitHub 上的 README.md**。你可以直接复制下面的内容，粘贴到你根目录的 `README.md` 文件中。

---

# 📄 复制以下内容到你的 README.md

```markdown
# 🏢 企业员工人事管理系统 (HRM)

<p align="center">
  <img src="https://img.shields.io/badge/SpringBoot-2.6.13-brightgreen.svg" alt="SpringBoot">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883.svg" alt="Vue3">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue.svg" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-6.x-red.svg" alt="Redis">
  <img src="https://img.shields.io/badge/JWT-Auth-orange.svg" alt="JWT">
  <img src="https://img.shields.io/badge/License-MIT-lightgrey.svg" alt="License">
</p>

## 📖 项目简介

本项目是一个基于 **Spring Boot + Vue 3** 的前后端分离架构的**企业员工人事管理系统 (HRM)**。

旨在为企业提供数字化的人事管理解决方案，实现员工信息、组织架构、考勤打卡、请假审批、薪资管理及 RBAC 权限体系的标准化线上管控，彻底替代传统人工管理模式，提升人事管理效率。

## ✨ 核心功能

- 🔐 **认证授权**：基于 Spring Security + JWT 的无状态认证，支持验证码登录、Token 自动刷新、登录失败锁定。
- 👥 **员工管理**：员工信息的增删改查、多条件分页搜索、工号自动生成、Excel 导出。
- 🏢 **部门管理**：组织架构的树形展示与维护。
- ⏱️ **考勤管理**：上下班打卡、考勤历史查询、自动化考勤状态判定（正常/早退/异常）。
- 📝 **请假管理**：员工在线申请，HR/管理员多级审批流转。
- 💰 **薪资管理**：薪资生成、发放与个人薪资查询。
- 🛡️ **系统管理**：用户管理、动态菜单渲染、RBAC 角色权限控制、AOP 操作日志记录。

## 🛠️ 技术栈

### 后端技术
- **核心框架**：Spring Boot 2.6.13
- **安全框架**：Spring Security, JWT (jjwt)
- **持久层**：MyBatis-Plus 3.5.3
- **数据库**：MySQL 8.0, Redis 6.x
- **工具库**：Hutool, EasyExcel, Lombok, Kaptcha(验证码)

### 前端技术
- **核心框架**：Vue 3 (Composition API)
- **UI 框架**：Element Plus
- **构建工具**：Vite
- **状态管理**：Pinia
- **网络请求**：Axios
- **图表库**：ECharts

## 📁 项目结构

```text
hrm/
├── frontend/                  # 前端 Vue3 源码
├── src/
│   └── main/
│       ├── java/com/example/hrm/
│       │   ├── common/        # 公共模块（核心配置、工具类、全局异常）
│       │   │   ├── config/    # Security, Redis, MyBatisPlus 等配置
│       │   │   ├── controller/# 公共控制器（验证码等）
│       │   │   ├── exception/ # 全局异常处理
│       │   │   └── utils/     # JwtUtils, RedisUtils, Result 等
│       │   ├── modules/       # 业务模块（高内聚）
│       │   │   ├── user/      # 用户与认证
│       │   │   ├── employee/  # 员工管理
│       │   │   ├── department/# 部门管理
│       │   │   ├── attendance/# 考勤管理
│       │   │   ├── leave/     # 请假管理
│       │   │   ├── salary/    # 薪资管理
│       │   │   ├── system/    # 系统菜单与权限
│       │   │   └── log/       # 操作日志
│       │   └── HrmApplication.java # 启动类
│       └── resources/
│           └── application.yml # 全局配置文件
├── pom.xml                    # Maven 依赖配置
└── README.md                  # 项目说明文档
```

## 🚀 快速开始

### 1. 环境准备
- JDK 1.8+
- MySQL 8.0+
- Redis 6.x+
- Node.js 16+
- Maven 3.6+

### 2. 数据库初始化
1. 创建数据库 `hrm_db` (字符集建议 `utf8mb4`)。
2. 导入根目录下的 `hrm_db.sql` 文件。

### 3. 修改配置
修改 `src/main/resources/application.yml` 文件，配置你的 MySQL 和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hrm_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
  redis:
    host: localhost # 或你的 Redis IP
    port: 6379
```

### 4. 启动后端
在 IDEA 中运行 `HrmApplication.java`，后端默认运行在 `http://localhost:8080`。

### 5. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端默认运行在 `http://localhost:5173`。

## 🔑 默认账号

| 角色 | 用户名 | 密码 |
| :--- | :--- | :--- |
| 超级管理员 | `admin` | `123456` |
| 人事专员 (HR) | `hr` | `123456` |
| 普通员工 | `zhangwei` | `123456` |

## 🖼️ 系统预览

*(建议在这里放 2-3 张系统的核心截图，比如登录页、数据看板、考勤打卡页，会让项目看起来非常专业)*

## 📝 开发规范

- **分层架构**：严格遵循 `Controller -> Service -> Mapper` 的分层规范。
- **统一返回**：所有接口使用 `Result<T>` 类进行统一封装。
- **权限控制**：基于 `@PreAuthorize` 注解进行接口级权限校验。
- **数据删除**：全部采用 MyBatis-Plus 逻辑删除 (`deleted` 字段)。

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源，仅供学习交流使用。
```

---

## 💡 额外建议（为了 GitHub 更好看）：

1. **加一张系统截图**：把你的登录页或者主页截图，上传到 Github 仓库，然后把链接放到 `🖼️ 系统预览` 下面，有图有真相，HR 和面试官一眼就会觉得你做得好。
2. **补充 `.gitignore`**：确保把 `target/`、`node_modules/` 和 `application.yml`（如果不包含敏感信息可保留）加进 `.gitignore`，不然你的 GitHub 会变得很大。
3. **注意配置文件的隐私**：如果你 GitHub 是公开的，建议把 `application.yml` 里面的数据库密码替换成占位符（比如 `123456`），避免泄露服务器真实密码。