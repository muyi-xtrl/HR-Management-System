<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧品牌区 -->
      <div class="login-left">
        <div class="brand-info">
          <div class="brand-icon">🏢</div>
          <div class="brand-name">企业人事管理系统</div>
          <div class="brand-sub">Enterprise HR Management System</div>
        </div>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>数据可视化</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>员工管理</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>考勤管理</span>
          </div>
        </div>
        <div class="brand-footer">
          <span></span>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-box">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录您的账户，开启高效办公之旅</p>

          <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
                class="login-input"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                class="login-input"
                show-password
              />
            </el-form-item>

            <el-form-item prop="captcha">
              <div class="captcha-row">
                <el-input
                  v-model="form.captcha"
                  placeholder="请输入验证码"
                  size="large"
                  class="login-input"
                  style="flex:1;"
                />
                <img
                  :src="captchaImage"
                  @click="refreshCaptcha"
                  class="captcha-img"
                  alt="验证码"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="handleLogin"
                :loading="loading"
                size="large"
                class="login-btn"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-footer">
            <span>没有账号？</span>
            <a href="#" class="register-link">立即注册</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { setToken, setUser } from '@/utils/storage'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref()
const captchaKey = ref('')
const captchaImage = ref('')

const form = reactive({
  username: '',
  password: '',
  captcha: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// ✅ 获取验证码（调用后端 JSON 接口）
const refreshCaptcha = async () => {
  try {
    const res = await request.get('/auth/captcha')
    if (res.code === 200) {
      captchaKey.value = res.data.key
      captchaImage.value = res.data.image
      console.log('验证码 Key:', captchaKey.value)
    }
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

// ✅ 登录
const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  loading.value = true
  try {
    const res = await login({
      username: form.username,
      password: form.password,
      captcha: form.captcha,
      captchaKey: captchaKey.value
    })
    
    if (res.code === 200) {
      setToken(res.data.token)
      const userInfo = res.data.user
      setUser(userInfo)
      userStore.setUser(userInfo)
      
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || res.msg || '登录失败')
      // 登录失败刷新验证码
      refreshCaptcha()
      form.captcha = ''
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查网络')
    refreshCaptcha()
    form.captcha = ''
  } finally {
    loading.value = false
  }
}

// ✅ 页面加载时获取验证码
onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=1920&q=80') no-repeat center center;
  background-size: cover;
  padding: 20px;
}

.login-wrapper {
  display: flex;
  width: 1000px;
  max-width: 100%;
  min-height: 580px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.login-left {
  flex: 0 0 380px;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.05);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
}

.brand-icon {
  font-size: 52px;
  margin-bottom: 16px;
}
.brand-name {
  font-size: 26px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 1px;
}
.brand-sub {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 6px;
  letter-spacing: 0.5px;
}

.brand-features {
  margin: 30px 0;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
}
.feature-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #4a6cf7;
  flex-shrink: 0;
}

.brand-footer {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 48px;
}

.login-box {
  width: 100%;
  max-width: 400px;
}

.login-title {
  font-size: 30px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 40px;
}

.login-form .el-form-item {
  margin-bottom: 22px;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 18px;
  height: 50px;
  box-shadow: none;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  transition: border-color 0.3s, background 0.3s;
}
.login-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.12);
}
.login-input :deep(.el-input__wrapper.is-focus) {
  border-color: #4a6cf7;
  background: rgba(255, 255, 255, 0.12);
  box-shadow: 0 0 0 4px rgba(74, 108, 247, 0.15);
}
.login-input :deep(.el-input__inner) {
  color: #ffffff;
}
.login-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}
.login-input :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.4);
}

.captcha-row {
  display: flex;
  gap: 14px;
  align-items: center;
}
.captcha-img {
  height: 50px;
  border-radius: 10px;
  cursor: pointer;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #4a6cf7 0%, #6a3de8 100%);
  border: none;
  color: #ffffff;
  transition: transform 0.2s, box-shadow 0.3s;
  margin-top: 6px;
}
.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(74, 108, 247, 0.35);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}
.register-link {
  color: #4a6cf7;
  font-weight: 600;
  text-decoration: none;
}
.register-link:hover {
  text-decoration: underline;
}

@media (max-width: 820px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 440px;
    min-height: auto;
  }
  .login-left {
    flex: none;
    padding: 32px 28px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  }
  .login-right {
    padding: 32px 28px;
  }
  .brand-features {
    margin: 20px 0;
  }
  .brand-footer {
    display: none;
  }
}

@media (max-width: 480px) {
  .login-box {
    max-width: 100%;
  }
  .login-title {
    font-size: 24px;
  }
  .brand-name {
    font-size: 20px;
  }
}
</style>