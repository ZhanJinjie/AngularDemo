<template>
  <Dialog :header="header" :visible.sync="visible" :modal="true" @show="onShow">
    {{content}}
    <template #footer>
      <Button label="确认" icon="pi pi-check" class="p-button-danger" @click="onOkClicked" />
      <Button
        ref="btnCancel"
        label="取消"
        icon="pi pi-times"
        @click="onCancelClicked"
        class="p-button-primary"
        v-focus
      />
    </template>
  </Dialog>
</template>
<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop, Watch, Emit, Ref } from "vue-property-decorator";
import TagCategory from "../models/TagCategory";
import { Button } from "primevue/button";
import { Observable, Subject } from "rxjs";

@Component
export default class DlgConfirm extends Vue {
  open(header: string, content: string): Observable<any> {
    this.header = header;
    this.content = content;
    this.visible = true;
    // const promise = new Promise((resolve, reject) => {
    //   this.resolve = resolve;
    //   this.reject = reject;
    // });
    const subject = new Subject<any>();
    this.subject = subject;
    return subject;
  }
  private subject?: Subject<any>;
  // private resolve?: (value?: any) => void;
  // private reject?: (reason?: any) => void;
  @Ref()
  private btnCancel!: Vue;
  private onOkClicked() {
    this.visible = false;
    // if (null != this.resolve) {
    //   this.resolve();
    // }
    this.subject!.next();
    this.onOk();
  }
  private onCancelClicked() {
    this.visible = false;
    this.onCancled();
  }

  private onShow() {
    setTimeout(() => {
      // (this.btnCancel.$el as HTMLElement).focus();
      console.log("on show");
    }, 0);
  }

  header = "";

  content = "";

  @Emit()
  onOk() {
    return;
  }

  @Emit()
  onCancled() {
    return;
  }

  visible = false;
}
</script>