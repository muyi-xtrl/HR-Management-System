<template>
  <div class="department-page">
    <div class="page-header">
      <div>
        <h2>🏢 部门管理</h2>
        <p class="subtitle">管理公司所有部门信息</p>
      </div>
      <el-button type="primary" @click="showDialog=true" size="large">
        <el-icon><Plus /></el-icon> 新增部门
      </el-button>
    </div>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" border v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="部门名称">
          <template #default="scope">
            <span style="font-weight:600;">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">修改</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="showDialog" width="480px" destroy-on-close>
      <el-form :model="form" label-width="90px">
        <el-form-item label="部门名称"><el-input v-model="form.name" placeholder="请输入部门名称" /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="form.leader" placeholder="请输入负责人" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" placeholder="请输入电话" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartmentList, addDepartment, updateDepartment, deleteDepartment } from '@/api/department'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const showDialog = ref(false)
const isEdit = ref(false)

const form = reactive({ id: null, name: '', leader: '', phone: '', sort: 0, status: 1 })
const dialogTitle = computed(() => isEdit.value ? '✏️ 修改部门' : '➕ 新增部门')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) tableData.value = res.data.records || []
  } catch (e) { ElMessage.error('加载失败') } finally { loading.value = false }
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.name) return ElMessage.warning('请输入部门名称')
  submitting.value = true
  try {
    const res = isEdit.value ? await updateDepartment(form) : await addDepartment(form)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      showDialog.value = false
      loadData()
    }
  } catch (e) { ElMessage.error('操作失败') } finally { submitting.value = false }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该部门吗？', '提示').then(async () => {
    const res = await deleteDepartment(id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.department-page { padding: 0; }
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