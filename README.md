# LeonYin-blog（Monorepo）

一个包含前后端的个人博客项目：后端基于 Spring Boot，前端基于 Vue 3 与 Element Plus。支持用户认证、文章/分类管理、文件上传、系统配置与限流，提供在线接口文档。

## 目录结构
```
LeonYin-blog/
├─ back-end/      # Spring Boot 后端服务
├─ front-end/     # Vue 3 前端项目
└─ main/          # 根级文档与脚本
```

更多使用细节请分别查看：
- 后端说明：`back-end/README.md`
- 前端说明：`front-end/README.md`

## 环境要求
- JDK 8+
- Node.js 14+（建议 16+） / npm 7+
- MySQL 8、Redis（如使用邮件/OSS需对应账号）

## 快速开始（本地开发）
1) 启动后端（PowerShell）
```powershell
# 在仓库根目录
./back-end/mvnw -f ./back-end/pom.xml spring-boot:run
```
- 默认服务地址：`http://localhost:8080/api`
- 接口文档（Knife4j）：`http://localhost:8080/api/doc.html`

2) 启动前端（开发代理指向后端）
```bash
cd front-end
npm install
npm run serve
```
- 开发地址：`http://localhost:5173`
- 代理配置：`vue.config.js` 将 `/api` 转发至 `http://localhost:8080`

## 基础配置（敏感信息）
后端建议通过环境变量注入数据库、Redis、JWT、邮件与 OSS 等配置，避免将真实 `application.yml` 提交到仓库（已在 `.gitignore` 忽略）。示例与完整变量清单见 `back-end/README.md`。

最小化变量示例（PowerShell）：
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="leon_blog"
$env:DB_USERNAME="user"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="请使用>=256bit的随机密钥"
```

## 生产构建与部署
- 后端
```powershell
./back-end/mvnw -f ./back-end/pom.xml clean package -DskipTests
java -jar ./back-end/target/back-end-0.0.1-SNAPSHOT.jar
```

- 前端
```bash
cd front-end
npm run build   # 产物在 dist/
```
使用 Nginx/静态服务器托管 `dist/`，并将 `/api` 反向代理到后端 `http://<backend-host>:8080`。

## 注意事项
- 切勿提交包含密钥/密码的 `application.yml`；如曾泄露请立即更换密钥
- 开发/测试/生产环境建议使用不同的配置与凭据
- 日志级别、限流参数与接口权限可根据需求在配置中调整

