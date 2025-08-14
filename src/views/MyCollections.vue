<template>
  <div class="my-collections">
    <el-page-header @back="$router.back()" content="我的收藏" />
    <div class="content" :class="{loading}">
      <div v-if="items.length===0 && !loading" class="empty">暂无收藏</div>
      <ul class="items">
        <li v-for="it in items" :key="it.id" class="item" @click="goDetail(it.id)">
          <h3 class="title">{{ it.title }}</h3>
          <p class="summary">{{ it.summary }}</p>
          <div class="meta">
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
          @current-change="(p)=>load(p)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { pageMyCollections } from '../api/article'
import { ElMessage } from 'element-plus'
export default {
  name: 'MyCollections',
  data() { return { items: [], total: 0, current: 1, size: 10, loading: false } },
  methods: {
    async load(page=1) {
      try {
        this.loading = true
        const res = await pageMyCollections({ current: page, size: this.size })
        const data = res && res.data ? res.data : { records: [], total: 0 }
        this.items = data.records || []
        this.total = data.total || 0
        this.current = data.current || page
        this.size = data.size || this.size
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
        try {
          if (e && (e.code === 401 || /未登录|Unauthorized/i.test(e.message || ''))) {
            this.$router.push('/login')
          }
        } catch (_) { return }
      } finally { this.loading = false }
    },
    goDetail(id) { this.$router.push({ path: '/article', query: { id } }) },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(Number(ts))
      return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
    }
  },
  mounted() { this.load(1) }
}
</script>

<style scoped>
.my-collections { max-width: 860px; margin: 16px auto; padding: 0 12px; }
.content { min-height: calc(100vh - 160px); display:flex; flex-direction: column; }
.items { list-style:none; padding:0; margin:0; flex:1; }
.item { padding:12px 0; border-bottom:1px solid #eee; cursor:pointer; }
.title { margin: 0; }
.summary { color:#666; font-size:14px; }
.meta { color:#999; font-size:12px; display:flex; gap:16px; }
.pager { display:flex; justify-content:center; margin-top:auto; padding-top:12px; }
</style>


