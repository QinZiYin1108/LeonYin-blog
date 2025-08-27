import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 将router实例挂载到window对象，以便在http.js中使用
window.router = router
// 将ElementPlus的消息组件挂载到window对象，以便在http.js中使用
import { ElMessage } from 'element-plus'
window.ElMessage = ElMessage

createApp(App).use(router).use(ElementPlus).mount('#app')
