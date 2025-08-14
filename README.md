# 后端服务（Spring Boot）

基于 Spring Boot 的个人博客后端，提供用户认证、文章与分类管理、评论与交互、文件上传、系统配置、限流与接口文档等能力。

## 技术栈
- 后端框架：Spring Boot 2.7.18（JDK 8）
- 数据访问：MyBatis-Plus 3.5.x、MySQL 8
- 缓存/限流：Redis、自定义注解 `@RateLimit`
- 认证授权：Spring Security、JWT（jjwt 0.11.x）
- 对象存储：阿里云 OSS（可选）
- 文档：Knife4j 3.x（Swagger2）

## 环境要求
- JDK 8+
- MySQL 8（已创建数据库并具备连接账号）
- Redis（默认本地 6379，可用环境变量覆盖）
- SMTP 邮箱（可选，用于发送验证码）
- 阿里云 OSS（可选，用于文件托管）

## 快速开始
1) 配置方式
- 推荐使用环境变量注入敏感信息，避免把真实 `application.yml` 提交到仓库（已在 `.gitignore` 忽略）。
- 后端上下文路径：`/api`，默认地址为 `http://localhost:8080/api`。

2) 启动（Windows PowerShell）
```powershell
# 在仓库根目录执行
./back-end/mvnw -f ./back-end/pom.xml spring-boot:run
```

3) 常用地址
- 接口根路径：`http://localhost:8080/api`
- 接口文档（Knife4j）：`http://localhost:8080/api/doc.html`
- 文件上传测试页：`http://localhost:8080/api/upload-test.html`（或 `http://localhost:8080/api/test-upload.html`）

## 环境变量示例（PowerShell）
```powershell
# 数据库
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="leon_blog"
$env:DB_USERNAME="user"
$env:DB_PASSWORD="your_password"

# Redis
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:REDIS_PASSWORD=""
$env:REDIS_DATABASE="0"

# JWT
$env:JWT_SECRET="请使用>=256bit的随机密钥"
$env:JWT_EXPIRATION="86400000"           # 24小时
$env:JWT_REFRESH_EXPIRATION="604800000"  # 7天

# 邮件（可选）
$env:MAIL_HOST="smtp.qq.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME=""
$env:MAIL_PASSWORD=""

# 阿里云 OSS（可选）
$env:ALIYUN_OSS_ENDPOINT=""
$env:ALIYUN_OSS_ACCESS_KEY_ID=""
$env:ALIYUN_OSS_ACCESS_KEY_SECRET=""
$env:ALIYUN_OSS_BUCKET_NAME=""
$env:ALIYUN_OSS_URL_PREFIX=""
```

> 也可本地创建 `back-end/src/main/resources/application.yml`，但请勿提交到仓库。

## 认证与访问
- 登录：`POST /api/auth/login`，成功后返回 `token`
- 访问受保护接口时在请求头添加：`Authorization: Bearer {token}`
- 公开接口：`/api/auth/**`、`/api/article/**`、`/api/category/**` 等
- 需登录接口：`/api/user/**`、`/api/file/**`、`/api/articles/auth/**`、`/api/comments/**`
- 管理端接口：`/api/admin/**`（需要 `ROLE_ADMIN`）

## 限流
- 使用注解 `@RateLimit(capacity = ..., ratePerSecond = ..., key = "#{ip}:/path")`
- 全局/业务限流参数可在配置中调整（见 `rate-limit`、`blog.rate-limit`）

## 打包与部署
```powershell
# 构建
./back-end/mvnw -f ./back-end/pom.xml clean package -DskipTests

# 运行
java -jar ./back-end/target/back-end-0.0.1-SNAPSHOT.jar
```

## 目录结构（简要）
- `com.example.backend.controller`：接口层
- `com.example.backend.service`：业务层（含 `impl`）
- `com.example.backend.mapper`：持久层（MyBatis-Plus）
- `com.example.backend.entity`：实体模型
- `com.example.backend.config`：安全、Swagger、MyBatis-Plus、拦截器等配置
- `com.example.backend.filter`：JWT 认证等过滤器
- `com.example.backend.util`：工具类

## 注意事项
- 曾经提交过的密钥/密码请立刻更换
- 优先使用环境变量维护敏感配置
- 控制台日志较详细，可通过 `logging.level` 调整
