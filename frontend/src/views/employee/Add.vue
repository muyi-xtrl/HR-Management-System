<template>
  <div>
    <h2 style="margin-bottom:20px;">{{ isEdit ? '✏️ 修改员工' : '➕ 新增员工' }}</h2>
    <el-card style="max-width:600px;">
      <el-form :model="form" label-width="100px">
        <el-form-item label="工号">
          <el-input v-model="form.employeeNo" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="form.departmentId" placeholder="请选择部门" style="width:100%;">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="form.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="入职日期">
          <el-input v-model="form.entryDate" placeholder="如：2024-01-15" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="在职">在职</el-radio>
            <el-radio label="离职">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addEmployee, updateEmployee, getEmployeeList } from '@/api/employee'
import { getDepartmentList } from '@/api/department'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const departments = ref([])

const form = reactive({
  id: null,
  employeeNo: '',
  name: '',
  gender: '男',
  departmentId: '',
  position: '',
  entryDate: '',
  phone: '',
  status: '在职'
})

// 加载部门列表
const loadDepartments = async () => {
  try {
    const res = await getDepartmentList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      departments.value = res.data.records || []
    }
  } catch (e) {
    ElMessage.error('加载部门失败')
  }
}

// 加载员工详情（修改时）
const loadEmployee = async (id) => {
  try {
    const res = await getEmployeeList({ pageNum: 1, pageSize: 100 })
    if (res.code === 200) {
      const emp = res.data.records.find(e => e.id === parseInt(id))
      if (emp) {
        Object.assign(form, emp)
      }
    }
  } catch (e) {
    ElMessage.error('加载员工信息失败')
  }
}

const handleSubmit = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await updateEmployee(form)
    } else {
      res = await addEmployee(form)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      router.push('/employee')
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(async () => {
  await loadDepartments()
  if (route.query.id) {
    isEdit.value = true
    await loadEmployee(route.query.id)
  }
})
</script>