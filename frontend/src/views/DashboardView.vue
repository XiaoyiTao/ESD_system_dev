<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getEsdHealth } from '../api/health'
import type { HealthResponse } from '../types/api'

const loading = ref(true)
const health = ref<HealthResponse>()

onMounted(async () => {
  try {
    const response = await getEsdHealth()
    health.value = response.data
  } catch {
    ElMessage.warning('ESD 服務尚未連接，當前顯示工程預覽')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="page-heading">
    <div>
      <div class="section-kicker">CONTROL ROOM / 01</div>
      <h1>運營概覽</h1>
      <p>集中查看靜電衣鞋的流轉狀態與現場作業準備情況。</p>
    </div>
    <el-tag :type="health?.status === 'UP' ? 'success' : 'info'" effect="plain">
      {{ loading ? '檢查服務中' : health?.status === 'UP' ? '服務在線' : '預覽模式' }}
    </el-tag>
  </section>

  <section class="metric-grid">
    <article class="metric-card metric-card-primary">
      <span class="metric-label">可用庫存</span>
      <strong>--</strong>
      <span class="metric-note">待接入資產統計</span>
    </article>
    <article class="metric-card">
      <span class="metric-label">已發放</span>
      <strong>--</strong>
      <span class="metric-note">當前持有中的資產</span>
    </article>
    <article class="metric-card">
      <span class="metric-label">清洗中</span>
      <strong>--</strong>
      <span class="metric-note">等待完成清洗</span>
    </article>
    <article class="metric-card metric-card-alert">
      <span class="metric-label">活動報警</span>
      <strong>--</strong>
      <span class="metric-note">衣服或鞋子超量持有</span>
    </article>
  </section>

  <section class="dashboard-grid">
    <article class="panel panel-flow">
      <div class="panel-heading">
        <div>
          <span class="section-kicker">LIFECYCLE</span>
          <h2>資產生命周期</h2>
        </div>
        <span class="panel-status">第一階段</span>
      </div>
      <div class="lifecycle-track">
        <div><b>01</b><span>入庫</span><small>AVAILABLE</small></div>
        <i>→</i>
        <div><b>02</b><span>發放</span><small>ISSUED</small></div>
        <i>→</i>
        <div><b>03</b><span>回收送洗</span><small>IN_LAUNDRY</small></div>
        <i>→</i>
        <div><b>04</b><span>完成入庫</span><small>AVAILABLE</small></div>
      </div>
    </article>
    <article class="panel panel-service">
      <div class="panel-heading">
        <div>
          <span class="section-kicker">SYSTEM STATUS</span>
          <h2>服務連接</h2>
        </div>
      </div>
      <div class="service-row">
        <span><i class="status-dot" :class="{ online: health?.status === 'UP' }"></i>ESD 業務服務</span>
        <strong>{{ health?.status === 'UP' ? 'ONLINE' : '待啟動' }}</strong>
      </div>
      <div class="service-row">
        <span><i class="status-dot"></i>平台統一認證</span>
        <strong>待接入</strong>
      </div>
      <div class="service-row">
        <span><i class="status-dot"></i>MySQL</span>
        <strong>待接入</strong>
      </div>
    </article>
  </section>
</template>

