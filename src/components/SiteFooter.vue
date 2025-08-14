<template>
  <div class="site-footer">
    <div class="inner">© {{ year }} LeonYin Blog · Powered by Vue3 & Element Plus</div>
  </div>
 </template>

<script>
export default {
  name: 'SiteFooter',
  data() { return { year: new Date().getFullYear() } }
}
</script>

<style scoped>
.site-footer { padding: 0; }
.inner { max-width: 1080px; margin: 0 auto; text-align: center; color: #888; padding: 12px 16px; }
</style>


