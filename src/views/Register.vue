<template>
  <div class="register">
    <el-card class="box-card">
      <template #header>
        <div class="card-header"><span>注册</span></div>
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
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-checkbox v-model="agree" class="agree">
          我已阅读并同意
          <a href="javascript:void(0)" @click="showPolicy=true">《用户协议》</a>
          和
          <a href="javascript:void(0)" @click="showPrivacy=true">《隐私政策》</a>
        </el-checkbox>
        <el-button type="primary" class="submit" @click="onSubmit" :loading="submitting" :disabled="!agree">注册</el-button>
        <div class="links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
        <el-alert v-if="error" :title="error" type="error" show-icon class="mt8" />
        <el-alert v-if="success" title="注册成功，请前往登录" type="success" show-icon class="mt8" />
      </el-form>
    </el-card>
    <el-dialog v-model="showPolicy" title="用户协议" width="720px">
      <div style="max-height:50vh;overflow:auto;white-space:pre-wrap;">欢迎使用本博客服务。您应当遵守法律法规，文明发言，不得发布违法违规内容。</div>
    </el-dialog>
    <el-dialog v-model="showPrivacy" title="隐私政策" width="720px">
      <div style="max-height:50vh;overflow:auto;white-space:pre-wrap;">我们仅在必要范围内收集和使用您的信息，用于账户注册、登录与安全目的，更多细则以正式隐私政策为准。</div>
    </el-dialog>
  </div>
</template>

<script>
import { register, sendCode } from '../api/auth'

export default {
  name: 'RegisterPage',
  data() {
    return {
      form: { email: '', code: '', password: '', confirmPassword: '' },
      error: '',
      success: false,
      sending: false,
      countdown: 0,
      timer: null,
      submitting: false,
      agree: false,
      showPolicy: false,
      showPrivacy: false
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
      if (!this.agree) { this.error = '请先阅读并同意用户协议与隐私政策'; return }
      if (this.form.password !== this.form.confirmPassword) { this.error = '两次输入的密码不一致'; return }
      this.submitting = true
      try {
        await register({ email: this.form.email, password: this.form.password, verificationCode: this.form.code, registerType: 0 })
        this.success = true
        setTimeout(() => this.$router.push('/login'), 1200)
      } catch (e) {
        this.error = e.message || '注册失败'
      } finally {
        this.submitting = false
      }
    }
  },
  beforeUnmount() { clearInterval(this.timer) }
}
</script>

 

<style scoped>
.register { max-width: 420px; margin: 80px auto; }
.box-card { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.submit { width: 100%; margin-top: 8px; }
.links { margin-top: 12px; text-align: right; }
.links a { color: #409eff; text-decoration: none; }
.mt8 { margin-top: 8px; }
</style>


