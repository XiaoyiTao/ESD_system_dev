<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Search, Upload } from '@element-plus/icons-vue'
import { createIssue, getIssuePage } from '../../api/issue'
import { getPersonPage } from '../../api/person'
import { getAssetPage } from '../../api/asset'
import type { Asset, IssueRecord, PersonProfile } from '../../types/esd'
import { useSessionStore } from '../../stores/session'

const session = useSessionStore()
const siteCode = computed(() => session.currentSite)
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)
const total = ref(0)
const rows = ref<IssueRecord[]>([])
const query = reactive({ keyword: '', pageNo: 1, pageSize: 20 })
// 發放彈窗的候選：在職人員與庫存資產。
const persons = ref<PersonProfile[]>([])
const availableAssets = ref<Asset[]>([])
const form = reactive({ employeeUserId: undefined as number | undefined, assetCode: '', issueDate: new Date().toISOString().slice(0, 10) })

const assetTypeName = (type: number) => (type === 1 ? '靜電衣' : '靜電鞋')

/** 按當前廠區刷新發放記錄列表。 */
async function load() {
  loading.value = true
  try {
    const response = await getIssuePage({ ...query, siteCode: siteCode.value })
    rows.value = response.data.data.list
    total.value = response.data.data.total
  } catch {
    rows.value = []
    ElMessage.warning('發放服務暫不可用，請確認後端已啟動')
  } finally {
    loading.value = false
  }
}

/** 載入在職人員與庫存資產，供發放彈窗選擇。 */
async function loadOptions() {
  try {
    const [personResp, assetResp] = await Promise.all([
      getPersonPage({ siteCode: siteCode.value, pageNo: 1, pageSize: 200, esdStatus: 1 }),
      getAssetPage({ siteCode: siteCode.value, pageNo: 1, pageSize: 200, lifecycleStatus: 10 }),
    ])
    persons.value = personResp.data.data.list
    availableAssets.value = assetResp.data.data.list
  } catch {
    persons.value = []
    availableAssets.value = []
  }
}

/** 提交發放，發放人由服務端登入上下文取得。 */
async function submit() {
  const employeeUserId = form.employeeUserId
  if (!employeeUserId || !form.assetCode || !form.issueDate) {
    ElMessage.warning('請選擇員工、物品編碼和發放日期')
    return
  }
  formLoading.value = true
  try {
    await createIssue({ siteCode: siteCode.value, employeeUserId, assetCode: form.assetCode, issueDate: form.issueDate })
    ElMessage.success('發放成功')
    dialogVisible.value = false
    form.employeeUserId = undefined
    form.assetCode = ''
    await Promise.all([load(), loadOptions()])
  } catch {
    ElMessage.error('發放失敗，請檢查資產狀態與員工有效性')
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
      <div class="section-kicker">ISSUE / 發放</div>
      <h1>發放管理</h1>
      <p>將庫存靜電衣鞋發放給在職員工，一個資產的多次發放全部留痕。</p>
    </div>
    <el-button type="primary" :icon="Plus" @click="dialogVisible = true">單筆發放</el-button>
  </section>

  <section class="data-panel">
    <div class="toolbar">
      <el-tag class="site-context" effect="plain">廠區 {{ siteCode }}</el-tag>
      <el-input v-model="query.keyword" class="keyword-input" clearable placeholder="搜索工號、姓名或物品編碼" @keyup.enter="query.pageNo = 1; load()">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" :icon="Search" @click="query.pageNo = 1; load()">查詢</el-button>
      <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="當前廠區暫無發放記錄">
      <el-table-column prop="employeeNo" label="工號" min-width="120" />
      <el-table-column prop="employeeName" label="姓名" min-width="100" />
      <el-table-column prop="assetCode" label="物品編碼" min-width="130" />
      <el-table-column label="類型" width="90">
        <template #default="{ row }">{{ assetTypeName(row.assetType) }}</template>
      </el-table-column>
      <el-table-column prop="supervisorName" label="責任主管" min-width="110" />
      <el-table-column prop="deptName" label="課別" min-width="110" />
      <el-table-column prop="floorCode" label="樓層" width="90" />
      <el-table-column prop="issueDate" label="發放日期" width="120" />
      <el-table-column prop="issueOperatorName" label="發放人" width="110" />
    </el-table>
    <div class="table-footer">
      <span>共 {{ total }} 條</span>
      <el-pagination v-model:current-page="query.pageNo" v-model:page-size="query.pageSize" layout="prev, pager, next" :total="total" @current-change="load" />
    </div>
  </section>

  <el-dialog v-model="dialogVisible" title="發放登記" width="480px" destroy-on-close>
    <el-form label-position="top" @submit.prevent="submit">
      <el-form-item label="員工" required>
        <el-select v-model="form.employeeUserId" filterable placeholder="選擇在職員工" style="width: 100%">
          <el-option v-for="p in persons" :key="p.id" :label="`${p.employeeNo} - ${p.employeeName}`" :value="p.platformUserId" />
        </el-select>
      </el-form-item>
      <el-form-item label="物品編碼" required>
        <el-select v-model="form.assetCode" filterable placeholder="選擇庫存資產" style="width: 100%">
          <el-option v-for="a in availableAssets" :key="a.id" :label="`${a.assetCode} - ${assetTypeName(a.assetType)} ${a.colorCode} ${a.sizeCode}`" :value="a.assetCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="發放日期" required>
        <el-date-picker v-model="form.issueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
      </el-form-item>
      <div class="dialog-actions">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">確認發放</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
