<template>
  <div class="dashboard">
    <div class="cards">
      <el-card class="card" :body-style="{padding:'16px'}" :loading="loading">
        <div class="metric">
          <div class="metric-title">今日访问用户数</div>
          <div class="metric-value">{{ overview.todayVisitUserCount ?? '-' }}</div>
        </div>
      </el-card>
      <el-card class="card" :body-style="{padding:'16px'}" :loading="loading">
        <div class="metric">
          <div class="metric-title">今日访问IP数</div>
          <div class="metric-value">{{ overview.todayVisitIpCount ?? '-' }}</div>
        </div>
      </el-card>
    </div>

    <el-card class="mt16" :loading="loading">
      <template #header>
        <div>近7天访问趋势（非管理员）</div>
      </template>
      <div id="visitsChart" class="chart"></div>
    </el-card>

    <el-card class="mt16" :loading="loading">
      <template #header>
        <div>最新用户日志（非管理员）</div>
      </template>
      <el-table :data="latestLogs" size="small">
        <el-table-column prop="userId" label="用户ID" width="180"/>
        <el-table-column prop="action" label="操作"/>
        <el-table-column prop="ipAddress" label="IP" width="140"/>
        <el-table-column prop="createTime" label="时间" width="180">
          <template #default="{row}">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
  
</template>

<script>
import * as echarts from 'echarts'
import { getMetricsOverview, getVisitsTrend, getLatestUserLogs } from '../../api/adminLog'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      loading: false,
      overview: {},
      trend: { categories: [], series: [] },
      latestLogs: []
    }
  },
  methods: {
    async load() {
      try {
        this.loading = true
        const [o, t, l] = await Promise.all([
          getMetricsOverview(),
          getVisitsTrend({ days: 7 }),
          getLatestUserLogs({ limit: 10 })
        ])
        this.overview = (o && o.data) || {}
        this.trend = (t && t.data) || { categories: [], series: [] }
        this.latestLogs = (l && l.data) || []
        this.renderChart()
      } finally {
        this.loading = false
      }
    },
    renderChart() {
      const el = document.getElementById('visitsChart')
      if (!el) return
      const chart = echarts.init(el)
      chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.trend.categories },
        yAxis: { type: 'value' },
        series: [{ name: '访问用户数', type: 'line', data: this.trend.series, smooth: true }]
      })
      window.addEventListener('resize', () => chart.resize())
    },
    formatTime(ts) {
      if (!ts) return '-'
      const d = new Date(ts)
      return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`
    }
  },
  mounted() { this.load() }
}
</script>

<style scoped>
.dashboard { padding: 8px; }
.cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
.card { min-height: 92px; }
.metric { display:flex; flex-direction:column; gap:6px; }
.metric-title { color:#666; font-size:13px; }
.metric-value { font-size:24px; font-weight:600; }
.mt16 { margin-top: 16px; }
.chart { height: 300px; width: 100%; }
</style>






