import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue()],
    server: {
      port: 5173,
      proxy: {
        '/admin-api': {
          target: env.VITE_DEV_API_TARGET || 'http://127.0.0.1:48082',
          changeOrigin: true,
        },
      },
    },
  }
})

