<template>
  <div class="editor-page">
    <el-page-header @back="$router.back()" :content="pageTitle" />

    <el-card class="mt16" :loading="loading">
      <el-form :model="form" label-width="96px">
        <el-form-item label="标题"><el-input v-model="form.title"/></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary"/></el-form-item>
        <el-form-item label="封面">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="form.coverImageId" placeholder="封面图片ID" style="width:260px"/>
            <el-upload :http-request="uploadCover" :show-file-list="false" accept="image/*"><el-button>上传封面</el-button></el-upload>
          </div>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" filterable style="width: 260px">
            <el-option v-for="c in categoryOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagList" multiple filterable allow-create default-first-option placeholder="输入后回车创建 或 选择已有"
                     style="width: 100%">
            <el-option v-for="t in tagOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <Toolbar :editor="editor" style="border-bottom:1px solid #eee" :defaultConfig="toolbarConfig" />
          <Editor v-model="form.content" style="height:420px;" :defaultConfig="editorConfig" @onCreated="onCreated" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option :value="1" label="发布"/>
            <el-option :value="0" label="草稿"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getArticleDetail, createArticle, updateArticle } from '../../api/adminArticle'
import { listCategories } from '../../api/adminCategory'
import http from '../../utils/http'
import { ElMessage } from 'element-plus'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

export default {
  name: 'ArticleEditor',
  components: { Editor, Toolbar },
  data() {
    return {
      loading: false,
      saving: false,
      form: { id: '', title: '', summary: '', content: '', coverImageId: '', categoryId: '', tagList: [], status: 1 },
      editor: null,
      toolbarConfig: { excludeKeys: ['insertImage'] },
      editorConfig: {
        placeholder: '请输入内容...',
        MENU_CONF: {
          uploadImage: {
            // 使用自定义上传，避免后端响应格式与 wangeditor 期望不一致
            customUpload: async (file, insertFn) => {
              try {
                const fd = new FormData()
                fd.append('file', file, file.name)
                fd.append('folder', 'article-images')
                fd.append('usageType', 4)
                const resp = await http.post('/file/upload/image', fd)
                const data = resp && resp.data ? resp.data : null
                const url = data && data.url
                if (!url) throw new Error('上传失败')
                insertFn(url, '', '')
              } catch (e) {
                ElMessage.error(e.message || '上传失败')
              }
            },
            // 其他可选限制
            maxFileSize: 5 * 1024 * 1024,
            allowedFileTypes: ['image/*']
          }
        }
      },
      categoryOptions: [],
      tagOptions: []
    }
  },
  computed: {
    pageTitle() { return this.form.id ? '编辑文章' : '创建文章' }
  },
  methods: {
    async init() {
      const id = this.$route.query.id
      // 加载分类选项
      try {
        const res = await listCategories(); this.categoryOptions = res.data || []
      } catch (e) { console.debug(e) }
      if (!id) return
      try {
        this.loading = true
        const res = await getArticleDetail(id)
        const a = (res && res.data) || {}
        let tagList = []
        try { tagList = Array.isArray(a.tags) ? a.tags : (a.tags ? JSON.parse(a.tags) : []) } catch (_) { tagList = [] }
        this.form = { id: a.id, title: a.title, summary: a.summary, content: a.content, coverImageId: a.coverImageId, categoryId: a.categoryId, tagList, status: a.status }
      } catch (e) { ElMessage.error(e.message || '加载失败') }
      finally { this.loading = false }
    },
    async uploadCover({ file, onError, onSuccess }) {
      try {
        const fd = new FormData(); fd.append('file', file, file.name); fd.append('folder','article-covers'); fd.append('usageType',3)
        const resp = await http.post('/file/upload/image', fd)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.form.coverImageId = data.imageId
        ElMessage.success('封面已上传')
        onSuccess && onSuccess(resp)
      } catch (e) { onError && onError(e); ElMessage.error(e.message || '上传失败') }
    },
    onCreated(ed) { this.editor = ed },
    async uploadImageForContent({ file, onError, onSuccess }) {
      try {
        const fd = new FormData(); fd.append('file', file, file.name); fd.append('folder','article-images'); fd.append('usageType',4)
        const resp = await http.post('/file/upload/image', fd)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.url) throw new Error('上传失败')
        if (this.editor) this.editor.insertNode({ type: 'image', src: data.url, alt: '', href: '' })
        onSuccess && onSuccess(resp)
      } catch (e) { ElMessage.error(e.message || '上传失败'); onError && onError(e) }
    },
    async uploadFileForContent({ file, onError, onSuccess }) {
      try {
        const fd = new FormData(); fd.append('file', file, file.name); fd.append('folder','article-files'); fd.append('usageType',5)
        const resp = await http.post('/file/upload/document', fd)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.url) throw new Error('上传失败')
        if (this.editor) this.editor.insertText(` 附件：${data.url} `)
        onSuccess && onSuccess(resp)
      } catch (e) { ElMessage.error(e.message || '上传失败'); onError && onError(e) }
    },
    async onSave() {
      try {
        this.saving = true
        const base = { title: this.form.title, summary: this.form.summary, content: this.form.content, coverImageId: this.form.coverImageId, categoryId: this.form.categoryId, tags: JSON.stringify(this.form.tagList||[]) }
        if (!this.form.id) {
          await createArticle(base)
        } else {
          const updatePayload = { ...base, status: this.form.status, isTop: this.form.isTop }
          await updateArticle(this.form.id, updatePayload)
        }
        ElMessage.success('已保存')
        this.$router.push('/admin/articles')
      } catch (e) { ElMessage.error(e.message || '保存失败') }
      finally { this.saving = false }
    }
  },
  mounted() { this.init() }
}
</script>

<style scoped>
.editor-page { max-width: 1040px; margin: 16px auto; }
.mt16 { margin-top: 16px; }
</style>


