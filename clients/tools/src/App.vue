<template>
  <div class="p-grid">
    <aside class="p-col-2 p-md-2">
      <PanelMenu :model="menuItems" />
    </aside>
    <div class="p-col-10 p-md-10 p-col-nogutter">
      <!-- <nav class="p-col-12 p-col-nogutter">Nav Bar</nav> -->
      <section class="p-col-12">
          <router-view :key="$route.fullPath" />
      </section>
    </div>
    <DlgConfirm ref="dlgConfirm"></DlgConfirm>
    <DlgExerciseRelationEditor ref="dlgExerciseRelationEditor"></DlgExerciseRelationEditor>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Ref } from "vue-property-decorator";
import DlgService from "@/dialogs/DlgService";
import DlgConfirm from "@/dialogs/DlgConfirm.vue";
import DlgExerciseRelationEditor from "@/dialogs/DlgExerciseRelationEditor.vue";
@Component({
  components: { DlgConfirm, DlgExerciseRelationEditor }
})
export default class App extends Vue {
  private dlgSvc = DlgService.instance;
  @Ref()
  private dlgConfirm!: DlgConfirm;
  @Ref()
  private dlgExerciseRelationEditor!: DlgExerciseRelationEditor;
  mounted() {
    this.dlgSvc.confirm = this.dlgConfirm.open;
    this.dlgSvc.openExerciseRelationEditor = this.dlgExerciseRelationEditor.open;
  }
  private menuItems = [
    {
      label: "题库管理",
      icon: "pi pi-fw ",
      items: [
        {
          label: "试题管理",
          icon: "pi pi-fw pi-list",
          to: "/"
        },
        {
          label: "教学大纲管理",
          icon: "pi pi-fw pi-th-large",
          to: "/outlines"
        },
        {
          label: "标签管理",
          icon: "pi pi-fw pi-tags",
          to: "/tags"
        }
      ]
    }
  ];
}
</script>

<style lang="scss">
@import "../node_modules/primevue/resources/primevue.min.css";
@import "../node_modules/primevue/resources/themes/saga-blue/theme.css";
@import "../node_modules/primeicons/primeicons.css";
@import "../node_modules/primeflex/primeflex.css";
</style>
