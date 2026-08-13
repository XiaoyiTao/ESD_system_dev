<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createPerson, getPersonPage } from '../../api/person'
import type { PersonProfile } from '../../types/esd'
import { useSessionStore } from '../../stores/session'

const session = useSessionStore()
const siteCode = computed(() => session.currentSite)
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)
const total = ref(0)
const rows = ref<PersonProfile[]>([])
const query = reactive({ keyword: '', pageNo: 1, pageSize: 20, esdStatus: 1 })
const form = reactive({ platformUserId: undefined as number | undefined, floorCode: '', shiftCode: '' })

/** 按當前廠區、關鍵字和啟停狀態刷新人員檔。 */
async function load() {
  loading.value = true
  try {
    const response = await getPersonPage({ ...query, siteCode: siteCode.value })
    rows.value = response.data.data.list
    total.value = response.data.data.total
  } catch {
    rows.value = []
    ElMessage.warning('人員服務暫不可用，請確認平台認證與 RPC 已啟動')
  } finally {
    loading.value = false
  }
}

/** 使用平台用戶 ID 創建綁定，員工主數據由後端 RPC 校驗。 */
async function submit() {
  const platformUserId = form.platformUserId
  if (!platformUserId) {
    ElMessage.warning('請輸入平台用戶編號')
    return
  }
  formLoading.value = true
  try {
    await createPerson({
      siteCode: siteCode.value,
      platformUserId,
      floorCode: form.floorCode,
      shiftCode: form.shiftCode,
    })
    ElMessage.success('人員檔案已綁定')
    dialogVisible.value = false
    await load()
  } catch {
    ElMessage.error('綁定失敗，請檢查平台用戶和廠區權限')
  } finally {
    formLoading.value = false
  }
}

/** 清除關鍵字並回到第一頁。 */
function resetQuery() {
  query.keyword = ''
  query.pageNo = 1
  load()
}

// 廠區由頂部會話選擇器控制，切換後重新從服務端加載隔離數據。
onMounted(() => { if (siteCode.value) load() })
watch(siteCode, (value, previous) => {
  if (value && value !== previous) {
    query.pageNo = 1
    load()
  }
})
</script>

<template>
  <!-- 人員擴展檔頁面：查詢、持有數量展示和平台用戶綁定。 -->
  <section class="page-heading page-heading-compact">
    <div>
      <div class="section-kicker">MASTER DATA / PEOPLE</div>
      <h1>人員檔案</h1>
      <p>從統一平台選擇員工，並維護靜電衣鞋業務所需的現場屬性。</p>
    </div>
    <el-button type="primary" :icon="Plus" @click="dialogVisible = true">綁定人員</el-button>
  </section>

  <section class="data-panel">
    <div class="toolbar">
      <el-tag class="site-context" effect="plain">廠區 {{ siteCode }}</el-tag>
      <el-input v-model="query.keyword" class="keyword-input" clearable placeholder="搜索工號或姓名" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.esdStatus" class="status-select" aria-label="狀態">
        <el-option :value="1" label="啟用" />
        <el-option :value="0" label="停用" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="query.pageNo = 1; load()">查詢</el-button>
      <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
    </div>

    <!-- 持有數量由後端根據已發放資產實時統計，不能使用前端緩存推算。 -->
    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="當前廠區暫無人員檔案">
      <el-table-column prop="employeeNo" label="工號" min-width="130" />
      <el-table-column prop="employeeName" label="姓名" min-width="100" />
      <el-table-column prop="deptName" label="部門" min-width="150" />
      <el-table-column prop="supervisorName" label="責任主管" min-width="120" />
      <el-table-column prop="floorCode" label="樓層" width="100" />
      <el-table-column prop="shiftCode" label="班別" width="100" />
      <el-table-column label="狀態" width="90">
        <template #default="{ row }">
          <el-tag :type="row.esdStatus === 1 ? 'success' : 'info'" effect="plain">{{ row.esdStatus === 1 ? '啟用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="table-footer">
      <span>共 {{ total }} 條</span>
      <el-pagination v-model:current-page="query.pageNo" v-model:page-size="query.pageSize" layout="prev, pager, next" :total="total" @current-change="load" />
    </div>
  </section>

  <!-- 當前先輸入平台 userId；後續接入平台員工選擇器時只替換這一塊表單。 -->
  <el-dialog v-model="dialogVisible" title="綁定平台人員" width="480px" destroy-on-close>
    <el-form label-position="top" @submit.prevent="submit">
      <el-form-item label="平台用戶編號" required>
        <el-input v-model.number="form.platformUserId" type="number" placeholder="輸入 platform userId，後續改為員工選擇器" />
        <div class="form-help">帳號、姓名、部門和廠區會由 platform-system-server 校驗並同步快照。</div>
      </el-form-item>
      <el-form-item label="樓層"><el-input v-model="form.floorCode" placeholder="如 2F" /></el-form-item>
      <el-form-item label="班別"><el-input v-model="form.shiftCode" placeholder="如 A 班" /></el-form-item>
      <div class="dialog-actions">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">確認綁定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
