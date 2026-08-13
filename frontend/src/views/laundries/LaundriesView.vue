<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { completeLaundry, getActiveLaundries, startLaundry } from '../../api/laundry'
import type { LaundryRecord } from '../../types/esd'
import { useSessionStore } from '../../stores/session'

const session = useSessionStore()
const siteCode = computed(() => session.currentSite)
const loading = ref(false)
const rows = ref<LaundryRecord[]>([])

const assetTypeName = (type: number) => (type === 1 ? '靜電衣' : '靜電鞋')

/** 刷新待送洗與清洗中的列表。 */
async function load() {
  loading.value = true
  try {
    const response = await getActiveLaundries(siteCode.value)
    rows.value = response.data.data
  } catch {
    rows.value = []
    ElMessage.warning('清洗服務暫不可用，請確認後端已啟動')
  } finally {
    loading.value = false
  }
}

/** 送洗登記：待送洗 -> 清洗中。 */
async function start(record: LaundryRecord) {
  try {
    await startLaundry(record.id, siteCode.value)
    ElMessage.success('送洗登記成功')
    await load()
  } catch {
    ElMessage.error('送洗登記失敗，請確認資產狀態')
  }
}

/** 完成清洗：清洗中 -> 庫存，完成後不再顯示。 */
async function complete(record: LaundryRecord) {
  try {
    await ElMessageBox.confirm(`確認完成 ${record.assetCode} 的清洗嗎？`, '完成清洗', { type: 'warning' })
  } catch {
    return
  }
  try {
    await completeLaundry(record.id, siteCode.value)
    ElMessage.success('完成清洗，資產已入庫')
    await load()
  } catch {
    ElMessage.error('完成清洗失敗，請確認資產狀態')
  }
}

onMounted(() => { if (siteCode.value) load() })
watch(siteCode, (value, previous) => {
  if (value && value !== previous) load()
})
</script>

<template>
  <section class="page-heading page-heading-compact">
    <div>
      <div class="section-kicker">LAUNDRY / 清洗</div>
      <h1>清洗管理</h1>
      <p>承接回收送洗的資產：待送洗登記送洗，清洗中完成入庫。</p>
    </div>
    <el-button :icon="Refresh" @click="load">刷新</el-button>
  </section>

  <section class="data-panel">
    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="當前無待送洗或清洗中的資產">
      <el-table-column prop="assetCode" label="物品編碼" min-width="130" />
      <el-table-column label="類型" width="90">
        <template #default="{ row }">{{ assetTypeName(row.assetType) }}</template>
      </el-table-column>
      <el-table-column prop="colorCode" label="顏色" width="90" />
      <el-table-column prop="sizeCode" label="尺碼" width="90" />
      <el-table-column label="狀態" width="110">
        <template #default="{ row }">
          <el-tag :type="row.lifecycleStatus === 25 ? 'info' : 'warning'" effect="plain">{{ row.lifecycleStatusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sendTime" label="送洗日期" min-width="170" />
      <el-table-column prop="sendOperatorName" label="送洗人" width="110" />
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.lifecycleStatus === 25" type="primary" size="small" @click="start(row)">送洗登記</el-button>
          <el-button v-else type="success" size="small" @click="complete(row)">完成清洗</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
