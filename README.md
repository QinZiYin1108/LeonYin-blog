# 前端（Vue 3 + Vue CLI 5）

基于 Vue 3、Element Plus 的博客前端，包含文章浏览、分类、登录注册、个人中心、后台管理等页面。

## 技术栈
- Vue 3、Vue Router 4、Vuex 4
- Element Plus、WangEditor、Markdown 渲染（v-md-editor / markdown-it / prismjs / highlight.js）
- Axios（统一请求封装，自动带上 `Authorization: Bearer {token}`）
- Vue CLI 5 构建

## 开发环境
- Node.js 14+（建议 16+）
- npm 7+

## 快速开始
```bash
# 安装依赖
npm install

# 启动开发（默认 http://localhost:5173）
npm run serve

# 生产构建（生成 dist/）
npm run build

# 代码检查与修复
npm run lint
```

## 与后端联调
- 开发代理：见 `vue.config.js`
  - 前端端口：`5173`
  - 将以 `/api` 开头的请求代理到 `http://localhost:8080`
- 请求基地址：见 `src/utils/http.js`，已设置为 `baseURL: '/api'`
- 认证：登录成功后将 `token` 存入 `localStorage`，后续请求自动携带 `Authorization: Bearer {token}`
- 返回结构：期望后端 `Result` 结构，`code === 200` 代表成功，否则抛出业务错误

如后端端口或上下文有变化，可修改：
- `vue.config.js` 中 `devServer.proxy['/api'].target`
- 或生产部署时在网关/Nginx 上将 `/api` 代理到后端实际地址

## 目录结构（简要）
- `src/api`：与后端接口的封装
- `src/utils/http.js`：Axios 实例与拦截器
- `src/router`：路由配置（含后台路由）
- `src/views`：页面视图（含 `admin` 子目录）
- `src/components`：通用组件
- `src/assets`：静态资源与全局样式

## 常见问题
- 端口被占用：修改 `vue.config.js` 的 `devServer.port`
- 跨域问题：开发阶段使用代理，无需更改后端；生产建议通过 Nginx 反向代理 `/api`
- Token 失效：后端返回非 200 `code` 时会走错误分支，请在页面侧统一处理跳转登录或提示

## 部署建议（生产）
1) 执行 `npm run build` 生成 `dist/`
2) 使用 Nginx/静态服务器托管 `dist/`
3) 配置反向代理：将 `/api` 代理到后端 `http://<backend-host>:8080`

更多 CLI 配置请参考官方文档：`https://cli.vuejs.org/config/`
