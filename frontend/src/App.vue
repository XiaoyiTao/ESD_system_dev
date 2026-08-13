<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Box, Location, Monitor, Setting, User } from '@element-plus/icons-vue'
import { useSessionStore } from './stores/session'

const route = useRoute()
const collapsed = ref(false)
const session = useSessionStore()

// 根布局只負責初始化一次平台會話，業務頁面通過 Pinia 讀取當前廠區。
onMounted(() => session.initialize())
</script>

<template>
  <!-- 統一後台殼：左側導航、頂部平台上下文、右側業務路由。 -->
  <el-container class="app-shell">
    <el-aside :width="collapsed ? '72px' : '232px'" class="app-aside">
      <div class="brand-mark">
        <span class="brand-icon">ESD</span>
        <span v-if="!collapsed" class="brand-name">靜電衣鞋管理</span>
      </div>
      <!-- 菜單項先按階段固定；後續可根據平台返回的菜單權限動態裁剪。 -->
      <el-menu :default-active="route.path" router class="app-menu">
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <template #title>運營概覽</template>
        </el-menu-item>
        <el-menu-item index="/persons">
          <el-icon><User /></el-icon>
          <template #title>人員檔案</template>
        </el-menu-item>
        <el-menu-item index="/assets">
          <el-icon><Box /></el-icon>
          <template #title>資產管理</template>
        </el-menu-item>
        <el-menu-item index="/settings" disabled>
          <el-icon><Setting /></el-icon>
          <template #title>系統設置</template>
        </el-menu-item>
      </el-menu>
      <button class="collapse-button" type="button" @click="collapsed = !collapsed">
        {{ collapsed ? '›' : '‹' }}
      </button>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div>
          <div class="header-eyebrow">PVD / ESD OPERATIONS</div>
          <div class="header-title">靜電防護資產中心</div>
        </div>
        <div class="header-actions">
          <!-- 當前廠區同時驅動人員和資產列表，服務端仍會重新校驗權限。 -->
          <el-select
            v-if="session.sites.length"
            :model-value="session.currentSite"
            class="site-selector"
            aria-label="当前厂区"
            @update:model-value="session.selectSite"
          >
            <template #prefix><el-icon><Location /></el-icon></template>
            <el-option v-for="site in session.sites" :key="site" :label="site" :value="site" />
          </el-select>
          <div class="header-user">
            <el-icon><User /></el-icon>
            <span>{{ session.user?.nickname || '平台用户' }}</span>
          </div>
        </div>
      </el-header>
      <el-main class="app-main">
        <!-- 會話未完成時不加載業務頁面，避免用空廠區發出無效請求。 -->
        <div v-if="session.loading" class="page-loading"><el-skeleton :rows="5" animated /></div>
        <el-empty v-else-if="!session.currentSite" description="当前账号未配置厂区权限" />
        <router-view v-else />
      </el-main>
    </el-container>
  </el-container>
</template>
