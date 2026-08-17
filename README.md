# e-commerce 电商后端

一个基于 **Spring Boot 3.3.7 / Java 22** 的电商后端服务，采用 Maven 多模块架构，提供用户、商品、分类、购物车、订单、后台管理及 AI 购物助手等接口。

---

## 技术栈

| 类别 | 技术 |
|------|------|
| Web 框架 | Spring Boot Web（内嵌 Tomcat） |
| 持久层 | MyBatis 3.0.5 + MySQL（HikariCP） |
| 缓存 | Redis（Spring Data Redis + Lettuce） |
| 认证 | JWT（jjwt 0.11.5，HS256）+ BCrypt |
| 工具 | Hutool 5.8.31、Lombok、Snowflake 雪花算法 |
| AI 助手 | Spring AI + DeepSeek（流式返回） |
| 对象存储 | 阿里云 OSS |
| 权限 | Spring AOP（`@AdminOnly` 切面） |

服务端口 `8080`，上下文路径 `/api`。

---

## 模块结构

```
e-commerce (父 pom)
├── e-commerce-pojo      # 实体、DTO、VO
├── e-commerce-common    # 通用配置、拦截器、AOP、异常、工具类、统一返回
└── e-commerce-server    # 启动类、Controller、Service、Mapper
```

---

## 环境要求

- JDK 22+（已在 JDK 26 下验证编译）
- Maven 3.9+（项目自带 `mvnw` Wrapper，可直接使用）
- MySQL 8.x
- Redis 6.x+
- 可访问 DeepSeek API 与阿里云 OSS（用于 AI 助手与图片上传）

---

## 配置说明（环境变量）

所有敏感信息均已改为**环境变量注入**，切勿在代码仓库中提交真实密钥。
运行前请设置以下环境变量：

| 环境变量 | 必填 | 默认值 | 说明 |
|----------|------|--------|------|
| `DB_USERNAME` | 否 | `root` | MySQL 用户名 |
| `DB_PASSWORD` | **是** | — | MySQL 密码 |
| `DB_URL` | 否 | `jdbc:mysql://localhost:3306/ecommerce_dev?...` | JDBC 连接串 |
| `REDIS_HOST` | 否 | `localhost` | Redis 地址 |
| `REDIS_PASSWORD` | **是** | — | Redis 密码 |
| `DEEPSEEK_API_KEY` | **是** | — | DeepSeek API Key |
| `ALIYUN_OSS_ACCESS_KEY_ID` | **是** | — | 阿里云 OSS AccessKey ID |
| `ALIYUN_OSS_ACCESS_KEY_SECRET` | **是** | — | 阿里云 OSS AccessKey Secret |
| `ALIYUN_OSS_BUCKET` | 否 | `mitsuha754` | OSS Bucket 名称 |
| `ALIYUN_OSS_URL_PREFIX` | 否 | `https://mitsuha754.oss-cn-beijing.aliyuncs.com/` | 图片访问域名前缀 |
| `JWT_SECRET` | 生产必改 | `dev-only-secret-please-change-0123456789` | JWT 签名密钥（≥32 字节） |

> 跨域白名单 `cors.allowed-origins` 在 `application.yaml` 中配置，默认放行 `localhost` 常见端口，生产环境请改为实际前端域名。

### Windows PowerShell 设置示例

```powershell
$env:DB_PASSWORD = "你的数据库密码"
$env:REDIS_PASSWORD = "你的Redis密码"
$env:DEEPSEEK_API_KEY = "sk-xxxx"
$env:ALIYUN_OSS_ACCESS_KEY_ID = "LTAIxxxx"
$env:ALIYUN_OSS_ACCESS_KEY_SECRET = "xxxx"
$env:JWT_SECRET = "长度不少于32字节的随机串"
```

### Linux / macOS 设置示例

```bash
export DB_PASSWORD="你的数据库密码"
export REDIS_PASSWORD="你的Redis密码"
export DEEPSEEK_API_KEY="sk-xxxx"
export ALIYUN_OSS_ACCESS_KEY_ID="LTAIxxxx"
export ALIYUN_OSS_ACCESS_KEY_SECRET="xxxx"
export JWT_SECRET="长度不少于32字节的随机串"
```

---

## 快速开始

```bash
# 1. 设置环境变量（见上）

# 2. 编译
./mvnw clean compile        # Windows 使用 .\mvnw.cmd

# 3. 运行
./mvnw -pl e-commerce-server spring-boot:run

# 或打包后运行
./mvnw clean package -DskipTests
java -jar e-commerce-server/target/e-commerce-server-1.0-SNAPSHOT.jar
```

启动后访问：`http://localhost:8080/api`

---

## 主要接口概览

| 模块 | 接口 | 说明 |
|------|------|------|
| 认证 | `POST /login` `POST /register` `POST /refresh` `POST /logout` | 登录/注册/刷新/退出 |
| 用户 | `POST /showBalance` | 查询当前用户余额 |
| 商品 | `GET /product/list` `POST /product/detail` | 分页搜索、详情（游客可访问） |
| 分类 | `POST /category/list` | 分类列表（游客可访问） |
| 购物车 | `POST /cart/add` `/cart/list` `/cart/delete` | 增删查 |
| 订单 | `/order/confirm` `/create` `/list` `/expire/{no}` `/cancel` `/detail/{no}` `/toPay` `/received` | 下单、支付、取消、收货 |
| 管理 | `/admin/user/*` `/category/*` `/product/*` `/order/*` | 后台管理（仅管理员） |
| AI | `POST /AiChat` | DeepSeek 流式购物助手 |

> 除标注「游客可访问」的接口外，其余接口需在请求头携带 `accessToken`（登录后返回）。

---

## 安全修复说明（重要）

本次对代码做了安全与功能修复，**前端需同步调整**：

1. **接口不再信任前端传入的 `userName`**：`/showBalance`、`/cart/list`、`/order/confirm|create|list`、`/order/cancel`、`/order/detail/{no}` 等接口统一从登录 token 中获取当前用户身份，多余参数会被忽略。
2. **`/order/createOrderItem` 接口已移除**：订单明细改为在 `/order/create` 时由服务端根据购物车自动生成，前端请移除该调用，避免重复插入明细。
3. **注册接口**：现在会校验手机号/邮箱格式，并落库 `name`、`phone`、`email` 字段。

---

## 已知事项

- 仓库未包含完整的数据库建表脚本（仅 `建表语句.sql` 中的 `user_refresh_token` 表，实际已改用 Redis 存储 refresh token）。业务表（`user`、`product`、`category`、`cart`、`order`、`order_item`）需按实体类字段手动建表。
- 商品分页、订单明细等历史 Bug 已修复，详见提交记录。
