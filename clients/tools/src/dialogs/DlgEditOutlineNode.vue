<template>
  <Dialog header="节点信息" :visible.sync="visible" :modal="true">
    <label style="margin-right:1em">名称</label>
    <InputText type="text" v-model="name" />
    <br />
    <br />
    <label style="margin-right:1em">备注</label>
    <Textarea rows="5" cols="30" v-model="description" />
    <br />
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
import { Subject } from "rxjs";
import TreeNode from "../vms/TreeNode";
import OutlineNodeService from "../services/OutlineNodeService";
import OutlineNode from "../models/OutlineNode";
@Component
export default class DlgConfirm extends Vue {
  open(node: OutlineNode | null = null, parentId?: string) {
    if (null == node) {
      this.name = null;
      this.description = null;
      this.id = null;
      this.parentId = parentId;
    } else {
      this.name = node.name;
      this.description = node.description;
      this.id = node.id;
      this.parentId = node.parentId;
    }
    this.visible = true;
    this.subject = new Subject();
    return this.subject;
  }

  private id?: string | null = null;
  private parentId?: string | null = null;
  private name?: string | null = null;
  private description?: string | null = null;

  private olSvc = OutlineNodeService.instance;

  private subject?: Subject<any>;
  private visible = false;
  onOkClicked() {
    const target: OutlineNode = {
      name: this.name!,
      description: this.description!,
      parentId: this.parentId!
    };

    if (this.id == null) {
      this.olSvc.create(target).subscribe(id => {
        this.visible = false;
        target.id = id;
        this.subject!.next(target);
        this.subject!.complete();
      });
    } else {
      target.id = this.id;
      this.olSvc.update(target).subscribe(() => {
        this.visible = false;
        this.subject!.next(target);
        this.subject!.complete();
      });
    }
  }
  onCancelClicked() {
    this.visible = false;
  }
}
</script>