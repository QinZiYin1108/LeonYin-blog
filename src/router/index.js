import { createRouter, createWebHistory } from 'vue-router'

const ArticleList = () => import('../views/ArticleList.vue')
const PublicArticleDetail = () => import('../views/PublicArticleDetail.vue')
const Categories = () => import('../views/Categories.vue')
const MyCollections = () => import('../views/MyCollections.vue')
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const ForgotPassword = () => import('../views/ForgotPassword.vue')
const Profile = () => import('../views/Profile.vue')
const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const AdminDashboard = () => import('../views/admin/AdminDashboard.vue')

const routes = [
  { path: '/', name: 'home', component: ArticleList },
  { path: '/categories', name: 'categories', component: Categories },
  { path: '/my-collections', name: 'myCollections', component: MyCollections },
  { path: '/article', name: 'articleDetail', component: PublicArticleDetail },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/forgot-password', name: 'forgotPassword', component: ForgotPassword },
  { path: '/profile', name: 'profile', component: Profile },
  { path: '/admin', component: AdminLayout, children: [
    { path: '', name: 'admin', component: AdminDashboard },
    { path: 'users', name: 'adminUsers', component: () => import('../views/admin/UserManage.vue') },
    { path: 'articles', name: 'adminArticles', component: () => import('../views/admin/ArticleManage.vue') },
    { path: 'article-editor', name: 'adminArticleEditor', component: () => import('../views/admin/ArticleEditor.vue') },
    { path: 'article-detail', name: 'adminArticleDetail', component: () => import('../views/admin/ArticleDetail.vue') },
    { path: 'categories', name: 'adminCategories', component: () => import('../views/admin/CategoryManage.vue') },
    { path: 'configs', name: 'adminConfigs', component: () => import('../views/admin/SystemConfig.vue') },
    { path: 'logs', name: 'adminLogs', component: () => import('../views/admin/LogManage.vue') }
  ]}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router


