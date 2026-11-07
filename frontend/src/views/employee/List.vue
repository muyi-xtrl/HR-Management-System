<template>
  <div class="employee-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2>👨‍💼 员工管理</h2>
        <p class="subtitle">管理所有员工信息，支持搜索、新增、修改、删除和导出</p>
      </div>
      <div class="header-actions">
        
        <el-button type="primary" @click="$router.push('/employee/add')" size="large">
          <el-icon><Plus /></el-icon> 新增员工
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="mini-stat">
          <span class="stat-num">{{ total }}</span>
          <span class="stat-label">总员工数</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="mini-stat">
          <span class="stat-num">{{ activeCount }}</span>
          <span class="stat-label">在职</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="mini-stat">
          <span class="stat-num">{{ deptCount }}</span>
          <span class="stat-label">覆盖部门</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="mini-stat">
          <span class="stat-num">{{ genderRatio }}</span>
          <span class="stat-label">男女比例</span>
        </div>
      </el-col>
    </el-row>

    <!-- 工具栏 -->
    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar">
        <div class="left">
          <el-input
            v-model="keyword"
            placeholder="🔍 搜索姓名/工号..."
            style="width:240px;"
            clearable
            @keyup.enter="loadData"
          />
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>
        <div class="right">
          <span style="color:#999;font-size:13px;">共 {{ total }} 条记录</span>
        </div>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" border v-loading="loading" stripe style="width:100%;">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="employeeNo" label="工号" width="140" />
        <el-table-column prop="name" label="姓名" width="110">
          <template #default="scope">
            <span style="font-weight:600;">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'danger'" size="small">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '在职' ? 'success' : 'danger'" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="$router.push(`/employee/add?id=${scope.row.id}`)">
              <el-icon><Edit /></el-icon> 修改
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end;">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEmployeeList, deleteEmployee } from '@/api/employee'
import { getDepartmentList } from '@/api/department'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const depts = ref([])

const activeCount = computed(() => {
  return tableData.value.filter(e => e.status === '在职').length
})
const deptCount = computed(() => {
  const depts = new Set(tableData.value.map(e => e.departmentId))
  return depts.size
})
const genderRatio = computed(() => {
  const male = tableData.value.filter(e => e.gender === '男').length
  const female = tableData.value.filter(e => e.gender === '女').length
  if (male === 0 && female === 0) return '0:0'
  return `${male}:${female}`
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    const res = await getEmployeeList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  keyword.value = ''
  pageNum.value = 1
  loadData()
}

const handleExport = () => {
  window.open('/api/employee/export', '_blank')
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该员工吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteEmployee(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.employee-page { padding: 0; }
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h2 { margin: 0; font-size: 22px; }
.subtitle { color: #909399; font-size: 14px; margin: 4px 0 0; }
.header-actions { display: flex; gap: 10px; }

.stat-row { margin-bottom: 16px; }
.mini-stat {
  background: #fff;
  padding: 16px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
}
.mini-stat .stat-num { font-size: 24px; font-weight: 700; color: #1a2332; }
.mini-stat .stat-label { font-size: 13px; color: #909399; margin-top: 2px; }

.toolbar-card { margin-bottom: 16px; border: none; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.toolbar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; }
.toolbar .left { display: flex; gap: 10px; align-items: center; }
.table-card { border: none; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
</style>