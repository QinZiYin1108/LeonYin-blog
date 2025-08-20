<template>
  <div class="list">
    <header class="toolbar" v-if="false"></header>

    <div v-if="loading">加载中...</div>
    <div v-else class="content">
      <div v-if="items.length === 0" class="empty">暂无文章</div>
      <ul class="items">
        <li v-for="it in items" :key="it.id" class="item" @click="goDetail(it.id)">
          <h3 class="title">{{ it.title }}</h3>
          <p class="summary">{{ it.summary }}</p>
          <div class="meta">
            <span>分类: {{ it.categoryName || categoryNameMap[it.categoryId] || '-' }}</span>
            <span>点赞: {{ it.likeCount ?? 0 }}</span>
            <span>收藏: {{ it.collectCount ?? 0 }}</span>
            <span>发布时间: {{ formatTime(it.publishTime) }}</span>
          </div>
        </li>
      </ul>
      <div class="pager">
        <button :disabled="current<=1" @click="fetchData(current-1)">上一页</button>
        <span>{{ current }} / {{ pages }}</span>
        <button :disabled="current>=pages" @click="fetchData(current+1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { pageArticles } from '../api/article'

export default {
  name: 'ArticleList',
  data() {
    return {
      items: [],
      current: 1,
      size: 10,
      total: 0,
      pages: 1,
      keyword: this.$route.query.keyword || '',
      loading: false,
      categoryNameMap: {}
    }
  },
  watch: {
    '$route.query.keyword'(v) {
      this.keyword = v || ''
      this.fetchData(1)
    }
  },
  mounted() { this.loadCategories(); this.fetchData(1) },
  methods: {
    async loadCategories() {
      try {
        const api = await import('../api/category')
        const res = await api.listEnabledCategories()
        const list = (res && res.data) || []
        const map = {}
        list.forEach(c => { if (c && c.id) map[c.id] = c.name })
        this.categoryNameMap = map
      } catch (e) { /* ignore */ }
    },
    async fetchData(page) {
      this.loading = true
      try {
        const res = await pageArticles({ current: page, size: this.size, keyword: this.keyword || null })
        const data = res.data || {}
        this.items = data.records || []
        this.total = data.total || 0
        this.current = data.current || page
        this.size = data.size || this.size
        this.pages = data.pages || Math.max(1, Math.ceil(this.total / this.size))
      } catch (e) {
        alert('加载失败：' + (e.message || JSON.stringify(e)))
      } finally {
        this.loading = false
      }
    },
    goDetail(id) {
      if (!id) return
      this.$router.push({ path: '/article', query: { id } })
    },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
    }
  }
}
</script>

<style scoped>
.list { max-width: 860px; margin: 24px auto; padding: 0 16px; }
.content { min-height: calc(100vh - 160px); display: flex; flex-direction: column; }
.toolbar { display: flex; gap: 8px; margin-bottom: 12px; }
.toolbar input { flex: 1; height: 34px; padding: 0 10px; border: 1px solid #ddd; border-radius: 4px; }
.toolbar button { padding: 6px 12px; }
.login-link { margin-left: auto; text-decoration: none; font-size: 14px; }
.items { list-style: none; padding: 0; margin: 0; flex: 1; }
.item { padding: 12px 0; border-bottom: 1px solid #eee; cursor: pointer; }
.title { margin: 0; }
.summary { color: #666; font-size: 14px; }
.meta { color: #999; font-size: 12px; display: flex; gap: 16px; }
.pager { display: flex; gap: 8px; align-items: center; justify-content: center; margin-top: auto; padding-top: 16px; }
</style>


