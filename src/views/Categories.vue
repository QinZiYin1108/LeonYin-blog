<template>
  <div class="categories-page">
    <div class="grid">
      <div v-for="c in categories" :key="c.id" class="card" @click="selectCategory(c)">
        <img v-if="c.iconUrl || c.iconImageId" class="icon" :src="c.iconUrl || getIconUrl(c.iconImageId)" alt="icon" />
        <div class="name">{{ c.name }}</div>
        <div class="desc">{{ c.description || '—' }}</div>
      </div>
    </div>

    <div class="article-list">
      <div class="head">
        <div class="title">{{ currentCategory ? '分类：' + currentCategory.name : '全部文章' }}</div>
        <el-button v-if="currentCategory" size="small" @click="clearCategory">清除筛选</el-button>
      </div>
      <ul class="items">
        <li v-for="it in items" :key="it.id" class="item" @click="goDetail(it.id)">
          <h3 class="title">{{ it.title }}</h3>
          <p class="summary">{{ it.summary }}</p>
          <div class="meta">
            <span>分类: {{ it.categoryName || categoryNameMap[it.categoryId] || '-' }}</span>
            <span>热度: {{ it.hotScore ?? '-' }}</span>
            <span>点赞: {{ it.likeCount ?? 0 }}</span>
            <span>收藏: {{ it.collectCount ?? 0 }}</span>
            <span>发布时间: {{ formatTime(it.publishTime) }}</span>
          </div>
        </li>
      </ul>
      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :total="total"
          :page-size="size"
          :current-page="current"
          @current-change="(p)=>fetchArticles(p)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { listEnabledCategories } from '../api/category'
import { pageArticles } from '../api/article'
export default {
  name: 'CategoriesPage',
  data() {
    return {
      categories: [],
      categoryNameMap: {},
      currentCategory: null,
      items: [],
      total: 0,
      current: 1,
      size: 10,
      loading: false
    }
  },
  methods: {
    getIconUrl(id) { return `/api/file/image/${id}` },
    async loadCategories() {
      const res = await listEnabledCategories()
      this.categories = res.data || []
      const map = {}
      this.categories.forEach(c => { if (c && c.id) map[c.id] = c.name })
      this.categoryNameMap = map
    },
    async fetchArticles(page = 1) {
      this.loading = true
      try {
        const payload = { current: page, size: this.size, keyword: null }
        if (this.currentCategory && this.currentCategory.id) payload.categoryId = this.currentCategory.id
        const res = await pageArticles(payload)
        const data = res.data || {}
        this.items = data.records || []
        this.total = data.total || 0
        this.current = data.current || page
        this.size = data.size || this.size
      } finally { this.loading = false }
    },
    selectCategory(c) {
      this.currentCategory = c
      this.fetchArticles(1)
    },
    clearCategory() {
      this.currentCategory = null
      this.fetchArticles(1)
    },
    goDetail(id) { this.$router.push({ path: '/article', query: { id } }) },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
    }
  },
  async mounted() {
    await this.loadCategories()
    await this.fetchArticles(1)
  }
}
</script>

<style scoped>
.categories-page { max-width: 1080px; margin: 16px auto; padding: 0 12px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; }
.card { border: 1px solid #eee; border-radius: 8px; padding: 12px; cursor: pointer; display:flex; flex-direction:column; align-items:center; }
.icon { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; margin-bottom: 8px; }
.name { font-weight: 600; }
.desc { color: #888; font-size: 12px; text-align: center; }
.article-list { margin-top: 16px; min-height: calc(100vh - 260px); display:flex; flex-direction: column; }
.items { list-style: none; padding: 0; margin: 0; flex: 1; }
.item { padding: 12px 0; border-bottom: 1px solid #eee; cursor: pointer; }
.title { margin: 0; }
.summary { color: #666; font-size: 14px; }
.meta { color: #999; font-size: 12px; display: flex; gap: 16px; }
.pager { display:flex; justify-content:center; margin-top: auto; padding-top: 12px; }
.head { display:flex; align-items:center; justify-content: space-between; margin-bottom: 8px; }
</style>


