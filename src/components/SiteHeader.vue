<template>
  <div class="site-header">
    <div class="left">
      <div class="brand" @click="$router.push('/')">LeonYin Blog</div>
      <el-menu mode="horizontal" :ellipsis="false" router>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/categories">分类</el-menu-item>
      </el-menu>
    </div>
    <div class="right" v-if="authed">
      <el-input v-model="keyword" placeholder="搜索文章" class="search" clearable @keyup.enter="goSearch">
        <template #append>
          <el-button @click="goSearch" type="primary">搜索</el-button>
        </template>
      </el-input>
      <el-dropdown @command="onCommand">
        <span class="el-dropdown-link">
          <el-avatar :size="32" :src="avatarUrl" />
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="account">我的账号</el-dropdown-item>
            <el-dropdown-item command="collections">我的收藏</el-dropdown-item>
            <el-dropdown-item v-if="isAdmin" command="admin">后台管理</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="right" v-else>
      <el-button @click="$router.push('/login')">登录</el-button>
      <el-button type="primary" @click="$router.push('/register')">注册</el-button>
    </div>
  </div>
</template>

<script>
import { getProfile } from '../api/user'
export default {
  name: 'SiteHeader',
  methods: {
    goSearch() {
      if (!this.keyword) return
      this.$router.push({ path: '/', query: { keyword: this.keyword } })
    },
    onCommand(cmd) {
      if (cmd === 'logout') this.logout()
      if (cmd === 'account') this.$router.push('/profile')
      if (cmd === 'collections') this.$router.push('/my-collections')
      if (cmd === 'admin') this.$router.push('/admin')
    },
    logout() {
      localStorage.removeItem('token')
      this.$router.push('/login')
      this.authed = false
      this.avatarUrl = ''
      this.isAdmin = false
    },
    async loadAvatar() {
      try {
        const res = await getProfile()
        const d = res && res.data ? res.data : {}
        this.avatarUrl = d.avatarUrl || (d.profile && d.profile.avatarUrl) || (d.userProfileVO && d.userProfileVO.avatarUrl) || ''
        // 基于返回信息补充角色判断
        try {
          const role = (d && d.account && (d.account.role || (d.account.userType === 1 ? 'ADMIN' : 'USER'))) || (d && d.role)
          if (role) this.isAdmin = role === 'ADMIN'
        } catch (e) { /* ignore */ }
      } catch (e) {
        // ignore
      }
    },
    syncAuth() {
      const has = !!localStorage.getItem('token')
      if (has !== this.authed) {
        this.authed = has
        if (this.authed) { this.resolveRoleFromToken(); this.loadAvatar() }
        else this.avatarUrl = ''
      }
    },
    resolveRoleFromToken() {
      try {
        const token = localStorage.getItem('token')
        if (!token) { this.isAdmin = false; return }
        if (token.split('.').length === 3) {
          const payloadStr = atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
          const payload = JSON.parse(payloadStr)
          const role = payload.role || (payload.userType === 1 ? 'ADMIN' : 'USER')
          this.isAdmin = role === 'ADMIN'
        }
      } catch (e) {
        this.isAdmin = false
      }
    }
  },
  data() {
    return { keyword: this.$route.query.keyword || '', avatarUrl: '', authed: !!localStorage.getItem('token'), isAdmin: false }
  },
  watch: {
    '$route.query.keyword'(v) { this.keyword = v || '' },
    '$route'() { this.syncAuth() }
  },
  mounted() {
    if (this.authed) { this.resolveRoleFromToken(); this.loadAvatar() }
    window.addEventListener('storage', this.syncAuth)
  },
  beforeUnmount() { window.removeEventListener('storage', this.syncAuth) }
}
</script>

<style scoped>
.site-header { max-width: 1080px; margin: 0 auto; height: 60px; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; }
.left { display: flex; align-items: center; gap: 12px; }
.brand { font-weight: 600; cursor: pointer; margin-right: 8px; }
.right { display: flex; align-items: center; gap: 12px; }
.search { width: 360px; }
</style>


