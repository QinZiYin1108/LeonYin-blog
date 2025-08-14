<template>
  <div class="forgot">
    <el-card class="box-card">
      <template #header>
        <div class="card-header"><span>找回密码</span></div>
      </template>
      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" type="email" placeholder="you@example.com" clearable />
        </el-form-item>
        <el-form-item label="邮箱验证码">
          <el-input v-model="form.code" placeholder="6位验证码">
            <template #append>
              <el-button :disabled="sending || countdown>0 || !form.email" @click="onSendCode" type="success" plain>
                {{ countdown>0 ? countdown + 's' : (sending ? '发送中...' : '发送验证码') }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-button type="primary" class="submit" @click="onSubmit" :loading="submitting">提交</el-button>
        <div class="links">
          <router-link to="/login">返回登录</router-link>
        </div>
        <el-alert v-if="error" :title="error" type="error" show-icon class="mt8" />
        <el-alert v-if="success" title="重置成功，请使用新密码登录" type="success" show-icon class="mt8" />
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { forgotPassword, sendCode } from '../api/auth'

export default {
  name: 'ForgotPasswordPage',
  data() {
    return {
      form: { email: '', code: '', password: '', confirmPassword: '' },
      error: '',
      success: false,
      sending: false,
      countdown: 0,
      timer: null,
      submitting: false
    }
  },
  methods: {
    async onSendCode() {
      if (!this.form.email) { this.error = '请先填写邮箱'; return }
      this.error = ''
      this.sending = true
      try {
        await sendCode(this.form.email)
        this.startCountdown()
      } catch (e) {
        this.error = e.message || '发送验证码失败'
      } finally {
        this.sending = false
      }
    },
    startCountdown() {
      this.countdown = 60
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        if (this.countdown <= 1) { clearInterval(this.timer); this.countdown = 0 } else { this.countdown -= 1 }
      }, 1000)
    },
    async onSubmit() {
      this.error = ''
      this.success = false
      if (this.form.password !== this.form.confirmPassword) { this.error = '两次输入的密码不一致'; return }
      this.submitting = true
      try {
        await forgotPassword({ email: this.form.email, verificationCode: this.form.code, newPassword: this.form.password })
        this.success = true
        setTimeout(() => this.$router.push('/login'), 1200)
      } catch (e) {
        this.error = e.message || '重置失败'
      } finally {
        this.submitting = false
      }
    }
  },
  beforeUnmount() { clearInterval(this.timer) }
}
</script>

<style scoped>
.forgot { max-width: 420px; margin: 80px auto; }
.box-card { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.submit { width: 100%; margin-top: 8px; }
.links { margin-top: 12px; text-align: right; }
.links a { color: #409eff; text-decoration: none; }
.mt8 { margin-top: 8px; }
</style>


