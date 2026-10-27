# FashionShop 服饰商城（Android）

本项目是 Java + XML 的课程设计。可演示：注册、登录、浏览商品、选尺码、加入购物车、全选和数量调整、填写收货信息、下单、**模拟支付**、待付款订单继续付款及取消、查看订单、退出登录。支付只改变本机订单状态；**没有接入支付宝、微信或银行，也不会发生扣款**。汇报时请明确称为“模拟支付”。

## 1. 技术栈与项目位置

- Android 客户端：Java 11 语法、XML 布局、Activity + Fragment、Material Components、RecyclerView、Glide、Gson。
- 网络：`HttpURLConnection` + `ExecutorService` 子线程 + `Handler(Looper.getMainLooper())`；不使用 Retrofit、OkHttp、Kotlin 协程。
- 后端：独立的 Maven WAR 项目，Tomcat 10.1 + `jakarta.servlet.*` + Gson。代码位于 `D:\Program\AndroidStudioProjects\fashionshop-backend`。
- Android 工程：`D:\Program\AndroidStudioProjects\FashionShop`。主要代码在 `app/src/main/java/com/example/fashionshop/`。
- `compileSdk/targetSdk 37`，`minSdk 24`。`app/build.gradle.kts` 使用 Kotlin DSL 仅配置构建，应用业务代码是 Java。

## 2. 运行和演示

在 Android Studio 打开 Android 工程，等待同步后运行 `app`。默认数据源是 Mock；登录 `demo / 123456`。建议演示路径：

1. 首页 → 点商品 → 选择尺码 → 加入购物车。同一商品不同尺码会成为不同购物车行。
2. 购物车 → 增减数量、单选或全选 → 结算。仅勾选的商品进入本次结算。
3. 确认订单 → 填收货人、11 位中国大陆手机号、详细地址。请填虚构信息。提交后生成“待付款”订单，选中商品从购物车移除；未勾选商品留在购物车。
4. 模拟支付页 → 选“模拟支付宝”或“模拟微信支付” → 确认。订单改为“待发货”。页面明确提示不会真实扣款。
5. 再做一单，在支付页点“稍后支付”；订单页显示“待付款”，可“去支付”或“取消订单”。点击订单卡片可看收货信息和支付方式。
6. “我的” → 切换数据源或退出登录。切换数据源后必须重新登录。

命令行构建（PowerShell；若系统的 `JAVA_HOME` 已正确指向兼容 JDK，可直接运行 Gradle）：

```powershell
cd D:\Program\AndroidStudioProjects\FashionShop
$env:JAVA_HOME = 'D:\vscode\jdk-17.0.20+8'
.\gradlew.bat assembleDebug --no-configuration-cache
```

APK 输出：`app/build/outputs/apk/debug/app-debug.apk`。本地当前 Gradle wrapper 使用 9.5.0；首次同步可能需要下载依赖。不要将 `local.properties` 中的 SDK 私有路径直接复制给其他电脑。

## 3. 各模块怎么实现

| 模块 | 主要文件 | 关键实现、边界处理 |
| --- | --- | --- |
| 启动与导航 | `FashionShopApp.java`、`LoginActivity.java`、`MainActivity.java`、`activity_main.xml`、`menu/bottom_nav_menu.xml` | Application 恢复数据源设置；登录页查询 SharedPreferences 登录状态；主页面由 BottomNavigationView 切换首页、购物车、订单、我的四个 Fragment；Android 17 的本地网络权限在连接后端时申请。 |
| 登录与注册 | `LoginActivity.java`、`RegisterActivity.java`、`data/SessionManager.java`、`net/MockApi.java` | 输入框本地校验用户名≥3、密码≥6，按钮请求期间禁用防重复提交；登录成功只保存用户 ID、用户名和来源，不保存明文密码。Mock 已知账号才能登录，注册账号本机持久化为随机盐 + PBKDF2 密码摘要；演示账号为固定 `demo/123456`。后端模式调用 POST JSON 接口。 |
| 网络 | `net/ApiConfig.java`、`ApiClient.java`、`HttpUtil.java`、`ApiCallback.java` | UI 只调用 ApiClient；它按当前数据源转向 MockApi 或 HttpUtil。HttpURLConnection 在固定线程池内运行；设置 10 秒连接/读取超时，POST 以 UTF-8 写 JSON，检查 HTTP 状态，读取输入/错误流，Gson 解析统一包裹 JSON。结果由主线程 Handler 投递给 Activity/Fragment，网络异常和无效 JSON 走 `onError`，流使用 try-with-resources 关闭、连接 finally 断开。 |
| 商品首页 | `ui/home/HomeFragment.java`、`ProductAdapter.java`、`fragment_home.xml`、`item_product.xml` | `ApiClient.getProducts()` 获取 `List<Product>`（含分类）；RecyclerView + GridLayoutManager 双列显示，卡片显示名称、价格；Glide 异步加载图像并配置占位/失败图；加载中、空数据、错误、重试分开处理；右上角“刷新”按钮重新拉取商品，顶部 ChipGroup 按分类（上衣/裤子/裙子/外套/鞋子/配饰）筛选；点击卡片用 Intent 打开详情。 |
| 商品详情与尺码 | `ProductDetailActivity.java`、`data/ProductSizes.java`、`activity_product_detail.xml` | Intent 传入商品字段（含分类，详情页显示“分类：xx”）；ChipGroup 单选尺码；服装 S/M/L/XL、鞋类 37–42、围巾均码。没选尺码不能加购。当前尺码按商品名称推断，仅供课程演示，**没有真实 SKU ID/库存校验**；接正式商城须由后端提供 SKU 并验证库存。 |
| 购物车 | `data/CartManager.java`、`data/model/CartItem.java`、`ui/cart/CartFragment.java`、`CartAdapter.java`、`fragment_cart.xml`、`item_cart.xml` | 商品 ID + 尺码作为购物车合并依据；同款同尺码加数量，限制最大 99。SharedPreferences 内保存 Gson JSON；增减、删除、单选、全选后重新计算小计、总价、角标。价格是加入时的本地快照，实际交易必须由服务端重新计价。 |
| 确认订单 | `CheckoutActivity.java`、`activity_checkout.xml`、`data/OrderManager.java` | 页面重新读取勾选的购物车商品，展示数量和金额。收货信息本地校验，保存到当前账号的 SharedPreferences。确认后先将商品、规格、数量、单价、地址复制为订单快照并同步写入，再移除购物车已选商品；保存失败不会跳转支付。金额用 BigDecimal 对商品价×数量累加后保留两位。 |
| 模拟支付 | `PaymentActivity.java`、`activity_payment.xml`、`data/OrderManager.java` | 订单从“待付款”变成“待发货”的本地演示流程。选模拟方式、二次确认后，OrderManager 只允许待付款状态付款，并同步持久化状态、方式和时间；重复点击或已取消/已付款订单不能再付款。“稍后支付”保留待付款订单供订单页继续付款。**没有调用付款接口，也没有服务端支付凭证。** |
| 订单列表 | `ui/orders/OrdersFragment.java`、`OrderAdapter.java`、`data/model/Order.java`、`OrderItem.java`、`fragment_orders.xml`、`item_order.xml` | 本机订单按新到旧显示。卡片显示订单号、状态、时间、商品行、数量、金额；待付款可去支付或确认取消；已取消或已完成（待发货）的订单可“删除订单”并二次确认后移除；点击卡片看地址及支付方式。订单状态和数据只在本机可信演示。 |
| 我的 | `ui/profile/ProfileFragment.java`、`fragment_profile.xml` | 显示用户名、订单入口、数据源切换和退出登录。切源清除当前登录状态并回到登录页，避免旧账号在另一数据源直接复用。 |

## 4. Mock 与连接后端的区别

| 项目 | Mock 模式 | 真实后端模式 |
| --- | --- | --- |
| 如何切换 | 登录页下方开关；或“我的”开关，切后重新登录 | 同左，关闭 Mock 开关 |
| 是否启动 Tomcat | 不需要 | 必须启动 Tomcat 并部署 `fashionshop.war` |
| 登录/注册 | `MockApi` 本地执行，注册用户摘要保存在 Android SharedPreferences；演示账号固定 | `ApiClient` → `HttpUtil.post()` → Tomcat `LoginServlet` / `RegisterServlet`；后端数据在内存，重启后新注册账号会丢失 |
| 商品列表 | `MockApi` 固定 22 件商品（含分类），Handler 延迟约 600ms 模拟网络 | `HttpUtil.get()` 从 `/api/products` 读取后端同样 22 件商品 |
| 商品详情 | 首页传入商品对象字段，尺码在客户端推断 | 同样从首页传字段；后端另有 `GET /api/products/{id}`，当前详情页**没有单独请求它** |
| 购物车、收货信息、订单、支付 | **全部是本机 SharedPreferences 功能** | **仍然是本机 SharedPreferences 功能**，后端没有购物车/订单/支付接口 |
| 图片 | Unsplash 外部 HTTPS 图片；断网可能显示占位图 | 同一批外部 HTTPS 图片；Tomcat 没有托管商品图片 |
| 网络异常 | API 逻辑不依赖网络，但在线图片可能失败 | 会出现错误提示/重试；模拟器需有权访问宿主 Tomcat |

因此，切到后端模式只能证明**登录、注册、商品列表**完成真实联调；不能在答辩中声称服务端已保存订单或完成真实交易。购物车、地址、订单按 **数据源 + 用户名** 隔离保存在本机，同一账号切回原模式可看到原来的订单。本版本改用新的隔离键，旧版本未隔离的本机购物车/订单不会自动导入；这是为了避免把不同用户或数据源的订单混在一起。

请求约定：`ApiConfig.BASE_URL = http://10.0.2.2:8080/fashionshop/`；`10.0.2.2` 是 Android 模拟器访问电脑 localhost 的特殊地址。真机需改成电脑在同一局域网的 IP，并确认防火墙放行。Tomcat 实际路径/端口变化时同步修改 `ApiConfig.java`。本地 HTTP 通过 `AndroidManifest.xml` 的 INTERNET 权限和 `res/xml/network_security_config.xml` 开发配置放行；正式上线应使用 HTTPS。Android 17/API 37 还需同意本地网络权限。

统一响应格式（客户端先读 `code/message`，再用 Gson 解析 `data`）：

```json
{ "code": 200, "message": "success", "data": [] }
```

| 方法 | 路径 | 当前用途 |
| --- | --- | --- |
| GET | `/api/health` | 浏览器验证部署，Android 页面不调用 |
| POST | `/api/login` | JSON `username/password`，返回 `User` |
| POST | `/api/register` | JSON `username/password`，返回 `User` |
| GET | `/api/products` | 返回 `Product` 列表 |
| GET | `/api/products/{id}` | 可供后续详情接口联调，当前 Android 详情未调用 |

后端业务错误仍以 HTTP 200 返回，靠 JSON `code` 字段表示 400/401/404；Android `HttpUtil` 同时检查 HTTP 响应码和业务码。后端无 Token/Session，客户端保存的登录状态不等于服务端持续认证，报告中应如实说明。

## 5. 汇报时可以讲清楚的设计细节

- **线程为什么这样分工**：网络 I/O 可能阻塞，`HttpUtil` 在线程池执行；Android View 只能安全地在主线程更新，所以用 Handler 回调。Mock 同样从主线程 Handler 返回，UI 不必写两套处理方式。
- **为什么有 ApiClient**：页面只依赖 `login/register/getProducts` 等业务方法；Mock 与后端在这里统一分流，切换数据源不需要改每个 Fragment。
- **订单快照与状态**：下单时拷贝商品名称、尺码、单价、数量、地址，不随购物车后续增删改变。订单状态为“待付款 → 待发货”或“待付款 → 已取消”；只有待付款能执行支付或取消。
- **本机持久化范围**：SharedPreferences + Gson 适合课设和单机演示；一旦清除 App 数据或卸载，所有本机订单会丢失。真实商用需要数据库、服务端订单、真实 SKU/库存校验、正式认证及支付平台服务端回调。
- **错误与边界**：空购物车不能结算，未选商品不能结算；购买数量 1–99；收货人/手机号/地址校验；网络超时、错误 JSON 显示错误状态；商品图失败用占位图；重复模拟付款不会再次改变已支付订单。

## 6. 建议验收清单

- Mock：正确/错误密码、未知用户名、注册后退出并重启再登录；首页→详情→购物车→确认订单→模拟付款→订单状态；订单稍后付、重进补付、取消后不能再付。
- 后端：电脑浏览器打开 `http://localhost:8080/fashionshop/api/health`；模拟器切后端登录 `demo/123456`，首页看到商品；停掉 Tomcat 后重新加载应有错误提示；切回 Mock 可以继续演示本地流程。
- 屏幕：小屏和键盘弹出时，结算/支付页可滚动；商品图片断网时占位图不影响下单。

尚未实现：真实收款、服务端订单、库存管理、数据库持久化、服务端图片存储、退款、物流、真正的 SKU。报告和现场演示请按当前实现描述。
