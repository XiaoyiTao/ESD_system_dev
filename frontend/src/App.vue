<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { Monitor, Setting, User } from '@element-plus/icons-vue'

const route = useRoute()
const collapsed = ref(false)
</script>

<template>
  <el-container class="app-shell">
    <el-aside :width="collapsed ? '72px' : '232px'" class="app-aside">
      <div class="brand-mark">
        <span class="brand-icon">ESD</span>
        <span v-if="!collapsed" class="brand-name">靜電衣鞋管理</span>
      </div>
      <el-menu :default-active="route.path" router class="app-menu">
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <template #title>運營概覽</template>
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
        <div class="header-user">
          <el-icon><User /></el-icon>
          <span>平台用戶</span>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

