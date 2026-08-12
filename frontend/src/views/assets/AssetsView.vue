<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Box, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createAsset, getAssetPage } from '../../api/asset'
import type { Asset } from '../../types/esd'
import { useSessionStore } from '../../stores/session'

const session = useSessionStore()
const siteCode = computed(() => session.currentSite)
const loading = ref(false)
const dialogVisible = ref(false)
const formLoading = ref(false)
const total = ref(0)
const rows = ref<Asset[]>([])
const query = reactive({ keyword: '', pageNo: 1, pageSize: 20, assetType: undefined as number | undefined, lifecycleStatus: undefined as number | undefined })
const form = reactive({ assetCode: '', assetType: 1, colorCode: '', sizeCode: '' })

const statusName: Record<number, string> = { 10: '可用库存', 20: '已发放', 30: '清洗中', 40: '已报废', 50: '已遗失' }
// Element Plus 标签类型只接受有限字符串，因此在映射中收窄类型，避免模板运行时计算颜色。
const statusType: Record<number, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = { 10: 'success', 20: 'primary', 30: 'warning', 40: 'info', 50: 'danger' }

/** 按当前厂区和筛选条件刷新资产列表。 */
async function load() {
  loading.value = true
  try {
    const response = await getAssetPage({ ...query, siteCode: siteCode.value })
    rows.value = response.data.data.list
    total.value = response.data.data.total
  } catch {
    rows.value = []
    ElMessage.warning('资产服务暂不可用，请确认 MySQL 与后端已启动')
  } finally {
    loading.value = false
  }
}

/** 校验最小表单条件后提交资产入库。 */
async function submit() {
  if (!form.assetCode || !form.colorCode || !form.sizeCode) {
    ElMessage.warning('请填写资产编码、颜色和尺码')
    return
  }
  formLoading.value = true
  try {
    await createAsset({ siteCode: siteCode.value, ...form })
    ElMessage.success('资产已入库')
    dialogVisible.value = false
    Object.assign(form, { assetCode: '', assetType: 1, colorCode: '', sizeCode: '' })
    await load()
  } catch {
    ElMessage.error('入库失败，请检查编码是否重复及厂区权限')
  } finally {
    formLoading.value = false
  }
}

/** 恢复默认筛选条件并重新查询。 */
function resetQuery() {
  Object.assign(query, { keyword: '', assetType: undefined, lifecycleStatus: undefined, pageNo: 1 })
  load()
}

// 首次进入页面等待根布局完成会话初始化；厂区变化由 watch 触发刷新。
onMounted(() => { if (siteCode.value) load() })
watch(siteCode, (value, previous) => {
  if (value && value !== previous) {
    query.pageNo = 1
    load()
  }
})
</script>

<template>
  <!-- 资产主档页面：筛选工具栏、分页表格和入库弹窗。 -->
  <section class="page-heading page-heading-compact">
    <div>
      <div class="section-kicker">ASSET REGISTER / INVENTORY</div>
      <h1>資產管理</h1>
      <p>维护静电衣鞋资产主档；发放、清洗和终止状态由专用业务交易驱动。</p>
    </div>
    <el-button type="primary" :icon="Plus" @click="dialogVisible = true">资产入库</el-button>
  </section>

  <section class="data-panel">
    <div class="toolbar">
      <el-tag class="site-context" effect="plain">厂区 {{ siteCode }}</el-tag>
      <el-input v-model="query.keyword" class="keyword-input" clearable placeholder="搜索资产编码或持有人" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="query.assetType" class="filter-select" clearable placeholder="物品类型">
        <el-option :value="1" label="静电衣" />
        <el-option :value="2" label="静电鞋" />
      </el-select>
      <el-select v-model="query.lifecycleStatus" class="filter-select" clearable placeholder="生命周期状态">
        <el-option v-for="(label, value) in statusName" :key="value" :value="Number(value)" :label="label" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="query.pageNo = 1; load()">查询</el-button>
      <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
    </div>

    <!-- 表格字段直接对应后端 AssetResp，状态名称由后端优先提供。 -->
    <el-table v-loading="loading" :data="rows" row-key="id" empty-text="当前厂区暂无资产">
      <el-table-column prop="assetCode" label="资产编码" min-width="150" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">{{ row.assetType === 1 ? '静电衣' : '静电鞋' }}</template>
      </el-table-column>
      <el-table-column prop="colorCode" label="颜色" width="100" />
      <el-table-column prop="sizeCode" label="尺码" width="100" />
      <el-table-column label="状态" min-width="120">
        <template #default="{ row }"><el-tag :type="statusType[row.lifecycleStatus]" effect="plain">{{ row.lifecycleStatusName || statusName[row.lifecycleStatus] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="当前持有人" min-width="150">
        <template #default="{ row }">{{ row.currentHolderName ? `${row.currentHolderNo} / ${row.currentHolderName}` : '—' }}</template>
      </el-table-column>
      <el-table-column prop="cleanCount" label="清洗次数" width="100" />
      <el-table-column prop="version" label="版本" width="80" />
    </el-table>
    <div class="table-footer">
      <span>共 {{ total }} 件资产</span>
      <el-pagination v-model:current-page="query.pageNo" v-model:page-size="query.pageSize" layout="prev, pager, next" :total="total" @current-change="load" />
    </div>
  </section>

  <!-- 入库只提交基础属性，生命周期和版本由后端统一生成。 -->
  <el-dialog v-model="dialogVisible" title="资产入库" width="500px" destroy-on-close>
    <el-form label-position="top">
      <el-form-item label="资产编码" required><el-input v-model="form.assetCode" maxlength="64" show-word-limit placeholder="如 J-Y-001" /></el-form-item>
      <el-form-item label="物品类型" required><el-radio-group v-model="form.assetType"><el-radio :value="1">静电衣</el-radio><el-radio :value="2">静电鞋</el-radio></el-radio-group></el-form-item>
      <div class="form-two-columns">
        <el-form-item label="颜色" required><el-input v-model="form.colorCode" maxlength="32" placeholder="如 黄色" /></el-form-item>
        <el-form-item label="尺码" required><el-input v-model="form.sizeCode" maxlength="32" placeholder="如 L / 42" /></el-form-item>
      </div>
      <div class="form-help"><el-icon><Box /></el-icon> 入库后初始状态为「可用库存」，清洗次数为 0。</div>
      <div class="dialog-actions">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确认入库</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
