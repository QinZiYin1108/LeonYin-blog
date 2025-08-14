<template>
  <el-container class="layout">
    <template v-if="!isAdmin">
      <el-header class="header-wrap">
        <SiteHeader />
      </el-header>
    </template>
    <el-main class="main-wrap">
      <router-view />
    </el-main>
    <template v-if="!isAdmin">
      <el-footer class="footer-wrap">
        <SiteFooter />
      </el-footer>
    </template>
  </el-container>
</template>

<script>
import SiteHeader from './components/SiteHeader.vue'
import SiteFooter from './components/SiteFooter.vue'

export default {
  name: 'App',
  components: { SiteHeader, SiteFooter },
  computed: {
    isAdmin() {
      return this.$route.path.startsWith('/admin')
    }
  }
}
</script>
<style>
html, body, #app { height: 100%; }
.main-wrap { --el-main-padding:0 !important}
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.main-wrap { flex: 1; }
.header-wrap, .footer-wrap { padding: 0; }
</style>
<style>

</style>
