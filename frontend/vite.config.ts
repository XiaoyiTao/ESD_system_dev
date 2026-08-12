import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue()],
    server: {
      port: 5173,
      proxy: {
        // ESD 自有接口走 48082；这样本地无需修改前端请求路径。
        '/admin-api/esd': {
          target: env.VITE_DEV_API_TARGET || 'http://127.0.0.1:48082',
          changeOrigin: true,
        },
        // 登录、权限和其他平台接口走统一后台服务。
        '/admin-api': {
          target: env.VITE_PLATFORM_API_TARGET || 'http://127.0.0.1:48080',
          changeOrigin: true,
        },
      },
    },
  }
})
