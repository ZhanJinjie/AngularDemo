<template>
  <div class="p-grid">
    <div class="p-col-2">
      <h3>标签维度</h3>
      <Button class="p-button-primary btn-menu" label="添加" @click="addNewCategory"></Button>
      <Button
        class="p-button-primary btn-menu"
        :disabled="!canEditCategory"
        @click="editCategory"
        label="修改"
      ></Button>
      <Button
        class="p-button-primary btn-menu"
        :disabled="!canEditCategory"
        @click="deleteCategory"
        label="删除"
      ></Button>
      <Listbox
        v-model="selectedCategory"
        :options="categories"
        optionLabel="name"
        @input="onTagCatSelected"
      />
    </div>
    <div class="p-col-10">
      <DataTable :value="tagDefs" :selection.sync="selectedDef" selectionMode="single">
        <template #header>
          <div>
            <h3>标签列表</h3>
            <Button class="p-button-primary btn-menu" label="添加" @click="addNewDef"></Button>
            <Button
              class="p-button-primary btn-menu"
              :disabled="!canEditDef"
              label="修改"
              @click="editDef"
            ></Button>
            <Button
              class="p-button-primary btn-menu"
              :disabled="!canEditDef"
              label="删除"
              @click="deleteDef"
            ></Button>
          </div>
        </template>
        <Column header="标签" field="name"></Column>
        <Column header="说明" field="description"></Column>
      </DataTable>
    </div>
    <DlgTagCategory ref="dlgTagCategory" @on-ok="onCategoryEdited" />
    <DlgConfirm ref="dlgConfirm" />
    <DlgTagDefinition ref="dlgTagDefinition" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import TagCategory from "../models/TagCategory";
import { Watch, Ref } from "vue-property-decorator";
import DlgTagCategory from "../dialogs/DlgTagCategory.vue";
import DlgConfirm from "../dialogs/DlgConfirm.vue";
import DlgTagDefinition from "../dialogs/DlgTagDefinition.vue";
import TagDefinition from "../models/TagDefinition";
import TagDefinitionService from "../services/TagDefinitionService";
import TagCategoryService from "../services/TagCategoryService";
@Component({ components: { DlgTagCategory, DlgConfirm, DlgTagDefinition } })
export default class TagManagement extends Vue {
  selectedCategory: TagCategory | null = null;
  private isRejecting = false;
  @Ref()
  dlgTagCategory!: DlgTagCategory;
  @Ref()
  dlgConfirm!: DlgConfirm;
  @Ref()
  dlgTagDefinition!: DlgTagDefinition;

  private tagDefSvc = TagDefinitionService.instance;

  addNewDef() {
    this.dlgTagDefinition
      .open({
        name: "",
        description: "",
        categoryId:
          this.selectedCategory == null ? undefined : this.selectedCategory.id
      })
      .subscribe(val => {
        // .then(val => {
        this.tagDefSvc.create(val).subscribe(() => {
          this.reloadDefs(
            this.selectedCategory == null ? undefined : this.selectedCategory.id
          );
        });

        // this.tagSvc.createDef(val).then(() => {
        //   this.reloadDefs(
        //     this.selectedCategory == null ? undefined : this.selectedCategory.id
        //   );
        // });
      });
  }

  editDef() {
    // this.dlgTagDefinition.open(this.selectedDef!).then(val => {
    this.dlgTagDefinition.open(this.selectedDef!).subscribe(val => {
      this.tagDefSvc.update(val).subscribe(() => {
        this.reloadDefs(this.selectedCategory!.id);
      });
      // this.tagSvc.updateDef(val).then(() => {
      //   this.reloadDefs(this.selectedCategory!.id);
      // });
    });
  }

  deleteDef() {
    // this.dlgConfirm.open("请注意！", "是否删除选中的标签?").then(() => {
    this.dlgConfirm.open("请注意！", "是否删除选中的标签?").subscribe(() => {
      this.tagDefSvc.delete(this.selectedDef!.id!).subscribe(() => {
        this.reloadDefs(this.selectedCategory!.id);
      });

      // this.tagSvc.deleteDef(this.selectedDef!.id!).then(() => {
      //   this.reloadDefs(this.selectedCategory!.id);
      // });
    });
  }

  addNewCategory() {
    this.dlgTagCategory.open({
      name: ""
    });
  }

  private tagCatSvc = TagCategoryService.instance;

  onCategoryEdited() {
    let cat: TagCategory;
    if (null == this.selectedCategory || "0" == this.selectedCategory.id) {
      cat = {
        name: this.dlgTagCategory.name
      };

      this.tagCatSvc.create(cat).subscribe(data => {
        this.reloadCategories();
      });

      // this.tagSvc.createCategory(cat).then(data => {
      //   this.reloadCategories();
      // });
    } else {
      this.selectedCategory.name = this.dlgTagCategory.name;
      cat = this.selectedCategory;
      this.tagCatSvc.update(cat).subscribe();
      // this.tagSvc.updateCategory(cat);
    }
  }

  editCategory() {
    if (null != this.selectedCategory) {
      this.dlgTagCategory.open(this.selectedCategory);
    }
  }
  deleteCategory() {
    // this.dlgConfirm.open("请确认", "是否要删除标签分类?").then(() => {
    this.dlgConfirm.open("请确认", "是否要删除标签分类?").subscribe(() => {
      if (null != this.selectedCategory) {
        this.tagCatSvc.delete(this.selectedCategory.id!).subscribe(() => {
          this.reloadCategories();
          this.reloadDefs("0");
        });
        // this.tagSvc.deleteCategory(this.selectedCategory.id!);
      }
    });
  }

  private get canEditCategory() {
    return this.selectedCategory != null && this.selectedCategory.id != "0";
  }
  tagDefs: TagDefinition[] = [];
  categories: TagCategory[] = [];
  selectedDef: TagDefinition | null = null;

  private get canEditDef() {
    return this.selectedDef != null;
  }

  onTagCatSelected(event: any) {
    if (null != event) {
      this.reloadDefs(event.id);
    }
  }

  reloadDefs(catId?: string) {
    if (catId) {
      this.tagCatSvc.getDefs(catId).subscribe(val => {
        this.tagDefs = val;
      });
      // this.tagSvc.getDefByCategoryId(catId).then(val => {
      //   this.tagDefs = val;
      // });
    }
  }

  reloadCategories() {
    this.tagCatSvc.list().subscribe(val => {
      this.categories = val.content;
      this.categories.unshift({
        id: "0",
        name: "未分类的标签"
      });
    });
  }

  mounted() {
    this.reloadCategories();
  }
}
</script>

<style scoped>
.btn-menu {
  margin: 0.5em;
}
</style>