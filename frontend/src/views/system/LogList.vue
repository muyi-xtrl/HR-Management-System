<template>
  <div>
    <h2 style="margin-bottom:20px;">📋 操作日志</h2>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="operation" label="操作" />
        <el-table-column prop="method" label="方法" />
        <el-table-column prop="params" label="参数" />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLogList } from '@/api/log'

const tableData = ref([])

const loadData = async () => {
  try {
    const res = await getLogList({ pageNum: 1, pageSize: 20 })
    if (res.code === 200) {
      tableData.value = res.data.records || []
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

onMounted(() => {
  loadData()
})
</script>