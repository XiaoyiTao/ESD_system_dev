import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles.css'
import App from './App.vue'
import router from './router'

// Pinia 必须在 App 挂载前注册，保证顶部会话上下文和各业务页面共享同一状态。
createApp(App).use(createPinia()).use(router).use(ElementPlus).mount('#app')
