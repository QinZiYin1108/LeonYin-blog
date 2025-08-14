<template>
  <div class="detail-page">
    <el-page-header @back="$router.back()" content="文章详情" />

    <el-card class="mt16" :loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ data.title }}</el-descriptions-item>
        <el-descriptions-item label="分类ID">{{ data.categoryId }}</el-descriptions-item>
        <el-descriptions-item label="作者ID">{{ data.authorId }}</el-descriptions-item>
        <el-descriptions-item label="封面ID">{{ data.coverImageId }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ data.status===1?'发布':'草稿' }}</el-descriptions-item>
        <el-descriptions-item label="置顶">{{ data.isTop===1?'是':'否' }}</el-descriptions-item>
        <el-descriptions-item label="发布/创建时间">{{ formatTime(data.publishTime || data.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>热度信息</el-divider>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="浏览">{{ data.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="点赞">{{ data.likeCount }}</el-descriptions-item>
        <el-descriptions-item label="收藏">{{ data.collectCount }}</el-descriptions-item>
        <el-descriptions-item label="热度分">{{ data.hotScore }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>正文</el-divider>
      <v-md-editor v-model="content" mode="preview" />

      
    </el-card>
  </div>
</template>

<script>
import { getArticleDetail } from '../../api/adminArticle'
import { ElMessage } from 'element-plus'
import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import Prism from 'prismjs'
VMdEditor.use(githubTheme, { Prism })

export default {
  name: 'ArticleDetail',
  components: { VMdEditor },
  data() {
    return { loading: false, data: {}, content: '' }
  },
  methods: {
    formatTime(ts) { if (!ts) return '-'; const d = new Date(ts); return `${d.getFullYear()}-${(d.getMonth()+1+'').padStart(2,'0')}-${(d.getDate()+'').padStart(2,'0')}` },
    async init() {
      const id = this.$route.query.id
      if (!id) return
      try {
        this.loading = true
        const res = await getArticleDetail(id)
        const a = res && res.data ? res.data : {}
        this.data = a
        this.content = a.content || ''
      } catch (e) { ElMessage.error(e.message || '加载失败') }
      finally { this.loading = false }
    }
  },
  mounted() { this.init() }
}
</script>

<style scoped>
.detail-page { max-width: 1040px; margin: 16px auto; }
.mt16 { margin-top: 16px; }
</style>


