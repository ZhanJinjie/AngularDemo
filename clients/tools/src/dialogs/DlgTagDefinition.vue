<template>
  <Dialog
    header="编辑标签信息"
    :visible.sync="visible"
    :modal="true"
    contentStyle="{'overflow': 'visible'}"
  >
    <div>
      <label style="margin-right:1em;">标签名称</label>
      <InputText type="text" v-model="name" />
    </div>
    <br />
    <div>
      <label for="description" style="margin-right:1em;">标签说明</label>
      <Textarea id="description" v-model="description" rows="5" cols="30" />
    </div>
    <br />
    <div>
      <label for="category" style="margin-right:1em;">标签类型</label>
      <Dropdown
        appendTo="body"
        id="category"
        v-model="selectedCategory"
        :options="categoryOptions"
        optionLabel="name"
        placeholder="请选择标签类型"
      />
    </div>
    <template #footer>
      <Button label="确认" icon="pi pi-check" @click="onOkClicked" />
      <Button label="取消" icon="pi pi-times" @click="onCancelClicked" class="p-button-secondary" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import TagDefinition from "../models/TagDefinition";
import TagCategory from "../models/TagCategory";
import TagDefinitionService from "../services/TagDefinitionService";
import TagCategoryService from "../services/TagCategoryService";
import { Observable, Subject } from "rxjs";

@Component
export default class DlgTagDefinition extends Vue {
  private name = "";
  private visible = false;
  private description = "";
  private selectedCategory: TagCategory | null = null;
  private categoryOptions: TagCategory[] = [];

  private resolve?: (
    value?: TagDefinition | PromiseLike<TagDefinition> | undefined
  ) => void;
  private reject?: (reason?: any) => void;
  onOkClicked() {
    this.visible = false;
    if (this.subject) {
      this.subject.next({
        id: this.tagDef!.id,
        name: this.name,
        description: this.description,
        categoryId:
          this.selectedCategory == null ? undefined : this.selectedCategory.id
      });
    }
    // if (this.resolve) {
    //   this.resolve({
    //     id: this.tagDef!.id,
    //     name: this.name,
    //     description: this.description,
    //     categoryId:
    //       this.selectedCategory == null ? undefined : this.selectedCategory.id
    //   });
    // }
  }
  onCancelClicked() {
    this.visible = false;
  }
  private tagDef?: TagDefinition;
  private tagDefSvc = TagDefinitionService.instance;
  private tagCatSvc = TagCategoryService.instance;
  open(tagDef: TagDefinition): Observable<TagDefinition> {
    this.tagCatSvc.list().subscribe(data => {
      const val = data.content;
      this.tagDef = tagDef;
      if (undefined != tagDef.name) {
        this.name = tagDef.name;
      }
      if (undefined != tagDef.description) {
        this.description = tagDef.description;
      }
      this.visible = true;
      val.unshift({ name: "未指定分类" });
      this.categoryOptions = val;
      for (const cat of val) {
        if (cat.id == tagDef.categoryId) {
          this.selectedCategory = cat;
          break;
        }
      }
    });
    // this.tagSvc.getCategories().then(val => {
    //   this.tagDef = tagDef;
    //   if (undefined != tagDef.name) {
    //     this.name = tagDef.name;
    //   }
    //   if (undefined != tagDef.description) {
    //     this.description = tagDef.description;
    //   }
    //   this.visible = true;
    //   val.unshift({ name: "未指定分类" });
    //   this.categoryOptions = val;
    //   for (const cat of val) {
    //     if (cat.id == tagDef.categoryId) {
    //       this.selectedCategory = cat;
    //       break;
    //     }
    //   }
    // });
    // return new Promise<TagDefinition>((resolve, reject) => {
    //   this.resolve = resolve;
    //   this.reject = reject;
    // });

    const subject = new Subject<TagDefinition>();
    this.subject = subject;
    return subject;
  }
  private subject?: Subject<TagDefinition>;
}
</script>