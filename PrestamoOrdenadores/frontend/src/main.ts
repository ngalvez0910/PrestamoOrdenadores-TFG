import './assets/main.css'
import axios from 'axios'
import './assets/base.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'primevue/resources/themes/lara-light-indigo/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';
import '@fortawesome/fontawesome-free/css/all.css'

import App from './App.vue'
import PrimeVue from 'primevue/config'
import Menubar from 'primevue/menubar'
import Button from 'primevue/button'
import MenuBar from "@/components/MenuBar.vue";
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import router from './router'
import Menu from 'primevue/menu'
import Badge from 'primevue/badge';
import Toast from 'primevue/toast';
import ToastService from 'primevue/toastservice';
import Paginator from 'primevue/paginator';
import ConfirmDialog from 'primevue/confirmdialog';
import Sidebar from "primevue/sidebar";
import Tooltip from "primevue/tooltip";
import ConfirmationService from 'primevue/confirmationservice';
import {authService} from "@/services/AuthService.ts";

axios.interceptors.request.use(
    config => {
        const token = authService.token;
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue)
app.use(ToastService)
app.use(ConfirmationService);
app.component('ConfirmDialog', ConfirmDialog);
app.component('Menubar', Menubar)
app.component('Button', Button)
app.component('MenuBar', MenuBar)
app.component('DataTable', DataTable)
app.component('Column', Column)
app.component('Menu', Menu)
app.component('Badge', Badge)
app.component('Toast', Toast)
app.component('Sidebar', Sidebar)
app.component('Paginator', Paginator)


app.mount('#app')
