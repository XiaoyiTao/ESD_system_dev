import { createRouter, createWebHistory } from 'vue-router'
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
