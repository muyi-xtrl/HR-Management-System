# 企业人事管理系统（HRM）

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.6.13-brightgreen" alt="SpringBoot">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883" alt="Vue3">
  <img src="https://img.shields.io/badge/MySQL-8.0-00758F" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-6.x-DC382D" alt="Redis">
  <img src="https://img.shields.io/badge/JWT-Auth-orange" alt="JWT">
</p>

Spring Boot + Vue 3 前后端分离的人事管理系统。员工、部门、考勤、请假、薪资、RBAC 权限的完整闭环。

> **这个项目的定位**：它是我的 **Java 工程地基**——用来证明我能把一个业务系统按规范分层、把认证授权链路走通、把异常和权限处理收口。**它不是我的主攻方向**，我真正想做的是数据智能与 AI 应用落地。把它放在这里，是因为"能写干净的后端工程"是做 AI 应用的前提，而不是因为人事管理系统本身有多新颖。

**如果你在看我的作品集**：这个项目建议只花 2 分钟看结构和认证实现，把时间留给 [金融风险预警系统](https://github.com/muyi-xtrl/Finance-Warning-System)（数据 + 工程深度）和 [AI Agent](https://github.com/muyi-xtrl/ai-agent)（AI 应用落地）。

---

## 我为什么把它做成这样（设计取舍）

人事管理系统是最容易写成"CRUD 堆砌"的题材。我刻意在三个地方做了额外设计，它们是我面试时真正会讲的部分：

**1. 认证不是"能登录就行"，而是一条完整的防线**

登录链路同时串了四件事：Redis 验证码（校验后立即删除，防重放）→ BCrypt 密码比对 → JWT 签发（携带角色）→ 失败计数锁定。失败 5 次锁定 15 分钟，计数和锁定键都放在 Redis 并设置 TTL，不需要定时任务清理。

这里有个容易忽略的细节：`JwtAuthenticationFilter` 在解析 Token 后会**记录每条请求的操作日志**，但显式跳过了 `/auth/login`——否则登录请求会触发日志写入，而日志写入又依赖认证状态，形成死循环。这个坑是调试时才发现的。

**2. 权限控制落在接口级，而不是只做菜单隐藏**

RBAC 做了两层：前端根据动态菜单渲染可见入口，后端用 `@PreAuthorize` 做接口级校验。前端隐藏只是体验，后端校验才是安全边界——只做前者是这类项目最常见的偷懒。

**3. 全项目统一返回与逻辑删除**

所有接口返回 `Result<T>` 统一封装；所有删除走 MyBatis-Plus 逻辑删除（`deleted` 字段），不做物理删除。前者让前端只需处理一套错误协议，后者保留了人事数据的可追溯性。

---

## 技术栈

**后端**：Spring Boot 2.6.13 · Spring Security · JWT（jjwt）· MyBatis-Plus 3.5.3 · MySQL 8.0 · Redis 6.x · EasyExcel · Kaptcha

**前端**：Vue 3（Composition API）· Element Plus · Vite · Pinia · Axios · ECharts

## 功能范围

- 认证授权：验证码登录、Token 刷新、失败锁定、BCrypt 加密
- 员工管理：多条件分页、工号自动生成、Excel 导出
- 组织与考勤：部门树、打卡、考勤状态自动判定
- 请假与薪资：多级审批流转、薪资生成与查询
- 系统管理：用户管理、动态菜单、RBAC 权限、操作日志

## 项目结构

```
src/main/java/com/example/hrm/
├── common/                    # 公共层：配置、工具类、全局异常
│   ├── config/                # Security / Redis / MyBatisPlus / Cors / Swagger
│   ├── exception/             # BusinessException + GlobalExceptionHandler
│   └── utils/                 # JwtUtils / RedisUtils / Result
└── modules/                   # 业务层：按领域垂直拆分
    ├── user/                  # 认证与用户
    ├── employee/  department/  attendance/
    ├── leave/     salary/      system/    log/
```

`common` 放横切关注点，`modules` 按业务域垂直拆分。新增一个业务模块时不需要改动公共层，这是我刻意保持的边界。

---

## 本地运行

**环境**：JDK 1.8+ · MySQL 8.0+ · Redis 6.x+ · Node.js 16+ · Maven 3.6+

```bash
# 1. 建库（utf8mb4），并执行建表脚本（见下方说明）
# 2. 修改 src/main/resources/application.yml 中的数据库与 Redis 连接
# 3. 启动后端：运行 HrmApplication.java（默认 8080）
# 4. 启动前端
cd frontend
npm install
npm run dev        # 默认 http://localhost:5173
```

> **数据库脚本说明**：初版提交时文档引用了一个并未实际提交的 `hrm_db.sql`，导致按文档无法启动。这个疏漏提醒我把"文档与产物一致性"当回事——目前已改为按 entity 定义自行建表，后续会补上一份可执行的 schema 脚本。

**默认账号**（仅用于本地演示，部署前必须修改）：

| 角色 | 用户名 | 密码 |
| :--- | :--- | :--- |
| 超级管理员 | `admin` | `123456` |
| 人事专员 | `hr` | `123456` |
| 普通员工 | `zhangwei` | `123456` |

**配置安全提示**：`application.yml` 中的数据库、Redis 与 JWT 配置已改为环境变量占位符（如 `${DB_PASSWORD:root}`），冒号后只是本地开发默认值。部署时请通过环境变量注入真实凭据，不要把密钥提交进仓库。

---

## 我知道的局限

写出来不是自我批评，而是划清这个项目的能力边界——面试官问到时我会主动说：

- **业务本身没有难度**：人事管理是成熟题材，价值在于工程规范，不在于业务创新。
- **操作日志写在 Filter 里而非 AOP**：能工作，但横切逻辑侵入了过滤器；更干净的做法是用 `@Aspect` 注解，这是我下一步会重构的点。
- **没有并发与性能场景**：没有秒杀、没有热点数据、没有缓存穿透/击穿的真实压力，Redis 在这里主要承担验证码和计数。

这些局限正是我在 [金融风险预警系统](https://github.com/muyi-xtrl/Finance-Warning-System) 里针对性补的：那个项目有幂等性、索引优化和真实的批处理链路。
