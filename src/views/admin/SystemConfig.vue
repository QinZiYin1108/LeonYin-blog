<template>
  <div>
    <el-card :loading="loading">
      <template #header>
        <div style="display:flex;align-items:center;justify-content:space-between;">
          <span>系统配置</span>
          <el-button type="primary" @click="reload">刷新</el-button>
        </div>
      </template>

      <el-form label-width="160px">
        <el-form-item label="是否自动分配默认头像">
          <el-switch v-model="autoAvatar" :active-value="'1'" :inactive-value="'0'" @change="saveAutoAvatar"/>
        </el-form-item>

        <el-form-item label="默认头像">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="defaultAvatarId" placeholder="默认头像图片ID" style="width:260px"/>
            <el-upload :http-request="uploadDefaultAvatar" :show-file-list="false" accept="image/*">
              <el-button>上传图片</el-button>
            </el-upload>
            <el-image v-if="defaultAvatarUrl" :src="defaultAvatarUrl" style="width:40px;height:40px;border-radius:4px;" fit="cover" />
            <el-button type="primary" @click="saveDefaultAvatar">保存</el-button>
          </div>
        </el-form-item>

        <el-form-item label="默认分类图标">
          <div style="display:flex;align-items:center;gap:8px;">
            <el-input v-model="defaultCategoryIconId" placeholder="默认分类图标图片ID" style="width:260px"/>
            <el-upload :http-request="uploadDefaultCategoryIcon" :show-file-list="false" accept="image/*">
              <el-button>上传图片</el-button>
            </el-upload>
            <el-image v-if="defaultCategoryIconUrl" :src="defaultCategoryIconUrl" style="width:40px;height:40px;border-radius:4px;" fit="cover" />
            <el-button type="primary" @click="saveDefaultCategoryIcon">保存</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { listConfigs, setAutoAvatar, setDefaultAvatar, setDefaultCategoryIcon } from '../../api/adminConfig'
import http from '../../utils/http'
import { ElMessage } from 'element-plus'

export default {
  name: 'SystemConfigPage',
  data() {
    return {
      loading: false,
      autoAvatar: '0',
      defaultAvatarId: '',
      defaultAvatarUrl: '',
      defaultCategoryIconId: '',
      defaultCategoryIconUrl: ''
    }
  },
  methods: {
    async reload() {
      try {
        this.loading = true
        const res = await listConfigs()
        const arr = res.data || []
        const map = {}
        arr.forEach(it => { map[it.configKey || it.key] = it.configValue || it.value })
        this.autoAvatar = map['auto_assign_avatar'] || '0'
        this.defaultAvatarId = map['default_avatar_image_id'] || ''
        this.defaultCategoryIconId = map['default_category_icon_image_id'] || ''
        // 可选：根据ID换取URL（当上传时会拿到URL，这里不强制拉取）
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
      } finally {
        this.loading = false
      }
    },
    async uploadDefaultAvatar({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'system')
        form.append('usageType', 9)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.defaultAvatarId = data.imageId
        this.defaultAvatarUrl = data.url || ''
        onSuccess && onSuccess(resp)
      } catch (e) { onError && onError(e); ElMessage.error(e.message || '上传失败') }
    },
    async uploadDefaultCategoryIcon({ file, onError, onSuccess }) {
      try {
        const form = new FormData()
        form.append('file', file, file.name)
        form.append('folder', 'system')
        form.append('usageType', 9)
        const resp = await http.post('/file/upload/image', form)
        const data = resp && resp.data ? resp.data : null
        if (!data || !data.imageId) throw new Error('上传响应异常')
        this.defaultCategoryIconId = data.imageId
        this.defaultCategoryIconUrl = data.url || ''
        onSuccess && onSuccess(resp)
      } catch (e) { onError && onError(e); ElMessage.error(e.message || '上传失败') }
    },
    async saveAutoAvatar() {
      try {
        await setAutoAvatar(this.autoAvatar)
        ElMessage.success('已保存')
      } catch (e) { ElMessage.error(e.message || '保存失败') }
    },
    async saveDefaultAvatar() {
      try { await setDefaultAvatar(this.defaultAvatarId); ElMessage.success('已保存') }
      catch (e) { ElMessage.error(e.message || '保存失败') }
    },
    async saveDefaultCategoryIcon() {
      try { await setDefaultCategoryIcon(this.defaultCategoryIconId); ElMessage.success('已保存') }
      catch (e) { ElMessage.error(e.message || '保存失败') }
    }
  },
  mounted() { this.reload() }
}
</script>






