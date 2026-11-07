<template>
  <div class="leave-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的请假</span>
          <el-button type="primary" @click="$router.push('/leave/apply')">
            <el-icon><Plus /></el-icon> 申请请假
          </el-button>
        </div>
      </template>

      <!-- 只显示当前用户的请假记录 -->
      <el-table :data="leaveList" v-loading="loading">
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approver" label="审批人" width="100" />
        <el-table-column prop="createTime" label="申请时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.size"
        :total="total"
        @current-change="loadData"
      />
    </el-card>

    <!-- 审批列表 - 仅HR和管理员可见 -->
    <el-card style="margin-top: 20px" v-if="['ADMIN', 'HR'].includes(role)">
      <template #header>
        <span>待审批请假</span>
      </template>
      <el-table :data="approveList" v-loading="approveLoading">
        <el-table-column prop="employeeName" label="申请人" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="approve(row, '通过')">
              通过
            </el-button>
            <el-button type="danger" size="small" @click="approve(row, '驳回')">
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyLeaveList, getApproveList, approveLeave } from '@/api/leave'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const role = computed(() => userStore.userInfo?.role || 'EMPLOYEE')

const loading = ref(false)
const approveLoading = ref(false)
const leaveList = ref([])
const approveList = ref([])
const total = ref(0)

const pageParams = ref({
  page: 1,
  size: 10
})

// 加载我的请假
const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyLeaveList(pageParams.value)
    leaveList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 加载待审批列表（仅HR/管理员）
const loadApproveList = async () => {
  if (!['ADMIN', 'HR'].includes(role.value)) return
  approveLoading.value = true
  try {
    const res = await getApproveList()
    approveList.value = res.data || []
  } catch (error) {
    console.error('加载审批列表失败', error)
  } finally {
    approveLoading.value = false
  }
}

// 审批
const approveHandler = async (row, status) => {
  try {
    await approveLeave({
      id: row.id,
      status: status
    })
    ElMessage.success(`已${status}请假申请`)
    loadApproveList()
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '审批失败')
  }
}

const getStatusTag = (status) => {
  const map = { '待审批': 'warning', '通过': 'success', '驳回': 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
  loadApproveList()
})
</script>

<style scoped>
.leave-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>