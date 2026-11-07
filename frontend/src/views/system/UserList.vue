<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="showAddDialog = true" v-permission="['ADMIN']">
            <el-icon><Plus /></el-icon> 新增用户
          </el-button>
        </div>
      </template>

      <!-- 搜索条件 -->
      <div class="search-bar">
        <el-input
          v-model="searchParams.username"
          placeholder="用户名"
          style="width: 200px"
          clearable
        />
        <el-input
          v-model="searchParams.name"
          placeholder="姓名"
          style="width: 200px; margin-left: 10px"
          clearable
        />
        <el-select
          v-model="searchParams.role"
          placeholder="角色"
          style="width: 150px; margin-left: 10px"
          clearable
        >
          <el-option label="管理员" value="ADMIN" />
          <el-option label="HR" value="HR" />
          <el-option label="员工" value="EMPLOYEE" />
        </el-select>
        <el-button type="primary" @click="loadData" style="margin-left: 10px">
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.role)">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editUser(row)" v-permission="['ADMIN']">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="resetPasswordHandler(row)" v-permission="['ADMIN']">
              重置密码
            </el-button>
            <el-button type="danger" size="small" @click="deleteUserHandler(row)" v-permission="['ADMIN']">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="formData.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="HR" value="HR" />
            <el-option label="员工" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const userList = ref([])
const total = ref(0)
const showAddDialog = ref(false)
const formRef = ref()

const searchParams = ref({
  username: '',
  nickname: '',
  role: ''
})

const pageParams = ref({
  page: 1,
  size: 10
})

const formData = ref({
  id: null,
  username: '',
  nickname: '',
  email: '',
  role: 'EMPLOYEE',
  status: 1
})

const dialogTitle = computed(() => formData.value.id ? '编辑用户' : '新增用户')

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const getRoleLabel = (role) => {
  const map = { 'ADMIN': '管理员', 'HR': 'HR', 'EMPLOYEE': '员工' }
  return map[role] || role
}

const getRoleTag = (role) => {
  const map = { 'ADMIN': 'danger', 'HR': 'warning', 'EMPLOYEE': 'info' }
  return map[role] || 'info'
}

// ========== 加载数据 ==========
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pageParams.value.page,
      size: pageParams.value.size,
      username: searchParams.value.username,
      nickname: searchParams.value.nickname,
      role: searchParams.value.role
    }
    const res = await getUserList(params)
    userList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// ========== 重置搜索 ==========
const resetSearch = () => {
  searchParams.value = { username: '', nickname: '', role: '' }
  loadData()
}

// ========== 提交表单（新增/编辑） ==========
const submitForm = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (formData.value.id) {
      await updateUser(formData.value)
      ElMessage.success('修改成功')
    } else {
      await addUser(formData.value)
      ElMessage.success('新增成功')
    }
    showAddDialog.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// ========== 编辑用户 ==========
const editUser = (row) => {
  formData.value = { ...row }
  showAddDialog.value = true
}

// ========== 重置密码 ==========
const resetPasswordHandler = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认重置用户 "${row.username}" 的密码为 123456？`,
      '重置密码',
      { type: 'warning' }
    )
    await resetPassword(row.id)
    ElMessage.success('密码重置成功，新密码为：123456')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '重置失败')
    }
  }
}

// ========== 删除用户 ==========
const deleteUserHandler = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除用户 "${row.username}"？`,
      '删除确认',
      { type: 'warning' }
    )
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// ========== 重置表单 ==========
const resetForm = () => {
  formData.value = {
    id: null,
    username: '',
    nickname: '',
    email: '',
    role: 'EMPLOYEE',
    status: 1
  }
  formRef.value?.clearValidate()
}

// ========== 页面加载 ==========
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}
</style>