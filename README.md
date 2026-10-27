# FashionShop 服饰商城

一个前后端分离的课程设计项目：Android 客户端 + Java Web 后端，实现完整的购物流程（登录注册 → 浏览商品 → 加购 → 结算 → 模拟支付 → 订单管理）。

> ⚠️ 支付为**模拟支付**，不接入支付宝/微信/银行，不会发生真实扣款。

## 项目结构

```
fashionshop/
├── android/      Android 客户端（Java + XML，Activity + Fragment）
└── backend/      Java Web 后端（Maven + Tomcat 10.1 + Jakarta Servlet + Gson）
```

## 技术栈

| 端 | 技术 |
| --- | --- |
| Android | Java 11、XML 布局、Material Components、RecyclerView、ViewPager2、Glide、Gson |
| 网络 | HttpURLConnection + 线程池 + Handler（不使用 Retrofit/OkHttp） |
| 后端 | Tomcat 10.1 + Jakarta Servlet 6.0 + Gson，内存数据存储 |
| 数据持久化 | Android 端 SharedPreferences（登录态、购物车、订单）；后端内存存储 |

## 功能

- 登录 / 注册（支持 Mock 数据与真实后端两种数据源切换）
- 首页：轮播图 + 商品列表 + 刷新 + 分类筛选（上衣/裤子/裙子/外套/鞋子/配饰）
- 商品详情：选尺码、加入购物车
- 购物车：增减数量、单选/全选、结算
- 确认订单：填收货信息、生成订单
- 模拟支付：支付宝/微信二选一，仅改本机订单状态
- 订单管理：待付款去支付/取消，已完成/已取消订单可删除

## 快速开始

### Android 客户端

用 Android Studio 打开 `android/` 目录，等待 Gradle 同步后运行 `app`。

- 默认数据源为 Mock，登录账号 `demo / 123456`
- 切换真实后端：登录页打开「Mock / 后端」开关，需先启动后端

命令行构建：

```powershell
cd android
.\gradlew.bat assembleDebug
```

APK 输出：`android/app/build/outputs/apk/debug/app-debug.apk`

### 后端

```bash
cd backend
mvn clean package        # 生成 target/fashionshop.war
```

将 `target/fashionshop.war` 部署到 Tomcat 10.1 的 `webapps/` 目录，启动 Tomcat 后访问：

- 接口测试页：`http://localhost:8080/fashionshop/`
- 健康检查：`http://localhost:8080/fashionshop/api/health`

Android 模拟器通过 `http://10.0.2.2:8080/fashionshop/` 访问宿主机后端。

## 后端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| POST | `/api/login` | 登录 `{username, password}` |
| POST | `/api/register` | 注册 `{username, password}` |
| GET | `/api/products` | 商品列表（22 件，含分类字段） |
| GET | `/api/products/{id}` | 商品详情 |

统一返回：`{ "code": 200, "message": "success", "data": ... }`

## 说明

- 购物车、订单、模拟支付全部保存在 Android 本机 SharedPreferences，后端不提供这些接口。
- 商品图片使用 Unsplash 外部 HTTPS 图片，断网时显示占位图。
- Android 17（API 37）访问宿主机后端需要 `ACCESS_LOCAL_NETWORK` 运行时权限（已在代码中处理）。
