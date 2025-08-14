<template>
  <div class="admin">
    <el-container class="admin-layout">
      <el-aside width="200px" class="aside">
        <div class="brand" @click="$router.push('/admin')">Admin Panel</div>
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/admin">
            <span>概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/articles">
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/configs">
            <span>系统配置</span>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="topbar">
          <div class="title">后台管理</div>
          <el-button size="small" @click="$router.push('/')">返回前台</el-button>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
  </template>

<script>
export default { name: 'AdminLayout' }
</script>

<style scoped>
.admin-layout { min-height: calc(100vh - 0px); }
.aside { border-right: 1px solid #eee; }
.brand { padding: 12px; font-weight: 600; cursor: pointer; }
.topbar { display: flex; align-items: center; justify-content: space-between; }
</style>


