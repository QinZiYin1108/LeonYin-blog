<template>
  <div class="site-footer">
    <div class="inner">
      <div>© {{ year }} LeonYin Blog · Powered by Vue3 & Element Plus</div>
      <div v-if="icpNumber" class="icp">
        <a
          :href="miitUrl"
          target="_blank"
          rel="noopener noreferrer"
        >{{ icpNumber }}</a>
      </div>
    </div>
  </div>
 </template>

<script>
export default {
  name: 'SiteFooter',
  data() {
    return {
      year: new Date().getFullYear(),
      miitUrl: 'https://beian.miit.gov.cn'
    }
  },
  computed: {
    icpNumber() {
      return process.env.VUE_APP_ICP || ''
    }
  }
}
</script>

<style scoped>
.site-footer { padding: 0; }
.inner { max-width: 1080px; margin: 0 auto; text-align: center; color: #888; padding: 12px 16px; }
.icp { margin-top: 4px; }
.icp a { color: #888; text-decoration: none; }
.icp a:hover { text-decoration: underline; }
</style>


