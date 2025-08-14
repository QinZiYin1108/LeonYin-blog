<template>
  <div>
    <el-card :loading="loading">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
          <span>文章管理</span>
          <div style="display:flex;gap:8px;">
            <el-input v-model="query.keyword" placeholder="关键词" style="width:200px"/>
            <el-input v-model="query.categoryId" placeholder="分类ID(可空)" style="width:180px"/>
            <el-button type="primary" @click="load(1)">查询</el-button>
            <el-button @click="goCreate">创建文章</el-button>
          </div>
        </div>
      </template>

      <el-table :data="items" size="small" style="width:100%">
        <el-table-column prop="title" label="标题" min-width="200"/>
        <el-table-column prop="summary" label="摘要" min-width="260"/>
        <el-table-column prop="categoryId" label="分类ID" width="140"/>
        <el-table-column prop="isTop" label="置顶" width="90">
          <template #default="{row}">
            <el-tag :type="row.isTop===1?'warning':'info'">{{ row.isTop===1?'置顶':'否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'info'">{{ row.status===1?'发布':'草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="$router.push({ path: '/admin/article-detail', query: { id: row.id } })">查看</el-button>
            <el-button size="small" @click="goEdit(row)">编辑</el-button>
            <el-button size="small" @click="toggleTopRow(row)">{{ row.isTop===1?'取消置顶':'置顶' }}</el-button>
            <el-popconfirm title="确认删除该文章？" @confirm="remove(row)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div style="display:flex;justify-content:flex-end;margin-top:8px;">
        <el-pagination background layout="prev, pager, next" :current-page="query.current" :page-size="query.size" :total="total" @current-change="load"/>
      </div>
    </el-card>

    <el-dialog v-model="editVisible" :title="edit.id?'编辑文章':'创建文章'" width="720px">
      <el-form :model="edit" label-width="96px">
        <el-form-item label="标题"><el-input v-model="edit.title"/></el-form-item>
        <el-form-item label="摘要"><el-input v-model="edit.summary"/></el-form-item>
        <el-form-item label="内容"><el-input v-model="edit.content" type="textarea" :rows="6"/></el-form-item>
        <el-form-item label="封面">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="edit.coverImageId" placeholder="封面图片ID" style="width:260px"/>
            <el-upload :http-request="uploadCover" :show-file-list="false" accept="image/*"><el-button>上传封面</el-button></el-upload>
          </div>
        </el-form-item>
        <el-form-item label="分类ID"><el-input v-model="edit.categoryId"/></el-form-item>
        <el-form-item label="标签(逗号分隔)"><el-input v-model="edit.tags"/></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="edit.status">
            <el-option :value="1" label="发布"/>
            <el-option :value="0" label="草稿"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { pageArticles, createArticle, updateArticle, deleteArticle, toggleTop } from '../../api/adminArticle'
import http from '../../utils/http'
import { ElMessage } from 'element-plus'

export default {
  name: 'ArticleManage',
  data() {
    return {
      loading: false,
      saving: false,
      query: { current: 1, size: 10, categoryId: '', keyword: '' },
      items: [],
      total: 0,
      editVisible: false,
      edit: { id: '', title: '', summary: '', content: '', coverImageId: '', categoryId: '', tags: '', status: 1 }
    }
  },
  methods: {
    async load(page) {
      try {
        if (typeof page === 'number') this.query.current = page
        this.loading = true
        const res = await pageArticles(this.query)
        const d = res.data || {}
        this.items = d.records || []
        this.total = d.total || 0
      } catch (e) { ElMessage.error(e.message || '加载失败') }
      finally { this.loading = false }
    },
    goCreate() { this.$router.push('/admin/article-editor') },
    goEdit(row) { this.$router.push({ path: '/admin/article-editor', query: { id: row.id } }) },
    async uploadCover({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'article-covers')
        form.append('usageType', 3)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.edit.coverImageId = data.imageId
        ElMessage.success('封面已上传')
        onSuccess && onSuccess(resp)
      } catch (e) { ElMessage.error(e.message || '上传失败'); onError && onError(e) }
    },
    async saveEdit() {
      try {
        this.saving = true
        const payload = { title: this.edit.title, summary: this.edit.summary, content: this.edit.content, coverImageId: this.edit.coverImageId, categoryId: this.edit.categoryId, tags: this.edit.tags, status: this.edit.status, isTop: this.edit.isTop }
        if (!this.edit.id) await createArticle(payload)
        else await updateArticle(this.edit.id, payload)
        ElMessage.success('已保存')
        this.editVisible = false
        this.load(this.query.current)
      } catch (e) { ElMessage.error(e.message || '保存失败') }
      finally { this.saving = false }
    },
    async remove(row) {
      try { await deleteArticle(row.id); ElMessage.success('已删除'); this.load(this.query.current) }
      catch (e) { ElMessage.error(e.message || '删除失败') }
    },
    async toggleTopRow(row) {
      try { await toggleTop(row.id); ElMessage.success('操作成功'); this.load(this.query.current) }
      catch (e) { ElMessage.error(e.message || '操作失败') }
    }
  },
  mounted() { this.load(1) }
}
</script>


