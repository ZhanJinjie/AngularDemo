import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import PanelMenu from "primevue/panelmenu";
import ContextMenu from 'primevue/contextmenu';
import Tree from 'primevue/tree';
import Paginator from 'primevue/paginator';
import Card from 'primevue/card';
import Button from 'primevue/button';
import Dropdown from 'primevue/dropdown';
import Editor from "primevue/editor";
import RadioButton from 'primevue/radiobutton';
import AutoComplete from 'primevue/autocomplete';
import Checkbox from 'primevue/checkbox';
import Listbox from 'primevue/listbox';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Textarea from 'primevue/textarea';
import SplitButton from 'primevue/splitbutton';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import Slider from 'primevue/slider';
Vue.config.productionTip = false
Vue.component("PanelMenu", PanelMenu);
Vue.component("ContextMenu", ContextMenu);
Vue.component("Tree", Tree);
Vue.component("Paginator", Paginator);
Vue.component("Card", Card);
Vue.component("Button", Button);
Vue.component("Dropdown", Dropdown);
Vue.component("RadioButton", RadioButton);
Vue.component("AutoComplete", AutoComplete);
Vue.component("Editor", Editor);
Vue.component("Checkbox", Checkbox);
Vue.component("Listbox", Listbox);
Vue.component("Dialog", Dialog);
Vue.component("InputText", InputText);
Vue.component("DataTable", DataTable);
Vue.component("Column", Column);
Vue.component("Textarea", Textarea);
Vue.component("SplitButton", SplitButton);
Vue.component("Accordion", Accordion);
Vue.component("AccordionTab", AccordionTab);
Vue.component("Slider", Slider);

Vue.directive('focus', {
  // When the bound element is inserted into the DOM...
  inserted: function (el) {
    // Focus the element
    setTimeout(() => {
      el.focus();
    }, 0);
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
