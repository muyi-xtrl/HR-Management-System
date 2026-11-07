<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div>
          <h2>👋 欢迎回来，<span style="color: #409EFF;">{{ userStore.user?.username || '管理员' }}</span></h2>
          <p style="color: rgba(255,255,255,0.8);margin-top:8px;">今天是 {{ currentDate }}，祝您工作顺利！</p>
        </div>
        <div style="text-align:right;">
          <el-tag type="warning" size="large" style="font-size:16px;padding:8px 20px;">
            🕐 {{ currentTime }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card card-blue" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalEmployees }}</div>
              <div class="stat-label">👨‍💼 员工总数</div>
            </div>
          </div>
          <div class="stat-trend">较上月 <span style="color:#67C23A;">↑ 12%</span></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card card-green" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon><OfficeBuilding /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalDepartments }}</div>
              <div class="stat-label">🏢 部门总数</div>
            </div>
          </div>
          <div class="stat-trend">较上月 <span style="color:#67C23A;">↑ 2%</span></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card card-orange" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.todayAttendance }}%</div>
              <div class="stat-label">📊 今日出勤率</div>
            </div>
          </div>
          <div class="stat-trend">较昨日 <span style="color:#67C23A;">↑ 3%</span></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card card-red" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingLeaves }}</div>
              <div class="stat-label">📋 待审批请假</div>
            </div>
          </div>
          <div class="stat-trend">待处理 <span style="color:#F56C6C;">⚠️ 请及时处理</span></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 + 公告 -->
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;">
              <span style="font-weight:600;font-size:16px;">📈 部门人数分布</span>
              <el-tag type="info" size="small">实时数据</el-tag>
            </div>
          </template>
          <div ref="chart1" style="height:300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span style="font-weight:600;font-size:16px;">📌 快捷操作</span>
          </template>
          <div style="display:flex;flex-direction:column;gap:12px;padding:10px 0;">
            <el-button type="primary" size="large" style="width:100%;" @click="$router.push('/employee/add')">
              ➕ 新增员工
            </el-button>
            <el-button type="success" size="large" style="width:100%;" @click="$router.push('/attendance')">
              ✅ 打卡
            </el-button>
            <el-button type="warning" size="large" style="width:100%;" @click="$router.push('/leave/apply')">
              📋 申请请假
            </el-button>
            <el-button type="info" size="large" style="width:100%;" @click="$router.push('/employee')">
              👨‍💼 员工列表
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部 -->
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;">
            <div>
              <span style="font-size:14px;color:#909399;">💡 提示：</span>
              <span style="color:#666;">欢迎使用企业员工人事管理系统，请从左侧菜单选择功能模块</span>
            </div>
            <el-tag type="success">系统运行正常 ✅</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { getEmployeeList } from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const userStore = useUserStore()
const chart1 = ref(null)
let myChart = null

const stats = reactive({
  totalEmployees: 0,
  totalDepartments: 0,
  todayAttendance: 95,
  pendingLeaves: 0
})

const currentTime = ref('')
const currentDate = ref('')

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { 
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' 
  })
}

// 加载统计数据
const loadStats = async () => {
  try {
    const empRes = await getEmployeeList({ pageNum: 1, pageSize: 1 })
    if (empRes.code === 200) {
      stats.totalEmployees = empRes.data.total || 0
    }

    const deptRes = await getDepartmentList({ pageNum: 1, pageSize: 100 })
    if (deptRes.code === 200) {
      stats.totalDepartments = deptRes.data.records?.length || 0
    }

    const leaveRes = await request({ url: '/api/leave/pending', method: 'get' })
    if (leaveRes.code === 200) {
      stats.pendingLeaves = leaveRes.data.total || 0
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 初始化图表
const initChart = () => {
  if (!chart1.value) return
  myChart = echarts.init(chart1.value)
  myChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['技术部', '人事部', '市场部', '财务部', '运营部'],
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#666' }
    },
    series: [{
      type: 'bar',
      data: [45, 12, 18, 8, 15],
      barWidth: '40%',
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' }
          ]
        },
        borderRadius: [6, 6, 0, 0]
      },
      label: {
        show: true,
        position: 'top',
        color: '#666'
      }
    }]
  })
  window.addEventListener('resize', () => myChart?.resize())
}

onMounted(() => {
  updateTime()
  loadStats()
  setTimeout(initChart, 300)
  setInterval(updateTime, 1000)
})

onUnmounted(() => {
  window.removeEventListener('resize', () => myChart?.resize())
  myChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px 40px;
  margin-bottom: 24px;
  color: #fff;
}
.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-content h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}
.welcome-content p {
  margin: 0;
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 0;
}
.stat-card {
  border-radius: 12px;
  border: none;
  transition: transform 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-6px);
}
.stat-card :deep(.el-card__body) {
  padding: 20px 24px;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
}
.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1a2332;
  line-height: 1.2;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.stat-trend {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

/* 卡片颜色 */
.card-blue .stat-icon { background: linear-gradient(135deg, #409EFF, #79bbff); }
.card-green .stat-icon { background: linear-gradient(135deg, #67C23A, #95d475); }
.card-orange .stat-icon { background: linear-gradient(135deg, #E6A23C, #f3d19e); }
.card-red .stat-icon { background: linear-gradient(135deg, #F56C6C, #f89898); }

.card-blue { border-left: 4px solid #409EFF; }
.card-green { border-left: 4px solid #67C23A; }
.card-orange { border-left: 4px solid #E6A23C; }
.card-red { border-left: 4px solid #F56C6C; }

/* 图表卡片 */
.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 20px;
}
.chart-card :deep(.el-card__body) {
  padding: 16px 20px;
}
</style>