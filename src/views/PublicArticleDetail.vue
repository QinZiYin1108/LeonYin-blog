<template>
  <div class="public-article-detail">
    <el-page-header @back="$router.back()" content="文章详情" />

    <el-card class="mt16" :loading="loading">
      <h2 class="title">{{ article.title }}</h2>
      <div class="meta">
        <span>作者：{{ article.authorName || '-' }}</span>
        <span>分类：{{ article.categoryName || '-' }}</span>
        <span>点赞：{{ likeCount }}</span>
        <span>收藏：{{ collectCount }}</span>
        <span>发布时间：{{ formatTime(article.publishTime) }}</span>
      </div>
      
      <div class="tags" v-if="article.tagList && article.tagList.length > 0">
        <el-tag v-for="tag in article.tagList" :key="tag" size="small" class="tag">{{ tag }}</el-tag>
      </div>
      
      <div class="content" v-html="article.content"></div>

      <div class="actions">
        <el-tooltip :content="liked ? '取消点赞' : '点赞'" placement="top">
          <el-button circle :type="liked?'primary':'default'" @click="toggleLike" :loading="doingLike">👍</el-button>
        </el-tooltip>
        <el-tooltip :content="collected ? '取消收藏' : '收藏'" placement="top">
          <el-button circle :type="collected?'success':'default'" @click="toggleCollect" :loading="doingCollect">⭐</el-button>
        </el-tooltip>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getArticleDetail } from '../api/article'
import { getCategoryById } from '../api/category'
import http from '../utils/http'
import { ElMessage } from 'element-plus'

export default {
  name: 'PublicArticleDetail',
  data() {
    return {
      loading: false,
      article: {},
      liked: false,
      collected: false,
      doingLike: false,
      doingCollect: false
    }
  },
  computed: {
    likeCount() { return this.article.likeCount ?? 0 },
    collectCount() { return this.article.collectCount ?? 0 }
  },
  methods: {
    async load() {
      const id = this.$route.query.id
      if (!id) return
      try {
        this.loading = true
        const res = await getArticleDetail(id)
        this.article = (res && res.data) || {}
        if (!this.article.categoryName && this.article.categoryId) {
          try {
            const c = await getCategoryById(this.article.categoryId)
            if (c && c.data) this.article.categoryName = c.data.name
          } catch (e) { /* ignore */ }
        }
        // 初始化已点赞/已收藏状态
        this.liked = !!this.article.liked
        this.collected = !!this.article.collected
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
      } finally { this.loading = false }
    },
    async toggleLike() {
      const id = this.article.id; if (!id) return
      try {
        this.doingLike = true
        if (!this.liked) {
          await http.post(`/article/${id}/like`)
          this.liked = true
          this.article.likeCount = (this.article.likeCount || 0) + 1
        } else {
          await http.delete(`/article/${id}/like`)
          this.liked = false
          this.article.likeCount = Math.max(0, (this.article.likeCount || 0) - 1)
        }
      } catch (e) {
        ElMessage.error(e.message || '操作失败')
      } finally { this.doingLike = false }
    },
    async toggleCollect() {
      const id = this.article.id; if (!id) return
      try {
        this.doingCollect = true
        if (!this.collected) {
          await http.post(`/article/${id}/collect`)
          this.collected = true
          this.article.collectCount = (this.article.collectCount || 0) + 1
        } else {
          await http.delete(`/article/${id}/collect`)
          this.collected = false
          this.article.collectCount = Math.max(0, (this.article.collectCount || 0) - 1)
        }
      } catch (e) {
        ElMessage.error(e.message || '操作失败')
      } finally { this.doingCollect = false }
    },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
    }
  },
  mounted() { this.load() }
}
</script>

<style scoped>
.public-article-detail { max-width: 860px; margin: 16px auto; padding: 0 12px; }
.mt16 { margin-top: 16px; }
.title { margin: 0 0 6px; }
.meta { color: #999; font-size: 12px; display:flex; gap:12px; margin-bottom: 8px; }
.cover img { max-width: 100%; border-radius: 4px; margin: 8px 0; }
.tags { margin: 10px 0; display: flex; flex-wrap: wrap; gap: 8px; }
.tag { margin-right: 5px; }
.content { margin-top: 16px; }
.actions { display:flex; gap:8px; margin-top: 12px; }
</style>


