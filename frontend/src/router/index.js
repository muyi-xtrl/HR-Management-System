import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/storage'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/components/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',        // ✅ 加 /
        component: () => import('@/views/dashboard/Dashboard.vue')
      },
      {
        path: '/employee',         // ✅ 加 /
        component: () => import('@/views/employee/List.vue')
      },
      {
        path: '/employee/add',     // ✅ 加 /
        component: () => import('@/views/employee/Add.vue')
      },
      {
        path: '/department',       // ✅ 加 /
        component: () => import('@/views/department/List.vue')
      },
      {
        path: '/attendance',       // ✅ 加 /
        component: () => import('@/views/attendance/Index.vue')
      },
      {
        path: '/leave',            // ✅ 加 /
        component: () => import('@/views/leave/MyList.vue')
      },
      {
        path: '/leave/apply',      // ✅ 加 /
        component: () => import('@/views/leave/Apply.vue')
      },
      {
        path: '/salary',           // ✅ 加 /
        component: () => import('@/views/salary/MySalary.vue')
      },
      {
        path: '/profile',          // ✅ 加 /，删除重复
        component: () => import('@/views/profile/Profile.vue')
      },
      {
        path: '/users',            // ✅ 加 /
        component: () => import('@/views/system/UserList.vue')
      },
      {
        path: '/logs',             // ✅ 加 /
        component: () => import('@/views/system/LogList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router