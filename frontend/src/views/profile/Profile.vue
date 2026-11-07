<template>
  <div>
    <h2 style="margin-bottom:20px;">👤 个人信息</h2>

    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-card style="max-width:500px;">
          <el-form :model="userForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userForm.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag>{{ userForm.role }}</el-tag>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateInfo" :loading="loading">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-card style="max-width:500px;">
          <el-form :model="pwdForm" label-width="100px">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="pwdLoading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('info')
const loading = ref(false)
const pwdLoading = ref(false)

const userForm = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  role: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      Object.assign(userForm, res.data)
    }
  } catch (e) {
    ElMessage.error('加载用户信息失败')
  }
}

// 更新基本信息
const updateInfo = async () => {
  loading.value = true
  try {
    const res = await updateUserInfo(userForm)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      userStore.setUser({ username: userForm.username })
    }
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    loading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  const { oldPassword, newPassword, confirmPassword } = pwdForm

  if (!oldPassword || !newPassword) {
    return ElMessage.warning('请填写完整')
  }

  if (newPassword.length < 6) {
    return ElMessage.warning('新密码长度至少6位')
  }

  if (newPassword !== confirmPassword) {
    return ElMessage.warning('两次输入的密码不一致')
  }

  pwdLoading.value = true
  try {
    const res = await updatePassword({
      oldPassword,
      newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      setTimeout(() => {
        localStorage.clear()
        window.location.href = '/login'
      }, 1500)
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    pwdLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>