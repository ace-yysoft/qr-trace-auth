import { createRouter, createWebHistory } from 'vue-router'
import QrCodeGenerator from '@/components/QrCodeGenerator.vue'
import QrCodeScanner from '@/components/QrCodeScanner.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'generator',
      component: QrCodeGenerator
    },
    {
      path: '/scanner',
      name: 'scanner',
      component: QrCodeScanner
    }
  ]
})

export default router
