# API 接口测试文档

## 项目概述
LeonYin-blog 后端 API 接口测试文档，包含所有 Controller 端点的详细介绍和测试方法。

**基础信息**:
- 后端地址: `http://localhost:8080/api`
- 技术栈: SpringBoot 2.7.18 + JWT + Redis
- 接口文档: `http://localhost:8080/api` (Knife4j)

---

## 认证说明

### JWT Token 使用
大部分接口需要在请求头中携带 JWT Token：
```
Authorization: Bearer <token>
```

### 用户角色
- `USER`: 普通用户
- `ADMIN`: 管理员

---

## 1. 认证模块 (AuthController)

**基础路径**: `/api/auth`

### 1.1 用户注册
- **接口**: `POST /api/auth/register`
- **描述**: 用户注册，需要邮箱验证码
- **权限**: 无需认证

#### 请求参数
```json
{
  "email": "test@example.com",
  "password": "123456",
  "nickname": "测试用户", 
  "registerType": 0,
  "verificationCode": "123456"
}
```

#### 测试用例
```bash
# 1. 先发送验证码
curl -X POST "http://localhost:8080/api/auth/send-code" \
  -H "Content-Type: application/json" \
  -d "email=test@example.com&purpose=0"

# 2. 注册用户
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456",
    "nickname": "测试用户",
    "registerType": 0,
    "verificationCode": "从邮箱或Redis获取的验证码"
  }'
```

#### 预期响应
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "U123456789",
    "email": "test@example.com",
    "nickname": "测试用户",
    "status": 1,
    "createTime": 1703123456789
  }
}
```

### 1.2 用户登录
- **接口**: `POST /api/auth/login`
- **描述**: 支持邮箱密码登录和邮箱验证码登录
- **权限**: 无需认证

#### 请求参数
```json
{
  "email": "test@example.com",
  "password": "123456",
  "loginType": 0,
  "verificationCode": ""
}
```

#### 测试用例
```bash
# 邮箱密码登录
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456",
    "loginType": 0
  }'

# 邮箱验证码登录
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "loginType": 1,
    "verificationCode": "123456"
  }'
```

#### 预期响应
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": "U123456789",
      "email": "test@example.com",
      "nickname": "测试用户"
    }
  }
}
```

### 1.3 发送邮箱验证码
- **接口**: `POST /api/auth/send-code`
- **描述**: 发送邮箱验证码
- **权限**: 无需认证

#### 请求参数
- `email`: 邮箱地址
- `purpose`: 用途 (0-注册，1-登录，2-重置密码)

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/auth/send-code" \
  -H "Content-Type: application/json" \
  -d "email=test@example.com&purpose=0"
```

---

## 2. 用户个人管理模块 (UserController)

**基础路径**: `/api/user`
**权限要求**: 需要 JWT Token

### 2.1 获取当前用户信息
- **接口**: `GET /api/user/profile`
- **描述**: 获取当前登录用户的个人信息
- **权限**: USER

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer <token>"
```

#### 预期响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "U123456789",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": "http://...",
    "status": 1,
    "gender": 0,
    "age": 25,
    "createTime": 1703123456789
  }
}
```

### 2.2 更新用户个人信息
- **接口**: `PUT /api/user/profile`
- **描述**: 更新当前用户的个人信息
- **权限**: USER

#### 请求参数
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/avatar.jpg",
  "bio": "个人简介",
  "realName": "张三",
  "phone": "13812345678"
}
```

#### 测试用例
```bash
curl -X PUT "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "bio": "这是我的个人简介",
    "phone": "13812345678"
  }'
```

### 2.3 上传头像
- **接口**: `POST /api/user/avatar`
- **描述**: 上传用户头像
- **权限**: USER
- **文件限制**: JPG/PNG/GIF, 最大2MB

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/user/avatar" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/avatar.jpg"
```

### 2.4 实名认证
- **接口**: `POST /api/user/real-name-verification`
- **描述**: 提交实名认证信息
- **权限**: USER

#### 请求参数
```json
{
  "realName": "张三",
  "idCard": "110101199001011234",
  "idCardFront": "http://...",
  "idCardBack": "http://..."
}
```

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/user/real-name-verification" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "张三",
    "idCard": "110101199001011234"
  }'
```

### 2.5 注销账号
- **接口**: `DELETE /api/user/account`
- **描述**: 注销当前用户账号
- **权限**: USER

#### 测试用例
```bash
curl -X DELETE "http://localhost:8080/api/user/account" \
  -H "Authorization: Bearer <token>"
```

---

## 3. 管理员用户管理模块 (AdminUserController)

**基础路径**: `/api/admin/users`
**权限要求**: ADMIN

### 3.1 分页查询用户列表
- **接口**: `POST /api/admin/users/page`
- **描述**: 分页查询用户列表
- **权限**: ADMIN

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "email": "",
  "nickname": "",
  "status": null,
  "startTime": null,
  "endTime": null
}
```

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/admin/users/page" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }'
```

### 3.2 获取用户详情
- **接口**: `GET /api/admin/users/{userId}`
- **描述**: 获取指定用户的详细信息
- **权限**: ADMIN

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/admin/users/U123456789" \
  -H "Authorization: Bearer <admin_token>"
```

### 3.3 更新用户信息
- **接口**: `PUT /api/admin/users/{userId}`
- **描述**: 管理员更新用户信息
- **权限**: ADMIN

#### 测试用例
```bash
curl -X PUT "http://localhost:8080/api/admin/users/U123456789" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "管理员修改的昵称",
    "status": 1
  }'
```

### 3.4 禁用/启用用户
- **接口**: `PUT /api/admin/users/{userId}/status`
- **描述**: 修改用户状态
- **权限**: ADMIN

#### 测试用例
```bash
# 禁用用户
curl -X PUT "http://localhost:8080/api/admin/users/U123456789/status?status=0" \
  -H "Authorization: Bearer <admin_token>"

# 启用用户  
curl -X PUT "http://localhost:8080/api/admin/users/U123456789/status?status=1" \
  -H "Authorization: Bearer <admin_token>"
```

### 3.5 查询用户操作日志
- **接口**: `GET /api/admin/users/logs`
- **描述**: 查询用户操作日志
- **权限**: ADMIN

#### 请求参数
- `pageNum`: 页码 (默认1)
- `pageSize`: 每页大小 (默认10)
- `userId`: 用户ID (可选)
- `operation`: 操作类型 (可选)

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/admin/users/logs?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer <admin_token>"

# 查询特定用户的日志
curl -X GET "http://localhost:8080/api/admin/users/logs?userId=U123456789" \
  -H "Authorization: Bearer <admin_token>"
```

---

## 4. 文件上传模块 (FileController)

**基础路径**: `/api/file`
**权限要求**: 需要 JWT Token

### 4.1 上传图片文件
- **接口**: `POST /api/file/upload/image`
- **描述**: 上传图片文件
- **权限**: USER
- **文件限制**: JPG/PNG/GIF, 最大2MB

#### 请求参数
- `file`: 图片文件
- `folder`: 文件夹名称 (默认 "images")

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/file/upload/image" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/image.jpg" \
  -F "folder=avatar"
```

### 4.2 上传文档文件
- **接口**: `POST /api/file/upload/document`
- **描述**: 上传文档文件
- **权限**: USER
- **文件限制**: PDF/DOC/DOCX/TXT, 最大10MB

#### 测试用例
```bash
curl -X POST "http://localhost:8080/api/file/upload/document" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/document.pdf" \
  -F "folder=documents"
```

### 4.3 删除文件
- **接口**: `DELETE /api/file/delete`
- **描述**: 删除已上传的文件
- **权限**: USER

#### 请求参数
- `fileUrl`: 文件URL

#### 测试用例
```bash
curl -X DELETE "http://localhost:8080/api/file/delete?fileUrl=https://..." \
  -H "Authorization: Bearer <token>"
```

---

## 5. 测试模块 (TestController)

**基础路径**: `/api/test`
**权限要求**: 无需认证 (仅用于开发测试)

### 5.1 测试Redis连接
- **接口**: `GET /api/test/redis`
- **描述**: 测试Redis连接状态

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/test/redis"
```

#### 预期响应
```
Redis连接正常! 写入: test_value, 读取: test_value
```

### 5.2 查看验证码
- **接口**: `GET /api/test/verification-code`
- **描述**: 查看邮箱验证码 (开发调试用)

#### 请求参数
- `email`: 邮箱地址

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/test/verification-code?email=test@example.com"
```

### 5.3 手动设置验证码
- **接口**: `GET /api/test/set-code`
- **描述**: 手动设置验证码 (测试用)

#### 请求参数
- `email`: 邮箱地址
- `code`: 验证码

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/test/set-code?email=test@example.com&code=123456"
```

### 5.4 查看Redis Keys
- **接口**: `GET /api/test/redis-keys`
- **描述**: 查看Redis中的所有keys

#### 测试用例
```bash
curl -X GET "http://localhost:8080/api/test/redis-keys"
```

---

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [ ... ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

## 测试脚本示例

### 完整用户注册登录流程测试
```bash
#!/bin/bash
BASE_URL="http://localhost:8080/api"

echo "=== 1. 发送注册验证码 ==="
curl -X POST "$BASE_URL/auth/send-code" \
  -H "Content-Type: application/json" \
  -d "email=test@example.com&purpose=0"

echo -e "\n=== 2. 用户注册 ==="
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456",
    "nickname": "测试用户",
    "registerType": 0,
    "verificationCode": "123456"
  }'

echo -e "\n=== 3. 用户登录 ==="
response=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456",
    "loginType": 0
  }')

token=$(echo $response | jq -r '.data.token')
echo "Token: $token"

echo -e "\n=== 4. 获取用户信息 ==="
curl -X GET "$BASE_URL/user/profile" \
  -H "Authorization: Bearer $token"
```

### 管理员功能测试
```bash
#!/bin/bash
BASE_URL="http://localhost:8080/api"
ADMIN_TOKEN="your_admin_token_here"

echo "=== 管理员查询用户列表 ==="
curl -X POST "$BASE_URL/admin/users/page" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }'

echo -e "\n=== 查询操作日志 ==="
curl -X GET "$BASE_URL/admin/users/logs?pageNum=1&pageSize=5" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 错误处理

### 常见错误代码
- `400`: 请求参数错误
- `401`: 未认证或Token无效
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 调试建议
1. **Token相关问题**: 检查请求头格式，确保Token有效
2. **参数验证失败**: 检查请求参数格式和必填字段
3. **权限不足**: 确认用户角色和接口权限要求
4. **文件上传失败**: 检查文件格式和大小限制

---

## 环境配置

### 开发环境
- 后端: `http://localhost:8080/api`
- 接口文档: `http://localhost:8080/api`
- Redis: `localhost:6379`
- MySQL: `39.99.40.133:3306`

### 测试工具推荐
- **Postman**: 图形化API测试
- **curl**: 命令行测试
- **Insomnia**: 轻量级API客户端
- **JMeter**: 性能测试

---

## 更新记录

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| 1.0.0 | 2024-01-XX | 初始版本创建，包含所有Controller接口 | Leon |
| 1.0.1 | 2024-01-XX | 修复User实体类缺失的setter方法，更新用户信息接口参数 | Leon |

---

## 注意事项

1. **生产环境**: 请删除或禁用 TestController
2. **Token安全**: 生产环境中使用HTTPS传输Token
3. **文件上传**: 注意文件类型和大小限制
4. **接口调用频率**: 注意避免频繁调用发送验证码接口
5. **日志记录**: 所有重要操作都会记录到用户日志中

**每次修改Controller代码后，请及时更新本文档！**
