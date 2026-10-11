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
| 用户中心 | `GET /user/profile` `POST /user/profile/update` `/user/password/update` `/user/deactivate` | 个人资料查询/修改、修改密码（需原密码）、注销账号（需密码） |
| 商品 | `GET /product/list` `GET /product/detail/{id}` `GET /product/shop/{productId}` | 分页搜索、详情、店铺公开信息（游客可访问，列表/详情仅返回已上架且审核通过的商品） |
| 分类 | `POST /category/list` | 分类列表（游客可访问） |
| 购物车 | `POST /cart/add` `/cart/list` `/cart/delete` | 增删查 |
| 订单 | `/order/confirm` `/create` `/list` `/expire/{no}` `/cancel` `/detail/{no}` `/toPay` `/received` | 下单、支付、取消、**确认收货（触发商家结算）** |
| 文件 | `POST /upload` | 图片上传（登录后可用，商家上架商品用） |
| 商家认证 | `GET /merchant/status` `POST /merchant/apply` `GET /merchant/info` | 查询认证状态、提交/重新提交申请、店铺信息 |
| 商家资料 | `POST /merchant/info/apply` `GET /merchant/info/apply` | 提交店铺资料变更申请（需管理员审核）、查询自己的申请状态 |
| 商家商品 | `GET /merchant/product/list` `POST /merchant/product/add` `/update` `/delete` `/status` | 商家对自己商品的增删改查与上下架（需商家身份） |
| 商家订单 | `GET /merchant/order/list` `GET /merchant/order/detail/{orderNo}` `POST /merchant/order/deliver` `/cancel` | 只处理「包含本店商品」的订单：查看、发货、取消（回退库存与余额） |
| 管理-分类 | `/admin/category/*` | 分类增删改查（管理端唯一可维护的商品基础数据） |
| 管理-商家 | `POST /admin/merchant/list` `/approve` `/reject` `/revoke` | 商家列表、审核通过、拒绝、撤销商家权限 |
| 管理-资料变更 | `POST /admin/merchant/update/list` `/approve` `/reject` | 商家资料变更申请列表（新旧值对比）、审核通过/驳回 |
| 管理-商品审核 | `GET /admin/merchant/product/list` `POST /admin/merchant/product/approve` `/reject` | 商家商品的审核列表与审核通过/驳回 |
| 管理-订单 | `POST /admin/order/list` `/admin/order/deliver` `/admin/order/cancel` | 订单列表（可按状态筛选）、发货、取消（回退库存与余额） |
| 管理-用户 | `/admin/user/*` | 用户管理（仅管理员） |
| AI | `POST /AiChat` | DeepSeek 流式购物助手 |

> 除标注「游客可访问」的接口外，其余接口需在请求头携带 `accessToken`（登录后返回）。

---

## 商家端业务流程

```
普通用户 ──提交认证──▶ merchant.status = 0（审核中）
                            │
        管理员 /admin/merchant/list 查看申请
                            ├── /admin/merchant/approve ──▶ status=1，user.role=MERCHANT
                            └── /admin/merchant/reject  ──▶ status=2（可重新提交申请）

商家（status=1）新增/修改商品 ──▶ product.audit_status = 0（待审核）+ 下架
                            │
        管理员 /admin/merchant/product/list 查看待审核商品
                            ├── /approve ──▶ audit_status=1 且自动上架
                            └── /reject  ──▶ audit_status=2（保持下架，商家可修改后重新送审）

管理员 /admin/merchant/revoke ──▶ merchant.status=3，user.role=USER，该商家商品全部下架
```

### 店铺资料变更（商家改资料 → 管理员审核）

```
商家在「个人信息管理」修改店铺资料 ──▶ 写入 merchant_update（status=0 待审核）
        │                              审核期间 merchant 表不变，商城展示的仍是旧资料
        │                              同一商家同一时间只允许一条待审核申请
        │
管理员 /admin/merchant/update/list 查看新旧值对比
        ├── /approve ──▶ 申请内容写回 merchant 表 + status=1（立即生效并展示到商城）
        └── /reject  ──▶ status=2，商家资料保持不变，商家可修改后重新提交
```

### 用户端店铺信息展示

- 商城主页商品卡片展示 **店铺名称**（`product` 联表 `merchant.shop_name`，历史平台商品显示「平台自营」）；
- 商品详情页展示店铺入口，点击「查看商家信息」可查看该店铺的 **店铺名 / 联系人 / 联系电话 / 店铺地址 / 店铺简介**；
- **联系人姓名脱敏**：接口只返回「姓」，其余用 `*` 代替（张三 → `张*`，欧阳娜娜 → `欧***`），
  脱敏在后端完成，原始姓名不会下发到前端。

### 订单流程（商家端 / 管理端）

```
买家下单并支付 ──▶ order.status = 1（待发货）
                      │   支付时：扣买家余额 + 扣库存（平台代收，商家还没拿到钱）
                      │
                      ├── 商家端「订单管理」：只显示包含本店商品的订单
                      │      · 发货  → order.status = 2（已发货）
                      │      · 取消  → order.status = 4（已取消，回退库存 + 退回买家余额）
                      │
                      ├── 管理端「订单管理」：可按状态筛选，同样支持发货 / 取消
                      │
                      └── 买家「确认收货」 → order.status = 3（已完成）
                             · 触发**商家结算**：按 订单明细 → 商品归属商家 汇总，
                               把货款分别打入各商家余额，并写入 order.settle_time
```

**商家结算（打款）规则**

- 结算时机：买家点击「确认收货」，订单从「已发货」流转到「已完成」时；
- 结算金额：`SUM(order_item.total_amount)` 按 `product.merchant_id` 分组，**一个订单包含多个商家时分别入账**；
- 平台历史商品（`product.merchant_id` 为空）不参与结算，这部分货款留在平台；
- 幂等保证：`receiveOrder` 带 `status = 2` 条件做原子流转（重复点击确认收货只有一个请求成功），
  `markSettled` 再带 `settle_time IS NULL` 兜底，**不会重复打款**；
- 已完成订单不允许再取消（否则会出现「商家已收款但订单被取消」），
  待付款/待发货订单取消时退买家余额、不回退商家（此时还没结算）；
- 历史遗留：结算逻辑上线前「已完成但未结算」的订单（`settle_time IS NULL`），
  可用 `backfill_order_settle.sql` 补打款（先跑第 1 段看清单，再跑第 2 段执行）。

**状态取值**

| 表 | 字段 | 取值 |
|----|------|------|
| `merchant` | `status` | 0 审核中 / 1 已通过 / 2 已拒绝 / 3 已撤销 |
| `merchant_update` | `status` | 0 待审核 / 1 已通过 / 2 已驳回 |
| `product` | `status` | 0 已下架 / 1 已上架 |
| `product` | `audit_status` | 0 待审核 / 1 已通过 / 2 已驳回 |
| `order` | `status` | 0 待付款 / 1 待发货 / 2 已发货 / 3 已完成 / 4 已取消 |

**订单操作的统一规则（商家端与管理端一致）**

- 发货：仅 `status = 1`（待发货）可发货；
- 取消：仅 `status ∈ {0, 1}` 可取消；已发货 / 已完成不允许取消；
- 已支付订单取消时自动回退库存与买家余额，并清理 Redis 临时订单；
- 商家只能操作「包含本店商品」的订单，其他订单一律返回「无权限操作该订单」。

### 商家购物与「不能买自己商品」

商家与买家是**同一个账号**（`user` 表同一个 `role/balance`），因此：

- 商家端右上角提供「去商城购物」，返回用户端后可用同一余额正常下单支付，余额实时互通；
- **商家不能购买自己店铺的商品**，在三个环节同时拦截：
  1. `POST /cart/add`：加入购物车时校验商品 `merchant_id`；
  2. `POST /order/confirm`（结算预览）与 `POST /order/create`（下单，含立即购买）；
  3. 前端商品详情页对自有商品直接禁用「加入购物车 / 立即购买」并给出提示。

### 用户中心（资料 / 密码 / 常用地址 / 注销）

用户端导航栏「用户中心」（`/home/profile`，头像下拉菜单也可进入）提供三块能力：

| 功能 | 说明 |
|------|------|
| 基本资料 | 姓名、手机号、邮箱、**常用收货地址**；登录账号 `username` 不支持修改（订单/购物车按它关联） |
| 修改密码 | **必须提供原密码**；新密码 6-20 位且不能与原密码相同；修改成功后前端自动退出并要求用新密码登录 |
| 注销账号 | **需要密码确认**；注销后 `user.status = 0`，无法登录、无法续期，同时清理 refreshToken |

**常用地址在下单时的使用**：结算页（`/order/confirm`）进入时会读取个人资料里的常用地址并**自动预填**收货地址，
同时在输入框上方提供一个「常用地址」快捷条，点击即可一键填入。

**账号状态与登录态**：
- 登录时会校验 `status`：`0 注销` / `2 异常` 都会被拒绝；
- `POST /refresh` 现在也会校验账号状态与是否存在，注销/禁用后旧 refreshToken 立即失效（返回 4001）；
- 用户注销不会删除历史订单（`order.user_name` 仍保留），只是不能再登录。

> 用户端 `GET /product/list`、`GET /product/detail/{id}` 只返回 `status = 1 且 audit_status = 1` 的商品。
> 权限校验：`@AdminOnly` 切面校验管理员；`@MerchantOnly` 切面校验商家角色，服务层还会再查一次商家表，
> 确保权限被撤销后的旧 token 无法继续操作商家端。

---

## 安全修复说明（重要）

本次对代码做了安全与功能修复，**前端需同步调整**：

1. **接口不再信任前端传入的 `userName`**：`/showBalance`、`/cart/list`、`/order/confirm|create|list`、`/order/cancel`、`/order/detail/{no}` 等接口统一从登录 token 中获取当前用户身份，多余参数会被忽略。
2. **`/order/createOrderItem` 接口已移除**：订单明细改为在 `/order/create` 时由服务端根据购物车自动生成，前端请移除该调用，避免重复插入明细。
3. **注册接口**：现在会校验手机号/邮箱格式，并落库 `name`、`phone`、`email` 字段。
4. **管理端不再提供创建/编辑商品接口**（原 `/admin/product/add`、`/admin/product/update`、`/admin/product/list` 已删除）：
   商品统一由商家创建，管理员只维护分类并负责「商家审核 + 商品上架审核」。
5. **商家端商品归属校验**：商家只能查询/修改/删除自己的商品，伪造 `merchantId`、`auditStatus`、`status` 参数无效
   （服务端强制覆盖为当前登录商家与待审核状态）。
6. **商家端订单归属校验**：商家只能查看/发货/取消「包含本店商品」的订单，订单详情也只返回本店商品明细，
   防止越权操作他人订单。
7. **禁止自购**：商家账号不能购买自己店铺的商品（加购、结算预览、下单三处校验），避免刷单与余额空转。
8. **店铺资料变更需审核**：商家修改店铺资料不会立即生效，先写入 `merchant_update` 待审核，
   管理员通过后才写回 `merchant`；审核期间不允许重复提交。
9. **商家姓名脱敏**：用户端「查看商家信息」接口只返回姓氏（张三 → `张*`、欧阳娜娜 → `欧***`），
   脱敏在服务端完成，原始姓名不会下发到前端。
10. **用户中心**：修改密码与注销账号都必须校验原密码；注销后账号无法登录、无法续期，
    但历史订单保留（便于对账）。
11. **上传大小限制**：`spring.servlet.multipart.max-file-size` 默认只有 1MB，
    已在 `application.yaml` 调为 3MB，并新增 `MaxUploadSizeExceededException` 处理器，
    避免 1~2MB 的图片被容器直接拒掉后只提示「网络异常」。
12. **商家结算**：买家确认收货时把货款按商品归属分账打给商家（写入 `order.settle_time`）；
    支付时只扣买家余额，**商家余额在确认收货后才增加**（平台代收模式）；
    已完成订单不可取消，取消仅限待付款/待发货且此时未结算，不会出现重复退款/重复打款。

---

## 数据库

建表语句见 [`建表语句.sql`](建表语句.sql)（与线上 `ecommerce_dev` 库保持一致）。
已存在的库升级到商家端版本，请按顺序执行增量脚本：

```bash
mysql -h <host> -u <user> -p < migration_merchant_audit.sql
mysql -h <host> -u <user> -p < migration_merchant_update.sql
```

`migration_merchant_audit.sql` 会：

1. `merchant` 表增加 `contact_name`、`contact_phone`、`introduction` 字段并放宽 `shop_name`/`shop_address` 长度；
2. `product` 表增加 `merchant_id`（归属商家）与 `audit_status`（上架审核状态）字段及索引；
3. 把历史（`merchant_id IS NULL`）商品标记为审核通过，保证原有商品继续正常展示。

`migration_merchant_update.sql` 会新建 `merchant_update` 商家资料变更申请表（`user_id` 唯一）。

---

## 已知事项

- 历史遗留的 `shop` 表已废弃（由 `merchant` 表取代），代码中不再有任何读写。
- **订单状态是整单状态**（`order.status`），没有按商家拆分子订单：若一个订单同时包含多个商家的商品，
  这些商家都能看到该订单，任一方发货/取消都会作用于整单。商家列表中的「本店小计/件数」只统计自己的商品。
  如需严格的商家维度履约，需要增加订单拆单（子订单表）后再做演进。
- 商品分页、订单明细等历史 Bug 已修复，详见提交记录。
