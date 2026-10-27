# FashionShop Backend

Java Web 后端：Maven + WAR + Tomcat 10.1 + Jakarta Servlet 6.0 + Gson。
数据保存在内存中，重启 Tomcat 后注册用户会清空，商品和演示账号固定。

## 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| POST | `/api/login` | `{"username":"demo","password":"123456"}` |
| POST | `/api/register` | `{"username":"...","password":"..."}`，用户名 ≥ 3 位、密码 ≥ 6 位 |
| GET | `/api/products` | 商品列表（22 件，含分类字段） |
| GET | `/api/products/{id}` | 商品详情 |

统一返回：

```json
{ "code": 200, "message": "success", "data": {} }
```

`code`：200 成功、400 参数错误、401 登录失败、404 资源不存在、500 服务器错误。

演示账号：`demo` / `123456`

浏览器打开 `http://localhost:8080/fashionshop/` 可在页面上直接测试接口。

## 构建

```bash
cd /d/Program/AndroidStudioProjects/fashionshop-backend
mvn clean package
```

生成 `target/fashionshop.war`。

## 部署到 Tomcat

```bash
cp target/fashionshop.war "D:/vscode/jdk-17.0.20+8/apache-tomcat-10.1.61/webapps/"
```

启动 Tomcat（注意 JAVA_HOME / CATALINA_HOME 都要设置）：

```bash
JAVA_HOME='D:\vscode\jdk-17.0.20+8' \
CATALINA_HOME='D:\vscode\jdk-17.0.20+8\apache-tomcat-10.1.61' \
CATALINA_BASE='D:\vscode\jdk-17.0.20+8\apache-tomcat-10.1.61' \
cmd //c "D:\\vscode\\jdk-17.0.20+8\\apache-tomcat-10.1.61\\bin\\catalina.bat run"
```

验证：`curl http://localhost:8080/fashionshop/api/health`

Android 模拟器通过 `http://10.0.2.2:8080/fashionshop/` 访问。

## 与 Android 端的边界（汇报时说明）

本后端当前只提供健康检查、登录、注册、商品列表和单件商品查询。Android 客户端的购物车、收货信息、订单和支付演示保存在手机本机 SharedPreferences 中；**本后端没有购物车、订单、支付接口，也没有接入实际支付平台**。因此“后端模式”仅表示登录、注册和商品列表由 Tomcat 提供，不能解释为订单已上传服务端或真实收款。

客户端订单流程为“待付款 → 模拟支付 → 待发货”或“待付款 → 已取消”，仅用于课程功能演示。上线前还需服务端持久化用户/订单、真正的 SKU 与库存校验、服务端鉴权、重算价格以及支付网关的安全回调。后端注册数据目前在内存，Tomcat 重启会清空；现有 `UserStore` 内的演示账号与密码也仅适合本地课设。

## 目录

```
src/main/java/com/fashionshop/backend/
├── model/    Product（含分类字段）、User
├── store/    ProductStore（商品数据）、UserStore（登录注册）
└── servlet/  BaseServlet、EncodingFilter、Health/Login/Register/ProductServlet
src/main/webapp/
├── index.html            接口测试页
└── WEB-INF/web.xml
```
