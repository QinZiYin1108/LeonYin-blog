<template>
  <div class="log-manage">
    <el-page-header @back="$router.back()" content="操作日志" />

    <el-card class="mt16" :loading="loading">
      <div class="toolbar">
        <el-input v-model="query.userId" placeholder="按用户ID筛选" clearable style="width:220px" />
        <el-input v-model="query.operation" placeholder="按操作名称筛选" clearable style="width:220px" />
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="onReset">重置</el-button>
      </div>

      <el-table :data="tableData" stripe style="width:100%" size="small">
        <el-table-column prop="id" label="ID" width="140" />
        <el-table-column prop="userId" label="用户ID" width="180" />
        <el-table-column prop="action" label="操作" width="160" />
        <el-table-column prop="ipAddress" label="IP" width="140" />
        <el-table-column prop="details" label="详情">
          <template #default="scope">
            <span>{{ formatDetails(scope.row.details) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 50]"
          :page-size="query.pageSize"
          :current-page="query.pageNum"
          @size-change="(s)=>{query.pageSize=s;query.pageNum=1;load()}"
          @current-change="(p)=>{query.pageNum=p;load()}"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { pageUserLogs } from '../../api/adminLog'
import { ElMessage } from 'element-plus'

export default {
  name: 'LogManage',
  data() {
    return {
      loading: false,
      query: { pageNum: 1, pageSize: 10, userId: '', operation: '' },
      tableData: [],
      total: 0
    }
  },
  methods: {
    async load() {
      try {
        this.loading = true
        const res = await pageUserLogs({
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize,
          userId: this.query.userId || undefined,
          operation: this.query.operation || undefined
        })
        const pr = res && res.data ? res.data : { records: [], total: 0 }
        this.tableData = pr.records || []
        this.total = pr.total || 0
      } catch (e) {
        ElMessage.error(e.message || '加载失败')
      } finally {
        this.loading = false
      }
    },
    onReset() {
      this.query = { pageNum: 1, pageSize: 10, userId: '', operation: '' }
      this.load()
    },
    formatTime(ts) {
      if (!ts) return ''
      try { return new Date(Number(ts)).toLocaleString() } catch { return '' }
    },
    formatDetails(str) {
      if (!str) return ''
      try {
        const obj = JSON.parse(str)
        if (obj && typeof obj === 'object') return JSON.stringify(obj)
      } catch (e) { return str }
      return str
    }
  },
  mounted() { this.load() }
}
</script>

<style scoped>
.mt16 { margin-top: 16px; }
.toolbar { display:flex; align-items:center; gap:8px; margin-bottom: 12px; }
.pager { display:flex; justify-content:flex-end; margin-top: 12px; }
.log-manage { max-width: 1200px; margin: 16px auto; }
</style>


