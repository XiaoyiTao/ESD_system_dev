<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createReturn, getReturnPage } from '../../api/return'
import { getAssetPage } from '../../api/asset'
import type { Asset, ReturnRecord } from '../../types/esd'
import { useSessionStore } from '../../stores/session'

const session = useSessionStore()
const siteCode = computed(() => session.currentSite)
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)
const total = ref(0)
const rows = ref<ReturnRecord[]>([])
const query = reactive({ keyword: '', pageNo: 1, pageSize: 20 })
// 回收彈窗的候選：發放中的資產。
const issuedAssets = ref<Asset[]>([])
const form = reactive({ assetCode: '', disposition: 1, returnerName: '', returnDate: new Date().toISOString().slice(0, 10) })

const assetTypeName = (type: number) => (type === 1 ? '靜電衣' : '靜電鞋')

/** 按當前廠區刷新回收記錄列表。 */
async function load() {
  loading.value = true
  try {
    const response = await getReturnPage({ ...query, siteCode: siteCode.value })
    rows.value = response.data.data.list
    total.value = response.data.data.total
  } catch {
    rows.value = []
    ElMessage.warning('回收服務暫不可用，請確認後端已啟動')
  } finally {
    loading.value = false
  }
}

/** 載入發放中的資產，供回收彈窗選擇。 */
async function loadOptions() {
  try {
    const response = await getAssetPage({ siteCode: siteCode.value, pageNo: 1, pageSize: 200, lifecycleStatus: 20 })
    issuedAssets.value = response.data.data.list
  } catch {
    issuedAssets.value = []
  }
}

/** 提交回收，直接入庫或送洗。 */
async function submit() {
  if (!form.assetCode || !form.returnDate) {
    ElMessage.warning('請選擇物品編碼和回收日期')
    return
  }
  formLoading.value = true
  try {
    await createReturn({ siteCode: siteCode.value, ...form })
    ElMessage.success('回收成功')
    dialogVisible.value = false
    form.assetCode = ''
    form.returnerName = ''
    await Promise.all([load(), loadOptions()])
  } catch {
    ElMessage.error('回收失敗，請檢查資產狀態')
  } finally {
    formLoading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.pageNo = 1
  load()
}

onMounted(() => { if (siteCode.value) { load(); loadOptions() } })
watch(siteCode, (value, previous) => {
  if (value && value !== previous) {
    query.pageNo = 1
    load()
    loadOptions()
  }
})
</script>

<template>
  <section class="page-heading page-heading-compact">
    <div>
      <div class="section-kicker">RETURN / 回收</div>
      <h1>回收管理</h1>
      <p>回收發放中的資產：直接入庫回到庫存，或送洗進入待送洗。</p>
    </div>
    <el-button type="primary" :icon="Plus" @click="dialogVisible = true">單筆回收</el-button>
  </section>

  <section class="data-panel">
    <div class="toolbar">
      <el-tag class="site-context" effect="plain">廠區 {{ siteCode }}</el-tag>
      <el-input v-model="query.keyword" class="keyword-input" clearable placeholder="搜索物品編碼" @keyup.enter="query.pageNo = 1; load()">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" :icon="Search" @click="query.pageNo = 1; load()">查詢</el-button>
      <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="當前廠區暫無回收記錄">
      <el-table-column prop="assetCode" label="物品編碼" min-width="130" />
      <el-table-column label="類型" width="90">
        <template #default="{ row }">{{ assetTypeName(row.assetType) }}</template>
      </el-table-column>
      <el-table-column prop="returnerName" label="歸還人" min-width="110" />
      <el-table-column prop="returnDate" label="回收日期" width="120" />
      <el-table-column prop="receiverName" label="回收人" width="110" />
      <el-table-column prop="dispositionName" label="後續操作" width="110" />
    </el-table>
    <div class="table-footer">
      <span>共 {{ total }} 條</span>
      <el-pagination v-model:current-page="query.pageNo" v-model:page-size="query.pageSize" layout="prev, pager, next" :total="total" @current-change="load" />
    </div>
  </section>

  <el-dialog v-model="dialogVisible" title="回收登記" width="480px" destroy-on-close>
    <el-form label-position="top" @submit.prevent="submit">
      <el-form-item label="物品編碼" required>
        <el-select v-model="form.assetCode" filterable placeholder="選擇發放中資產" style="width: 100%">
          <el-option v-for="a in issuedAssets" :key="a.id" :label="`${a.assetCode} - ${assetTypeName(a.assetType)} ${a.colorCode} ${a.sizeCode}`" :value="a.assetCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="後續操作" required>
        <el-radio-group v-model="form.disposition">
          <el-radio :value="1">直接入庫</el-radio>
          <el-radio :value="2">送洗</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="歸還人"><el-input v-model="form.returnerName" maxlength="100" placeholder="選填" /></el-form-item>
      <el-form-item label="回收日期" required>
        <el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
      <div class="dialog-actions">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">確認回收</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
