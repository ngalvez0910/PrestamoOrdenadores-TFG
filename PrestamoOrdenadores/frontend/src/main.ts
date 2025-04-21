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
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import router from './router'
import Menu from 'primevue/menu'
import Badge from 'primevue/badge';
import Toast from 'primevue/toast';
import ToastService from 'primevue/toastservice';
import Paginator from 'primevue/paginator';
import ConfirmDialog from 'primevue/confirmdialog';
import ConfirmationService from 'primevue/confirmationservice';

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue)
app.use(ToastService)
app.use(ConfirmationService);
app.component('ConfirmDialog', ConfirmDialog);
app.component('Menubar', Menubar)
app.component('Button', Button)
app.component('AdminMenuBar', AdminMenuBar)
app.component('DataTable', DataTable)
app.component('Column', Column)
app.component('Menu', Menu)
app.component('Badge', Badge)
app.component('Toast', Toast)
app.component('Paginator', Paginator)


app.mount('#app')
