<template>
  <div class="attendance-container">
    <!-- 管理员提示 -->
    <el-alert
      v-if="userRole === 'ADMIN'"
      title="管理员模式：您只能查看考勤记录，不能打卡"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <!-- 今日打卡卡片 - 仅非管理员显示 -->
    <el-card class="today-card" v-if="userRole !== 'ADMIN'">
      <template #header>
        <div class="card-header">
          <span>今日打卡</span>
          <span class="date">{{ today }}</span>
        </div>
      </template>
      <div class="clock-area">
        <div class="clock-info">
          <div class="clock-item">
            <span class="label">上班时间</span>
            <span class="value">{{ todayRecord?.checkInTime || '未打卡' }}</span>
          </div>
          <div class="clock-item">
            <span class="label">下班时间</span>
            <span class="value">{{ todayRecord?.checkOutTime || '未打卡' }}</span>
          </div>
          <div class="clock-item">
            <span class="label">状态</span>
            <el-tag :type="statusType">{{ statusText }}</el-tag>
          </div>
        </div>
        <div class="clock-buttons">
          <el-button type="primary" @click="clockInHandler" :disabled="!!todayRecord?.checkInTime">
            上班打卡
          </el-button>
          <el-button type="success" @click="clockOutHandler" :disabled="!todayRecord?.checkInTime || !!todayRecord?.checkOutTime">
            下班打卡
          </el-button>
        </div>
      </div>
    </el-card>

    

    <!-- 考勤历史 -->
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>考勤历史</span>
          <div>
            <el-select
              v-if="userRole === 'ADMIN'"
              v-model="selectedEmployeeId"
              placeholder="选择员工"
              clearable
              style="width: 150px; margin-right: 10px"
              @change="loadHistory"
            >
              <el-option
                v-for="emp in employeeList"
                :key="emp.id"
                :label="emp.name"
                :value="emp.id"
              />
            </el-select>
            <el-date-picker
              v-model="searchMonth"
              type="month"
              placeholder="选择月份"
              @change="loadHistory"
            />
          </div>
        </div>
      </template>
      <el-table :data="historyList" v-loading="loading" border stripe>
        <el-table-column prop="employeeName" label="员工姓名" width="120" />
        <el-table-column prop="date" label="日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="上班时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.checkInTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="下班时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.checkOutTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ row.status || '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workHours" label="工作时长" width="120">
          <template #default="{ row }">
            {{ calculateWorkHours(row.checkInTime, row.checkOutTime) }}
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadHistory"
        @size-change="loadHistory"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayRecord, clockIn, clockOut, getHistory, getStatistics, getAllAttendance } from '@/api/attendance'
import { getEmployeeList } from '@/api/employee'
import { useUserStore } from '@/store/user'
import dayjs from 'dayjs'

const userStore = useUserStore()

// ✅ 修复：从 userStore.user 获取角色
const userRole = computed(() => {
  return userStore.user?.role || 'EMPLOYEE'
})

const today = ref(dayjs().format('YYYY-MM-DD'))
const todayRecord = ref(null)
const historyList = ref([])
const loading = ref(false)
const total = ref(0)
const searchMonth = ref(dayjs().format('YYYY-MM'))
const selectedEmployeeId = ref(null)
const employeeList = ref([])
const statistics = ref({
  totalDays: 0,
  presentDays: 0,
  lateDays: 0,
  absentDays: 0
})

const pageParams = ref({
  page: 1,
  size: 10
})

const statusText = computed(() => {
  if (!todayRecord.value) return '未打卡'
  if (todayRecord.value.checkInTime && !todayRecord.value.checkOutTime) return '已上班'
  if (todayRecord.value.checkInTime && todayRecord.value.checkOutTime) return '已下班'
  return '未打卡'
})

const statusType = computed(() => {
  if (!todayRecord.value) return 'info'
  if (todayRecord.value.checkInTime && !todayRecord.value.checkOutTime) return 'warning'
  if (todayRecord.value.checkInTime && todayRecord.value.checkOutTime) return 'success'
  return 'info'
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('HH:mm:ss')
}

const calculateWorkHours = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return '-'
  const diff = dayjs(checkOut).diff(dayjs(checkIn), 'hour', true)
  if (diff < 0) return '-'
  const hours = Math.floor(diff)
  const minutes = Math.round((diff - hours) * 60)
  if (hours === 0) return `${minutes}分钟`
  return `${hours}小时${minutes}分`
}

const getStatusTag = (status) => {
  const map = {
    '正常': 'success',
    '迟到': 'warning',
    '早退': 'info',
    '异常': 'danger',
    '缺勤': 'info'
  }
  return map[status] || 'info'
}

const clockInHandler = async () => {
  try {
    await clockIn()
    ElMessage.success('打卡上班成功！')
    loadToday()
    loadHistory()
    loadStatistics()
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  }
}

const clockOutHandler = async () => {
  try {
    await clockOut()
    ElMessage.success('打卡下班成功！')
    loadToday()
    loadHistory()
    loadStatistics()
  } catch (error) {
    ElMessage.error(error.message || '打卡失败')
  }
}

const loadToday = async () => {
  // ✅ 修复：管理员不加载打卡记录
  if (userRole.value === 'ADMIN') return
  try {
    const res = await getTodayRecord()
    todayRecord.value = res.data
  } catch (error) {
    console.error('加载今日记录失败', error)
  }
}

const loadHistory = async () => {
  loading.value = true
  try {
    let params = {
      page: pageParams.value.page,
      size: pageParams.value.size,
      month: searchMonth.value
    }

    if (userRole.value === 'ADMIN') {
      if (selectedEmployeeId.value) {
        params.employeeId = selectedEmployeeId.value
      }
      const res = await getAllAttendance(params)
      historyList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      const res = await getHistory(params)
      historyList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载历史记录失败', error)
    ElMessage.error(error.message || '加载历史记录失败')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  // ✅ 修复：管理员不加载统计（或者加载所有员工的统计）
  try {
    const res = await getStatistics({
      month: searchMonth.value
    })
    statistics.value = res.data
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

const loadEmployees = async () => {
  if (userRole.value !== 'ADMIN') return
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    employeeList.value = res.data.records || []
  } catch (error) {
    console.error('加载员工列表失败', error)
  }
}

watch(searchMonth, () => {
  loadHistory()
  if (userRole.value !== 'ADMIN') {
    loadStatistics()
  }
})

onMounted(() => {
  if (userRole.value !== 'ADMIN') {
    loadToday()
    loadStatistics()
  }
  loadHistory()
  loadEmployees()
})
</script>

<style scoped>
.attendance-container {
  padding: 20px;
}
.today-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.date {
  color: #909399;
  font-size: 14px;
}
.clock-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}
.clock-info {
  display: flex;
  gap: 60px;
}
.clock-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.clock-item .label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}
.clock-item .value {
  font-size: 20px;
  font-weight: bold;
}
.clock-buttons {
  display: flex;
  gap: 16px;
}
.statistics {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
  padding: 10px 0;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}
.stat-label {
  margin-top: 8px;
  color: #909399;
}
.history-card {
  margin-top: 20px;
}
</style>