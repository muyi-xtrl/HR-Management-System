<template>
  <div class="salary-page">
    <div class="page-header">
      <div>
        <h2>💰 薪资管理</h2>
        <p class="subtitle">查看我的薪资记录和发放状态</p>
      </div>
      <el-button type="success" @click="showGenerate=true" size="large">
        <el-icon><Plus /></el-icon> 生成薪资
      </el-button>
    </div>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" border stripe>
        <el-table-column prop="month" label="月份" width="120" />
        <el-table-column prop="baseSalary" label="基本工资" align="right" />
        <el-table-column prop="bonus" label="绩效奖金" align="right" />
        <el-table-column prop="allowance" label="津贴" align="right" />
        <el-table-column prop="socialSecurity" label="社保扣款" align="right" />
        <el-table-column prop="tax" label="个税" align="right" />
        <el-table-column prop="actualSalary" label="实发工资" align="right">
          <template #default="scope">
            <span style="color:#409EFF;font-weight:bold;font-size:16px;">
              ¥{{ scope.row.actualSalary }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已发放' ? 'success' : 'warning'" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status === '待发放'" type="primary" size="small" @click="handlePay(scope.row.id)">
              发放
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="📊 生成薪资" v-model="showGenerate" width="480px" destroy-on-close>
      <el-form :model="salaryForm" label-width="110px">
        <el-form-item label="员工ID"><el-input v-model="salaryForm.employeeId" /></el-form-item>
        <el-form-item label="月份"><el-input v-model="salaryForm.month" placeholder="2026-08" /></el-form-item>
        <el-form-item label="基本工资"><el-input v-model="salaryForm.baseSalary" /></el-form-item>
        <el-form-item label="绩效奖金"><el-input v-model="salaryForm.bonus" /></el-form-item>
        <el-form-item label="津贴"><el-input v-model="salaryForm.allowance" /></el-form-item>
        <el-form-item label="加班费"><el-input v-model="salaryForm.overtimePay" /></el-form-item>
        <el-form-item label="社保扣款"><el-input v-model="salaryForm.socialSecurity" /></el-form-item>
        <el-form-item label="个税"><el-input v-model="salaryForm.tax" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerate=false">取消</el-button>
        <el-button type="primary" @click="handleGenerate" :loading="generating">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const tableData = ref([])
const showGenerate = ref(false)
const generating = ref(false)
const salaryForm = reactive({
  employeeId: 1,
  month: '',
  baseSalary: 8000,
  bonus: 2000,
  allowance: 500,
  overtimePay: 300,
  socialSecurity: 1000,
  tax: 200
})

const loadData = async () => {
  const res = await request({ url: '/api/salary/list', method: 'get', params: { pageNum: 1, pageSize: 10 } })
  if (res.code === 200) tableData.value = res.data.records || []
}

const handleGenerate = async () => {
  if (!salaryForm.month) return ElMessage.warning('请填写月份')
  generating.value = true
  try {
    const res = await request({ url: '/api/salary/generate', method: 'post', data: salaryForm })
    if (res.code === 200) { ElMessage.success(res.data); showGenerate.value = false; loadData() }
  } catch (e) { ElMessage.error('生成失败') } finally { generating.value = false }
}

const handlePay = async (id) => {
  const res = await request({ url: `/api/salary/pay/${id}`, method: 'put' })
  if (res.code === 200) { ElMessage.success(res.data); loadData() }
}

onMounted(() => loadData())
</script>

<style scoped>
.salary-page { padding: 0; }
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h2 { margin: 0; font-size: 22px; }
.subtitle { color: #909399; font-size: 14px; margin: 4px 0 0; }
.table-card { border: none; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
</style>