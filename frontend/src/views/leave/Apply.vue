<template>
  <div>
    <h2 style="margin-bottom:20px;">📋 申请请假</h2>
    <el-card style="max-width:600px;">
      <el-form :model="form" label-width="100px">
        <el-form-item label="请假类型">
          <el-select v-model="form.type" placeholder="请选择请假类型" style="width:100%;">
            <el-option label="事假" value="事假" />
            <el-option label="病假" value="病假" />
            <el-option label="年假" value="年假" />
            <el-option label="调休" value="调休" />
            <el-option label="婚假" value="婚假" />
            <el-option label="产假" value="产假" />
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width:100%;"
            :disabled-date="disabledStartDate"
          />
        </el-form-item>

        <el-form-item label="结束时间">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width:100%;"
            :disabled-date="disabledEndDate"
          />
        </el-form-item>

        <el-form-item label="请假原因">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入请假原因（如：家里有事、身体不适等）"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交申请
          </el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const submitting = ref(false)

const form = reactive({
  employeeId: 1,
  type: '',
  startTime: '',
  endTime: '',
  reason: ''
})

// 限制开始日期不能选择今天之前的日期
const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 限制结束日期不能早于开始日期
const disabledEndDate = (time) => {
  if (!form.startTime) return false
  return time.getTime() < new Date(form.startTime).getTime()
}

const handleSubmit = async () => {
  if (!form.type) {
    ElMessage.warning('请选择请假类型')
    return
  }
  if (!form.startTime) {
    ElMessage.warning('请选择开始时间')
    return
  }
  if (!form.endTime) {
    ElMessage.warning('请选择结束时间')
    return
  }
  if (!form.reason) {
    ElMessage.warning('请填写请假原因')
    return
  }

  // 检查结束时间是否大于开始时间
  if (new Date(form.startTime) >= new Date(form.endTime)) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }

  submitting.value = true
  try {
    const res = await request({
      url: '/api/leave/apply',
      method: 'post',
      data: form
    })
    if (res.code === 200) {
      ElMessage.success(res.data)
      router.push('/leave')
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>