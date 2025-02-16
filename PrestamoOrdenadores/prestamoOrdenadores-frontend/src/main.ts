import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'primevue/resources/themes/lara-light-indigo/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

import App from './App.vue'
import PrimeVue from 'primevue/config'
import Menubar from 'primevue/menubar'
import Button from 'primevue/button'
import AdminMenuBar from "@/components/AdminMenuBar.vue";
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue)
app.component('Menubar', Menubar)
app.component('Button', Button)
app.component('AdminMenuBar', AdminMenuBar)

app.mount('#app')
