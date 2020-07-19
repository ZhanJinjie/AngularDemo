<template>
  <Dialog header="编辑标签分类" :visible.sync="visible" :modal="true">
    <label style="margin-right:1em">分类名称</label>
    <InputText type="text" v-model="name" />
    <template #footer>
      <Button label="确认" icon="pi pi-check" @click="onOkClicked" />
      <Button label="取消" icon="pi pi-times" @click="onCancelClicked" class="p-button-secondary" />
    </template>
  </Dialog>
</template>
<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop, Watch, Emit } from "vue-property-decorator";
import TagCategory from "../models/TagCategory";

@Component
export default class DlgTagCategory extends Vue {
  open(category: TagCategory) {
    this.name = category.name;
    this.visible = true;
  }
  private onOkClicked() {
    this.visible = false;
    this.onOk();
  }
  private onCancelClicked() {
    this.visible = false;
    this.onCancled();
  }

  @Emit()
  onOk() {
    return;
  }

  @Emit()
  onCancled() {
    return;
  }

  visible = false;

  @Prop()
  category!: TagCategory;
  name?: string = "";
}
</script>