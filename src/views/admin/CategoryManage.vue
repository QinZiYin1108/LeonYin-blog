<template>
  <div>
    <el-card :loading="loading">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
          <span>分类管理</span>
          <div>
            <el-button type="primary" @click="openCreate">新增分类</el-button>
          </div>
        </div>
      </template>

      <el-table :data="items" size="small" style="width:100%">
        <el-table-column label="图标" width="80">
          <template #default="{row}">
            <el-image v-if="row.iconUrl" :src="row.iconUrl" style="width:40px;height:40px;border-radius:4px;" fit="cover" />
            <span v-else style="color:#bbb;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="160"/>
        <el-table-column prop="description" label="描述" min-width="260"/>
        <el-table-column prop="sortOrder" label="排序" width="100"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'info'">{{ row.status===1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用" width="100">
          <template #default="{row}">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" @change="(v)=>toggle(row, v)"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该分类？" @confirm="remove(row)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑分类" width="520px">
      <el-form :model="edit" label-width="96px">
        <el-form-item label="名称"><el-input v-model="edit.name"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="edit.description"/></el-form-item>
        <el-form-item label="排序"><el-input v-model.number="edit.sortOrder"/></el-form-item>
        <el-form-item label="图标">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="edit.iconImageId" placeholder="图标图片ID" style="width:240px"/>
            <el-upload
              :http-request="uploadEditIcon"
              :show-file-list="false"
              accept="image/*">
              <el-button>上传图标</el-button>
            </el-upload>
            <el-image v-if="editIconUrl" :src="editIconUrl" style="width:40px;height:40px;border-radius:4px;" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="edit.status">
            <el-option :value="1" label="启用"/>
            <el-option :value="0" label="禁用"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible=false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="createVisible" title="新增分类" width="520px">
      <el-form :model="create" label-width="96px">
        <el-form-item label="名称"><el-input v-model="create.name"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="create.description"/></el-form-item>
        <el-form-item label="排序"><el-input v-model.number="create.sortOrder"/></el-form-item>
        <el-form-item label="图标">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="create.iconImageId" placeholder="图标图片ID" style="width:240px"/>
            <el-upload
              :http-request="uploadCreateIcon"
              :show-file-list="false"
              accept="image/*">
              <el-button>上传图标</el-button>
            </el-upload>
            <el-image v-if="createIconUrl" :src="createIconUrl" style="width:40px;height:40px;border-radius:4px;" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="create.status">
            <el-option :value="1" label="启用"/>
            <el-option :value="0" label="禁用"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible=false">取消</el-button>
        <el-button type="primary" @click="saveCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listCategories, createCategory, updateCategory, deleteCategory, toggleCategory } from '../../api/adminCategory'
import { ElMessage } from 'element-plus'
import http from '../../utils/http'

export default {
  name: 'CategoryManage',
  data() {
    return {
      loading: false,
      items: [],
      createVisible: false,
      create: { name: '', description: '', sortOrder: 0, iconImageId: '', status: 1 },
      editVisible: false,
      edit: { id: '', name: '', description: '', sortOrder: 0, iconImageId: '', status: 1 },
      createIconUrl: '',
      editIconUrl: ''
    }
  },
  methods: {
    async load() {
      try {
        this.loading = true
        const res = await listCategories()
        this.items = res.data || []
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
      } finally {
        this.loading = false
      }
    },
    openCreate() { this.createVisible = true },
    async saveCreate() {
      if (!this.create.name) { ElMessage.error('请填写分类名称'); return }
      try {
        await createCategory(this.create)
        ElMessage.success('已创建')
        this.createVisible = false
        this.create = { name: '', description: '', sortOrder: 0, iconImageId: '', status: 1 }
        this.load()
      } catch (e) { ElMessage.error(e.message || '创建失败') }
    },
    async uploadCreateIcon({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'category-icons')
        form.append('usageType', 2)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.create.iconImageId = data.imageId
        ElMessage.success('图标已上传')
        onSuccess && onSuccess(resp)
      } catch (e) { ElMessage.error(e.message || '上传失败'); onError && onError(e) }
    },
    openEdit(row) {
      this.edit = { id: row.id, name: row.name, description: row.description, sortOrder: row.sortOrder, iconImageId: row.iconImageId, status: row.status }
      this.editIconUrl = row.iconUrl || ''
      this.editVisible = true
    },
    async saveEdit() {
      try {
        await updateCategory(this.edit.id, { name: this.edit.name, description: this.edit.description, sortOrder: this.edit.sortOrder, iconImageId: this.edit.iconImageId, status: this.edit.status })
        ElMessage.success('已保存')
        this.editVisible = false
        this.load()
      } catch (e) { ElMessage.error(e.message || '保存失败') }
    },
    async uploadEditIcon({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'category-icons')
        form.append('usageType', 2)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.edit.iconImageId = data.imageId
        ElMessage.success('图标已上传')
        onSuccess && onSuccess(resp)
      } catch (e) { ElMessage.error(e.message || '上传失败'); onError && onError(e) }
    },
    async toggle(row, val) {
      try {
        await toggleCategory(row.id)
        row.status = val
        ElMessage.success('状态已更新')
      } catch (e) { ElMessage.error(e.message || '更新失败') }
    },
    async remove(row) {
      try {
        await deleteCategory(row.id)
        ElMessage.success('已删除')
        this.load()
      } catch (e) { ElMessage.error(e.message || '删除失败') }
    }
  },
  mounted() { this.load() }
}
</script>


