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

async function load() {
  loading.value = true
  try {
    const response = await getPersonPage({ ...query, siteCode: siteCode.value })
    rows.value = response.data.data.list
    total.value = response.data.data.total
  } catch {
    rows.value = []
    ElMessage.warning('人员服务暂不可用，请确认平台认证与 RPC 已启动')
  } finally {
    loading.value = false
  }
}

async function submit() {
  const platformUserId = form.platformUserId
  if (!platformUserId) {
    ElMessage.warning('请输入平台用户编号')
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
    ElMessage.success('人员档案已绑定')
    dialogVisible.value = false
    await load()
  } catch {
    ElMessage.error('绑定失败，请检查平台用户和厂区权限')
  } finally {
    formLoading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.pageNo = 1
  load()
}

onMounted(() => { if (siteCode.value) load() })
watch(siteCode, (value, previous) => {
  if (value && value !== previous) {
    query.pageNo = 1
    load()
  }
})
</script>

<template>
  <section class="page-heading page-heading-compact">
    <div>
      <div class="section-kicker">MASTER DATA / PEOPLE</div>
      <h1>人員檔案</h1>
      <p>从统一平台选择员工，并维护静电衣鞋业务所需的现场属性。</p>
    </div>
    <el-button type="primary" :icon="Plus" @click="dialogVisible = true">绑定人员</el-button>
  </section>

  <section class="data-panel">
    <div class="toolbar">
      <el-tag class="site-context" effect="plain">厂区 {{ siteCode }}</el-tag>
      <el-input v-model="query.keyword" class="keyword-input" clearable placeholder="搜索工号或姓名" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.esdStatus" class="status-select" aria-label="状态">
        <el-option :value="1" label="启用" />
        <el-option :value="0" label="停用" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="query.pageNo = 1; load()">查询</el-button>
      <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="当前厂区暂无人员档案">
      <el-table-column prop="employeeNo" label="工号" min-width="130" />
      <el-table-column prop="employeeName" label="姓名" min-width="100" />
      <el-table-column prop="deptName" label="部门" min-width="150" />
      <el-table-column prop="supervisorName" label="责任主管" min-width="120" />
      <el-table-column prop="floorCode" label="楼层" width="100" />
      <el-table-column prop="shiftCode" label="班别" width="100" />
      <el-table-column label="当前持有" min-width="150">
        <template #default="{ row }">
          <span class="hold-count">衣 {{ row.garmentCount }}</span>
          <span class="hold-count hold-count-shoes">鞋 {{ row.shoesCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.esdStatus === 1 ? 'success' : 'info'" effect="plain">{{ row.esdStatus === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="table-footer">
      <span>共 {{ total }} 条</span>
      <el-pagination v-model:current-page="query.pageNo" v-model:page-size="query.pageSize" layout="prev, pager, next" :total="total" @current-change="load" />
    </div>
  </section>

  <el-dialog v-model="dialogVisible" title="绑定平台人员" width="480px" destroy-on-close>
    <el-form label-position="top" @submit.prevent="submit">
      <el-form-item label="平台用户编号" required>
        <el-input v-model.number="form.platformUserId" type="number" placeholder="输入 platform userId，后续改为员工选择器" />
        <div class="form-help">账号、姓名、部门和厂区会由 platform-system-server 校验并同步快照。</div>
      </el-form-item>
      <el-form-item label="楼层"><el-input v-model="form.floorCode" placeholder="如 2F" /></el-form-item>
      <el-form-item label="班别"><el-input v-model="form.shiftCode" placeholder="如 A 班" /></el-form-item>
      <div class="dialog-actions">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确认绑定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
