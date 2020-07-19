<template>
  <div class="p-grid">
    <div class="p-col-3">
      <div>
        <SplitButton
          class="p-button-primary btn-menu"
          :label="currentAddBtn.label"
          :model="addBtns"
          @click="onAddBtnClicked(currentAddBtn.id)"
        ></SplitButton>
        <Button class="p-button-primary btn-menu" :disabled="!canEdit" label="编辑" @click="onEdit"></Button>
        <Button class="p-button-primary btn-menu" :disabled="!canEdit" label="删除" @click="onDelete"></Button>
      </div>
      <Tree
        :selectable="true"
        :value="outlineTreeNodes"
        :selectionKeys.sync="selectedOutlineNodeKeys"
        selectionMode="single"
        @node-select="onTreeNodeSelect"
      ></Tree>
    </div>
    <div class="p-col-9">
      <DataTable
        :value="exercises"
        :lazy="true"
        :loading="loading"
        :paginator="true"
        :rows="exSize"
        :totalRecords="total"
        @page="onPage($event)"
        :selection.sync="selectedExercise"
        selectionMode="single"
      >
        <template #header>
          <div>
            <Button
              label="添加习题"
              style="margin-right:1em"
              :disabled="selectedOutlineNodeKeys==null"
              @click="onAddExercise"
            />
            <Button
              label="移除习题"
              :disabled="null==selectedExercise"
              style="margin-right:1em"
              @click="onRemoveExercise"
            />
            <Button
              label="新建习题"
              style="margin-right:1em"
              :disabled="selectedOutlineNodeKeys==null"
              @click="onCreateExercise"
            />
            <Button
              label="编辑习题"
              style="margin-right:1em"
              :disabled="null==selectedExercise"
              @click="onEditExercise"
            />
            <Button
              label="删除习题"
              style="margin-right:1em"
              :disabled="null==selectedExercise"
              @click="onDeleteExercise"
            />
          </div>
        </template>
        <Column>
          <template #filter>
            <ExerciseFilterBar v-model="filters"></ExerciseFilterBar>
          </template>
          <template #body="slotProps">
            <ExerciseViewer :exercise="slotProps.data"></ExerciseViewer>
          </template>
        </Column>
      </DataTable>
    </div>
    <DlgEditOutlineNode ref="dlgEditOutlineNode"></DlgEditOutlineNode>
    <DlgExerciseSelector ref="dlgExerciseSelector"></DlgExerciseSelector>
    <DlgExerciseEditor ref="dlgExerciseEditor" />
    <DlgConfirm ref="dlgConfirm" />
  </div>
</template>
<style scoped>
.btn-menu {
  margin: 1em;
}
.filter-bar {
  display: flex;
  display: -webkit-flex;
  list-style: none;
  margin: 0;
  padding: 0;
  flex-wrap: wrap;
  justify-content: space-around;
}
.filter-bar > li {
  flex-grow: 1;
  margin: 0.5em;
}
.filter-bar > li > * {
  width: 100%;
}
</style>
<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import OutlineNodeService from "../services/OutlineNodeService";
import OutlineNode from "../models/OutlineNode";
import TagCategory from "../models/TagCategory";
import TreeNode from "../vms/TreeNode";
import DlgEditOutlineNode from "../dialogs/DlgEditOutlineNode.vue";
import { Ref, Watch } from "vue-property-decorator";
import { forkJoin, Observable } from "rxjs";
import DlgConfirm from "../dialogs/DlgConfirm.vue";
import { Exercise, AnswerMethod } from "../models/Exercise";
import ExerciseService from "@/services/ExerciseService";
import DlgExerciseSelector from "@/dialogs/DlgExerciseSelector.vue";
import RelationService from "@/services/RelationService";
import ExerciseViewer from "@/components/ExerciseViewer.vue";
import Filter from "@/queries/Filter";
import DlgExerciseEditor from "@/dialogs/DlgExerciseEditor.vue";
import ExerciseFilterBar from "@/components/ExerciseFilterBar.vue";
@Component({
  components: {
    DlgEditOutlineNode,
    DlgConfirm,
    DlgExerciseSelector,
    ExerciseViewer,
    DlgExerciseEditor,
    ExerciseFilterBar
  }
})
export default class OutlineManagement extends Vue {
  mounted() {
    this.reloadOutlines();
  }
  @Ref()
  private dlgExerciseEditor!: DlgExerciseEditor;

  private onCreateExercise() {
    this.dlgExerciseEditor
      .open(undefined, Object.keys(this.selectedOutlineNodeKeys)[0])
      .subscribe(() => {
        this.loadExercises();
      });
  }
  private onEditExercise() {
    this.dlgExerciseEditor.open(
      this.selectedExercise!,
      Object.keys(this.selectedOutlineNodeKeys)[0]
    );
  }
  private onDeleteExercise() {
    this.dlgConfirm.open("请确认", "是否删除已选中的试题?").subscribe(() => {
      this.exSvc.delete(this.selectedExercise!.id!).subscribe(() => {
        this.loadExercises();
      });
    });
  }

  private filters: Filter[] = [];
  private exPage = 0;
  private exSize = 10;
  private exSvc = ExerciseService.instance;
  private onTreeNodeSelect(node: TreeNode) {
    this.exPage = 0;
    this.exSize = 10;
    this.loadExercises(node.key);
  }

  private loadExercises(nodeId?: string) {
    let id = null;
    if (null != nodeId) {
      id = nodeId;
    } else {
      if (!this.selectedOutlineNodeKeys) {
        return;
      }
      id = Object.keys(this.selectedOutlineNodeKeys)[0];
    }
    this.selectedExercise = null;
    this.olSvc
      .findExercises(id, {
        page: this.exPage,
        size: this.exSize,
        filters: this.filters
      })
      .subscribe(data => {
        this.total = data.totalElements;
        this.exercises = data.content;
      });
  }

  @Watch("filters")
  private onFiltersChanged() {
    this.loadExercises();
  }

  private notEmpty(value: any) {
    return null != value && "" != (value as string).trim();
  }

  onPage(event: any) {
    const first = event.page;
    const rows = event.rows;
    const page = first / rows;
    this.exPage = page;
    this.exSize = rows;
    this.loadExercises();
  }

  private onAddExercise() {
    this.dlgExerciseSelector.open().subscribe(selections => {
      this.addExercises(selections);
    });
  }

  private rlSvc = RelationService.instance;

  private addExercises(exercises: Exercise[]) {
    if (exercises.length == 0) {
      return;
    }

    const exIds: string[] = [];
    exercises.forEach(ex => {
      exIds.push(ex.id!);
    });

    this.olSvc
      .addExercises(Object.keys(this.selectedOutlineNodeKeys)[0], exIds)
      .subscribe(() => {
        this.loadExercises();
      });
  }
  private onRemoveExercise() {
    this.olSvc
      .removeExercise(
        Object.keys(this.selectedOutlineNodeKeys)[0],
        this.selectedExercise!.id!
      )
      .subscribe(() => {
        this.loadExercises();
      });
  }

  private exercises: Exercise[] = [];
  private loading = false;
  private pageSize = 10;
  private total = 0;
  private selectedExercise: Exercise | null = null;

  private currentAddBtn: any = {
    label: "添加同级",
    icon: "pi pi-plus",
    id: "addSibling",
    command: () => {
      this.onAddBtnClicked("addSibling");
    }
  };
  private addBtns = [
    {
      label: "添加子级",
      icon: "pi pi-plus",
      id: "addChild",
      command: () => {
        this.onAddBtnClicked("addChild");
      }
    }
  ];

  get canEdit() {
    return this.getCurrentTreeNode() != null;
  }

  onEdit() {
    this.dlgEditOutlineNode
      .open(this.getCurrentTreeNode()!.data)
      .subscribe(() => {
        this.reloadOutlines();
      });
  }

  onDelete() {
    this.dlgConfirm.open("请确认", "是否删除节点?").subscribe(() => {
      this.olSvc.delete(this.getCurrentTreeNode()!.data.id).subscribe(() => {
        this.reloadOutlines();
      });
    });
  }

  @Ref()
  private dlgConfirm!: DlgConfirm;
  @Ref()
  private dlgExerciseSelector!: DlgExerciseSelector;
  private getCurrentTreeNode() {
    if (null != this.selectedOutlineNodeKeys) {
      return this.treeNodeMap!.get(
        Object.keys(this.selectedOutlineNodeKeys)[0]
      );
    } else {
      return null;
    }
  }

  private onAddBtnClicked(id: string) {
    const currentNode = this.getCurrentTreeNode();
    let parentKey: string | undefined;
    switch (id) {
      case "addSibling":
        if (null != currentNode) {
          parentKey = currentNode.parentKey;
        }
        break;
      case "addChild":
        if (null != currentNode) {
          parentKey = currentNode.key;
        }
        break;
      default:
        break;
    }
    this.dlgEditOutlineNode.open(null, parentKey).subscribe(() => {
      this.reloadOutlines();
    });
    this.switchAddBtn(id);
  }

  @Ref()
  private dlgEditOutlineNode!: DlgEditOutlineNode;

  private switchAddBtn(btnId: string) {
    const currentId = this.currentAddBtn.id;
    if (currentId == btnId) {
      return;
    }
    const btn = this.addBtns[0];
    this.addBtns = [this.currentAddBtn];
    this.currentAddBtn = btn;
  }

  private olSvc = OutlineNodeService.instance;
  private selectedOutlineNodeKeys: any = null;
  private treeNodeMap?: Map<string, TreeNode> = new Map();
  private reloadOutlines() {
    this.olSvc.list().subscribe(data => {
      this.outlines = data.content;
      this.outlineTreeNodes = [];
      this.treeNodeMap = new Map();
      this.outlines.forEach(node => {
        const trnode: TreeNode = {
          key: node.id,
          label: node.name,
          data: node,
          children: []
        };
        this.treeNodeMap!.set(trnode.key!, trnode);
        const syncChildren = (parent: TreeNode, children: OutlineNode[]) => {
          if (children == null) {
            return;
          }
          children.forEach(node => {
            const trchild = {
              key: node.id,
              label: node.name,
              data: node,
              parentKey: parent.key,
              children: []
            };
            this.treeNodeMap!.set(trchild.key!, trchild);
            syncChildren(trchild, node.children!);
            parent.children!.push(trchild);
          });
        };
        syncChildren(trnode, node.children!);
        this.outlineTreeNodes.push(trnode);
      });
    });
  }

  private outlineTreeNodes: TreeNode[] = [];
  private outlines: OutlineNode[] = [];
}
</script>

