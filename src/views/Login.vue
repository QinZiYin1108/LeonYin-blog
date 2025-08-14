<template>
  <div class="login">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>登录</span>
          <el-segmented v-model="mode" :options="[{label:'密码登录',value:'password'},{label:'验证码登录',value:'code'}]" size="small"/>
        </div>
      </template>
      <el-form :model="form" label-position="top" @submit.prevent>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" type="email" placeholder="you@example.com" clearable />
        </el-form-item>

        <el-form-item v-if="mode==='password'" label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item v-else label="邮箱验证码">
          <el-input v-model="form.code" placeholder="6位验证码">
            <template #append>
              <el-button :disabled="sending || countdown>0 || !form.email" @click="onSendCode" type="success" plain>
                {{ countdown>0 ? countdown + 's' : (sending ? '发送中...' : '发送验证码') }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-checkbox v-model="agree" class="agree">
          我已阅读并同意
          <a href="javascript:void(0)" @click="showPolicy=true">《用户协议》</a>
          和
          <a href="javascript:void(0)" @click="showPrivacy=true">《隐私政策》</a>
        </el-checkbox>
        <el-button type="primary" class="submit" @click="onSubmit" :loading="submitting" :disabled="!agree">登录</el-button>

        <div class="links">
          <router-link to="/register">去注册</router-link>
          <span> | </span>
          <router-link to="/forgot-password">忘记密码？</router-link>
        </div>

        <el-alert v-if="error" :title="error" type="error" show-icon class="mt8" />
      </el-form>
    </el-card>
    <el-dialog v-model="showPolicy" title="用户协议" width="720px">
      <div style="max-height:50vh;overflow:auto;white-space:pre-wrap;">
    <h1>用户协议</h1>
    <div class="last-updated">最后更新日期：[2025年08月14日]</div>

    <h2>1. 接受条款</h2>
    <p>访问或注册本博客（以下简称"本网站"），即表示您同意遵守本协议的全部内容。如果您不同意，请立即停止使用。</p>

    <h2>2. 服务说明</h2>
    <p>本网站为个人非营利性博客，由管理员（以下简称"我们"）独立运营，内容主要为技术分享与生活记录。</p>
    <p>注册用户可对文章进行点赞、收藏等互动操作。</p>

    <h2>3. 用户义务</h2>
    <p>您承诺：</p>
    <ul>
        <li>注册时提供真实有效的邮箱地址；</li>
        <li>不发表违法、侵权或骚扰性内容；</li>
        <li>不进行任何干扰网站正常运行的行为（如爬虫、攻击等）。</li>
    </ul>

    <h2>4. 免责声明</h2>
    <p>网站文章仅为个人观点，不构成专业建议；</p>
    <p>因网络故障、维护等造成的服务中断，我们不承担责任；</p>
    <p>用户因使用本网站产生的任何风险自行负责。</p>

    <h2>5. 协议变更</h2>
    <p>我们保留修改本协议的权利，修改后将通过网站公告通知。继续使用视为接受变更。</p>

    <div class="contact">
        <strong>联系方式：</strong>如有疑问，请联系邮箱 <a href="mailto:[3606793447@qq.com]">[3606793447@qq.com]</a>。
    </div>
      </div>
    </el-dialog>
    <el-dialog v-model="showPrivacy" title="隐私政策" width="720px">
      <div style="max-height:50vh;overflow:auto;white-space:pre-wrap;">


    <!-- ---------------------------------------------------------------------------------------------------------------- -->

    
    <h1 style="margin-top: 50px;">隐私政策</h1>
    <div class="last-updated">最后更新日期：[2025年08月14日]</div>

    <h2>1. 收集的信息</h2>
    <p>我们可能收集以下信息：</p>
    <ul>
        <li><strong>注册信息：</strong>邮箱地址（用于账号验证和功能操作）；</li>
        <li><strong>自动收集信息：</strong>IP地址、浏览器类型、访问时间（用于基础运维和反滥用）；</li>
        <li><strong>用户生成数据：</strong>点赞、收藏记录（仅用于功能实现）。</li>
    </ul>

    <h2>2. 信息用途</h2>
    <p>您的数据仅用于：</p>
    <ul>
        <li>提供博客互动功能；</li>
        <li>保障账号安全；</li>
        <li>优化网站体验（如分析热门文章）。</li>
    </ul>

    <h2>3. 数据共享</h2>
    <p>我们承诺：</p>
    <ul>
        <li>不会出售、出租您的个人信息；</li>
        <li>仅在法律要求时向相关部门提供数据。</li>
    </ul>

    <h2>4. 数据存储与安全</h2>
    <p>数据存储在 中国境内 的服务器上；</p>
    <p>采取合理安全措施（如密码加密），但无法保证绝对安全。</p>

    <h2>5. 您的权利</h2>
    <p>您可以：</p>
    <ul>
        <li><strong>删除账号：</strong>您可自主选择注销账号</li>
        <li><strong>拒绝Cookie：</strong>通过浏览器设置禁用，但可能影响部分功能。</li>
    </ul>

    <h2>6. 儿童隐私</h2>
    <p>本网站不面向13岁以下儿童，不会故意收集其信息。</p>

    <div class="contact">
        <strong>联系方式：</strong>隐私相关问题请联系 <a href="mailto:[3606793447@qq.com]">[3606793447@qq.com]</a>。
    </div>

      </div>
    </el-dialog>
  </div>
  </template>

<script>
import { login, sendCode } from '../api/auth'

export default {
  name: 'LoginPage',
  data() {
    return {
      mode: 'password',
      form: { email: '', password: '', code: '' },
      error: '',
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
    async onSubmit() {
      this.error = ''
      if (!this.form.email) { this.error = '请填写邮箱'; return }
      if (!this.agree) { this.error = '请先阅读并同意用户协议与隐私政策'; return }
      this.submitting = true
      try {
        const isPassword = this.mode === 'password'
        const payload = {
          loginType: isPassword ? 2 : 1,
          email: this.form.email,
          deviceId: 'web',
          deviceInfo: navigator.userAgent
        }
        if (isPassword) payload.password = this.form.password
        else payload.verificationCode = this.form.code

        const res = await login(payload)
        if (res && res.data && res.data.token) {
          const token = res.data.token
          localStorage.setItem('token', token)
          const user = res.data.user || res.data.userInfo
          let role = (user && user.account && (user.account.role || (user.account.userType === 1 ? 'ADMIN' : 'USER'))) || (user && user.role)
          if (!role && token && token.split('.').length === 3) {
            try {
              const payloadStr = atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
              const payload = JSON.parse(payloadStr)
              role = payload.role || (payload.userType === 1 ? 'ADMIN' : 'USER')
            } catch (e) { /* ignore */ }
          }
          if (role === 'ADMIN') this.$router.push('/admin')
          else this.$router.push('/')
        } else if (res && res.data && res.data.needVerification) {
          // 密码登录首次设备需验证码时，切换到验证码登录模式
          this.mode = 'code'
          this.error = res.data.message || '首次在该设备登录，需要邮箱验证码'
        }
      } catch (e) {
        this.error = e.message || '登录失败'
      } finally {
        this.submitting = false
      }
    },
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
        if (this.countdown <= 1) {
          clearInterval(this.timer)
          this.countdown = 0
        } else {
          this.countdown -= 1
        }
      }, 1000)
    }
  },
  beforeUnmount() { clearInterval(this.timer) }
}
</script>

 

<style scoped>
.login { max-width: 420px; margin: 80px auto; }
.box-card { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.submit { width: 100%; margin-top: 8px; }
.links { margin-top: 12px; text-align: right; }
.links a { color: #409eff; text-decoration: none; }
.mt8 { margin-top: 8px; }
</style>


