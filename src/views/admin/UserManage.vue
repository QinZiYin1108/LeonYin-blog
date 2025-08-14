<template>
  <div>
    <el-card :loading="loading">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
          <span>用户管理</span>
          <div style="display:flex;gap:8px;">
            <el-input v-model="query.email" placeholder="邮箱关键词" clearable style="width:200px"/>
            <el-select v-model="query.userType" placeholder="用户类型" clearable style="width:140px">
              <el-option :value="0" label="普通用户"/>
              <el-option :value="1" label="管理员"/>
            </el-select>
            <el-select v-model="query.status" placeholder="状态" clearable style="width:120px">
              <el-option :value="1" label="正常"/>
              <el-option :value="0" label="禁用"/>
            </el-select>
            <el-button type="primary" @click="load(1)">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="items" size="small" style="width:100%">
        <el-table-column label="头像" width="80">
          <template #default="{row}">
            <el-image v-if="row.avatarUrl" :src="row.avatarUrl" style="width:40px;height:40px;border-radius:50%;" fit="cover" />
            <span v-else style="color:#bbb;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="200"/>
        <el-table-column prop="nickname" label="昵称" min-width="140"/>
        <el-table-column prop="bio" label="简介" min-width="200"/>
        <el-table-column prop="userType" label="类型" width="100">
          <template #default="{row}">
            <el-tag :type="row.userType===1?'warning':'info'">{{ row.userType===1?'管理员':'用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'正常':'禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="emailVerified" label="邮箱验证" width="100">
          <template #default="{row}">
            <el-tag :type="row.emailVerified===1?'success':'info'">{{ row.emailVerified===1?'已验证':'未验证' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" min-width="160">
          <template #default="{row}">{{ formatTime(row.lastLoginTime) }}</template>
        </el-table-column>
        <el-table-column label="启用" width="90">
          <template #default="{row}">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" :loading="rowLoading[row.id]===true" @change="(val) => handleToggleStatus(row, val)"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-dropdown @command="cmd => onCommand(cmd, row)">
              <el-button type="primary" size="small" :loading="rowLoading[row.id]===true">
                操作
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑账号</el-dropdown-item>
                  <el-dropdown-item command="profile">编辑资料</el-dropdown-item>
                  <el-dropdown-item command="resetAvatar">重置头像为默认</el-dropdown-item>
                  <el-dropdown-item command="reset">重置密码</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:flex-end;margin-top:8px;">
        <el-pagination background layout="prev, pager, next" :current-page="query.pageNum" :page-size="query.pageSize" :total="total" @current-change="load"/>
      </div>
    </el-card>
  </div>
</template>

<script>
import { pageUsers, updateUser, resetUserPassword } from '../../api/adminUser'
import { ElMessageBox, ElMessage } from 'element-plus'
import { listAllProfiles, updateUserProfileByAdmin, resetAvatarToDefault } from '../../api/adminProfile'

export default {
  name: 'UserManage',
  data() {
    return {
      query: { pageNum: 1, pageSize: 10, email: '', userType: null, status: null },
      items: [],
      total: 0,
      tmp: {},
      loading: false,
      rowLoading: {}
    }
  },
  methods: {
    onCommand(cmd, row) {
      if (cmd === 'edit') this.openEditAccount(row)
      if (cmd === 'profile') this.openEditProfile(row)
      if (cmd === 'reset') this.resetPwd(row)
      if (cmd === 'resetAvatar') this.resetAvatar(row)
    },
    async openEditAccount(row) {
      // 简易弹窗编辑账号信息：类型/状态/邮箱验证
      const ok = await this.$prompt('请输入邮箱验证状态(1=已验证,0=未验证)', '编辑账号', { inputValue: row.emailVerified+'' }).catch(() => null)
      if (!ok) return
      const emailVerified = parseInt(ok.value, 10) === 1 ? 1 : 0
      const payload = {
        userType: this.tmp[row.id]?.userType ?? row.userType,
        status: this.tmp[row.id]?.status ?? row.status,
        emailVerified
      }
      try {
        this.$set ? this.$set(this.rowLoading, row.id, true) : (this.rowLoading[row.id] = true)
        await updateUser(row.id, payload)
        this.$message.success('保存成功')
        this.load(this.query.pageNum)
      } catch (e) {
        this.$message.error(e.message || '保存失败')
      } finally {
        this.$set ? this.$set(this.rowLoading, row.id, false) : (this.rowLoading[row.id] = false)
      }
    },
    async openEditProfile(row) {
      // 管理员编辑资料，弹出两个prompt简化演示（可改为对话框表单）
      const nicknameRes = await this.$prompt('请输入昵称', '编辑资料', { inputValue: row.nickname || '' }).catch(() => null)
      if (!nicknameRes) return
      const bioRes = await this.$prompt('请输入简介', '编辑资料', { inputValue: row.bio || '' }).catch(() => null)
      if (!bioRes) return
      const payload = { nickname: nicknameRes.value, bio: bioRes.value }
      try {
        this.$set ? this.$set(this.rowLoading, row.id, true) : (this.rowLoading[row.id] = true)
        await updateUserProfileByAdmin(row.id, payload)
        this.$message.success('资料已更新')
        this.load(this.query.pageNum)
      } catch (e) {
        this.$message.error(e.message || '资料更新失败')
      } finally {
        this.$set ? this.$set(this.rowLoading, row.id, false) : (this.rowLoading[row.id] = false)
      }
    },
    async resetAvatar(row) {
      try {
        this.$set ? this.$set(this.rowLoading, row.id, true) : (this.rowLoading[row.id] = true)
        await resetAvatarToDefault(row.id)
        ElMessage.success('头像已重置')
        this.load(this.query.pageNum)
      } catch (e) { ElMessage.error(e.message || '操作失败') }
      finally {
        this.$set ? this.$set(this.rowLoading, row.id, false) : (this.rowLoading[row.id] = false)
      }
    },
    async load(page) {
      try {
        if (typeof page === 'number') this.query.pageNum = page
        this.loading = true
        const [accRes, profRes] = await Promise.all([pageUsers(this.query), listAllProfiles()])
        const d = accRes.data || {}
        const profileArr = (profRes && profRes.data) || []
        const userIdToProfile = {}
        profileArr.forEach(p => { userIdToProfile[p.userId] = p })
        const accs = d.records || []
        this.items = accs.map(a => {
          const p = userIdToProfile[a.id] || {}
          return { ...a, nickname: p.nickname || '', bio: p.bio || '', avatarUrl: p.avatarUrl || '' }
        })
        this.total = d.total || 0
        if (typeof d.current === 'number') this.query.pageNum = d.current
        if (typeof d.size === 'number') this.query.pageSize = d.size
        // 初始化临时编辑值
      const t = {}
      this.items.forEach(u => { t[u.id] = { userType: u.userType, status: u.status } })
        this.tmp = t
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
      } finally {
        this.loading = false
      }
    },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getFullYear()}-${(d.getMonth()+1+'').padStart(2,'0')}-${(d.getDate()+'').padStart(2,'0')} ${(d.getHours()+'').padStart(2,'0')}:${(d.getMinutes()+'').padStart(2,'0')}`
    },
    async saveRow(row) {
      try {
        const payload = { userType: this.tmp[row.id].userType, status: this.tmp[row.id].status, emailVerified: row.emailVerified }
        await updateUser(row.id, payload)
        ElMessage.success('保存成功')
        this.load(this.query.current)
      } catch (e) {
        ElMessage.error(e.message || '保存失败')
      }
    },
    async handleToggleStatus(row, val) {
      try {
        this.$set ? this.$set(this.rowLoading, row.id, true) : (this.rowLoading[row.id] = true)
        const payload = { userType: row.userType, status: val, emailVerified: row.emailVerified }
        await updateUser(row.id, payload)
        row.status = val
        ElMessage.success('状态已更新')
      } catch (e) {
        ElMessage.error(e.message || '更新失败')
      } finally {
        this.$set ? this.$set(this.rowLoading, row.id, false) : (this.rowLoading[row.id] = false)
      }
    },
    async resetPwd(row) {
      try {
        this.$set ? this.$set(this.rowLoading, row.id, true) : (this.rowLoading[row.id] = true)
        const { value } = await ElMessageBox.prompt('请输入新密码', '重置密码', { inputType: 'password' })
        await resetUserPassword(row.id, { newPassword: value })
        ElMessage.success('已重置')
      } catch (e) {
        if (e) ElMessage.error(e.message || '重置失败')
      } finally {
        this.$set ? this.$set(this.rowLoading, row.id, false) : (this.rowLoading[row.id] = false)
      }
    }
  },
  mounted() { this.load(1) }
}
</script>


