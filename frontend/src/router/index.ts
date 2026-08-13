import { createRouter, createWebHistory } from 'vue-router'

// 使用懶加載讓 Dashboard、人員和資產頁面按路由拆包，降低首屏下載量。
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/dashboard', component: () => import('../views/DashboardView.vue') },
    { path: '/persons', component: () => import('../views/persons/PersonsView.vue') },
    { path: '/assets', component: () => import('../views/assets/AssetsView.vue') },
  ],
})

export default router
