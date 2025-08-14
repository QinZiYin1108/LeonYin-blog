<template>
  <div class="profile">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">个人中心</div>
      </template>

      <el-tabs v-model="active">
        <el-tab-pane label="我的账号" name="account">
          <el-form :model="form" label-width="96px">
            <el-form-item label="头像">
              <div class="avatar-wrap">
                <el-avatar :size="80" :src="avatarUrl" />
                <el-upload
                  class="uploader"
                  :http-request="uploadRequest"
                  :show-file-list="false"
                  accept="image/*">
                  <el-button type="primary" plain>更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="20" />
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="200" placeholder="一句话介绍自己" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
              <el-popconfirm title="确认注销账号？该操作不可恢复" confirm-button-text="确认" cancel-button-text="取消" @confirm="onDeleteAccount">
                <template #reference>
                  <el-button type="danger" plain style="margin-left: 12px;">注销账号</el-button>
                </template>
              </el-popconfirm>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form label-width="96px">
            <el-form-item label="旧密码">
              <el-input v-model="pwd.oldPassword" type="password" show-password placeholder="旧密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwd.newPassword" type="password" show-password placeholder="新密码(6-20位)" />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="changing" @click="onChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getProfile, updateProfile, updateAvatar, changePassword, deleteAccount } from '../api/user'
import http from '../utils/http'

export default {
  name: 'ProfilePage',
  data() {
    return {
      active: 'account',
      form: { email: '', nickname: '', bio: '' },
      avatarUrl: '',
      saving: false,
      pwd: { oldPassword: '', newPassword: '' },
      changing: false
    }
  },
  computed: {},
  methods: {
    async uploadRequest({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'avatars')
        form.append('usageType', 1)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.avatarUrl = data.url
        await updateAvatar(data.imageId)
        this.$message.success('头像更新成功')
        onSuccess && onSuccess(resp)
      } catch (e) {
        this.$message.error(e.message || '头像更新失败')
        onError && onError(e)
      }
    },
    async load() {
      try {
        const res = await getProfile()
        const d = res && res.data ? res.data : {}
        // 兼容不同返回结构
        const profile = d.profile || d.userProfileVO || d.userProfile || d
        const account = d.account || d.userAccountVO || d.userAccount || d
        this.form.email = account.email || ''
        this.form.nickname = profile.nickname || ''
        this.form.bio = profile.bio || ''
        this.avatarUrl = profile.avatarUrl || ''
      } catch (e) {
        this.$message.error(e.message || '加载资料失败')
      }
    },
    async onSave() {
      this.saving = true
      try {
        await updateProfile({ nickname: this.form.nickname, bio: this.form.bio })
        this.$message.success('保存成功')
      } catch (e) {
        this.$message.error(e.message || '保存失败')
      } finally {
        this.saving = false
      }
    },
    async onChangePassword() {
      if (!this.pwd.oldPassword || !this.pwd.newPassword) { this.$message.error('请填写旧密码和新密码'); return }
      this.changing = true
      try {
        await changePassword({ oldPassword: this.pwd.oldPassword, newPassword: this.pwd.newPassword })
        this.$message.success('密码修改成功，请使用新密码登录')
        this.pwd.oldPassword = ''
        this.pwd.newPassword = ''
      } catch (e) {
        this.$message.error(e.message || '修改失败')
      } finally {
        this.changing = false
      }
    },
    async onDeleteAccount() {
      try {
        await deleteAccount()
        this.$message.success('账号已注销')
        localStorage.removeItem('token')
        this.$router.push('/')
      } catch (e) {
        this.$message.error(e.message || '注销失败')
      }
    },
    
  },
  mounted() { this.load() }
}
</script>

<style scoped>
.profile { max-width: 720px; margin: 32px auto; }
.box-card { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.avatar-wrap { display: flex; align-items: center; gap: 12px; }
</style>


