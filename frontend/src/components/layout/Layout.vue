<template>
  <el-container style="height:100vh;">
    <el-aside width="220px" style="background:#304156;overflow-y:auto;">
      <div style="height:60px;line-height:60px;text-align:center;color:#fff;font-size:18px;font-weight:bold;border-bottom:1px solid #1f2d3d;">
        🏢 HRM 系统
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        style="border:none;"
      >
        <!-- ✅ 动态渲染菜单 -->
        <template v-for="menu in menus" :key="menu.id">
          <el-menu-item :index="menu.path">
            <el-icon><component :is="getIcon(menu.icon)" /></el-icon>
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #e6e6e6;display:flex;align-items:center;justify-content:space-between;padding:0 20px;">
        <span style="font-size:16px;font-weight:500;">企业员工人事管理系统</span>
        <div>
          <span style="margin-right:15px;">👤 {{ userStore.user?.username || '管理员' }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main style="background:#f0f2f5;padding:20px;overflow-y:auto;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { removeToken } from '@/utils/storage'
import { getMenus } from '@/api/menu'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const menus = ref([])

const getIcon = (icon) => {
  return ElementPlusIconsVue[icon] || 'Document'
}

const loadMenus = async () => {
  try {
    const res = await getMenus()
    if (res.code === 200) {
      menus.value = res.data || []
      console.log('菜单加载成功：', menus.value)
    }
  } catch (e) {
    console.error('加载菜单失败', e)
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定退出吗？', '提示').then(() => {
    userStore.logout()
    removeToken()
    ElMessage.success('已退出')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  loadMenus()
})
</script>