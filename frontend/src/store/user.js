import { defineStore } from 'pinia'
import { getUser, setUser, removeToken, removeUser } from '@/utils/storage'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: getUser()  // ✅ 现在不会报错了
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    userRole: (state) => state.user?.role || 'EMPLOYEE'
  },
  actions: {
    setUser(user) {
      this.user = user
      setUser(user)
    },
    logout() {
      this.user = null
      removeToken()
      removeUser()
    }
  }
})