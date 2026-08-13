import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles.css'
import App from './App.vue'
import router from './router'

// Pinia 必須在 App 掛載前註冊，保證頂部會話上下文和各業務頁面共享同一狀態。
createApp(App).use(createPinia()).use(router).use(ElementPlus).mount('#app')
