<template>
  <div class="p-grid">
    <div class="p-col-12">
      <DataTable
        style="height: calc(100vh - 143px)"
        :scrollable="true"
        scrollHeight="flex"
        :value="exercises"
        :lazy="true"
        :paginator="true"
        :rows="pageSize"
        :totalRecords="total"
        @page="onPage($event)"
        :selection.sync="selectedExercise"
        selectionMode="single"
      >
        <template #header>
          <div>
            <Button label="新建" style="margin-right:1em" @click="onNewExercise" />
            <Button
              label="编辑"
              :disabled="null==selectedExercise"
              style="margin-right:1em"
              @click="onEditExercise"
            />
            <Button label="删除" :disabled="null==selectedExercise" @click="onDeleteExercise" />
          </div>
        </template>
        <Column>
          <template #filter>
            <ExerciseFilterBar v-model="filters"></ExerciseFilterBar>
          </template>
          <template #body="slotProps">
            <ExerciseViewer :configs="viewerConfigs" :exercise="slotProps.data"></ExerciseViewer>
          </template>
        </Column>
      </DataTable>
    </div>
    <DlgExerciseEditor ref="dlgExerciseEditor" />
    <!-- <DlgConfirm ref="dlgConfirm" /> -->
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";

import ExerciseEditor from "../components/ExerciseEditor.vue";
import { Exercise, AnswerMethod } from "../models/Exercise";
import DlgExerciseEditor from "../dialogs/DlgExerciseEditor.vue";
import ExerciseService from "../services/ExerciseService";
import ExerciseViewer, {
  ExerciseViewerConfig
} from "../components/ExerciseViewer.vue";
import { Ref, Watch } from "vue-property-decorator";
// import DlgConfirm from "../dialogs/DlgConfirm.vue";
import DlgService from "@/dialogs/DlgService";
import { Page } from "../queries/Page";
import ExerciseFilterBar from "@/components/ExerciseFilterBar.vue";
import Filter from "@/queries/Filter";
@Component({
  components: {
    ExerciseEditor,
    DlgExerciseEditor,
    // DlgConfirm,
    ExerciseViewer,
    ExerciseFilterBar
  }
})
export default class ExerciseManagement extends Vue {
  name = "ExerciseManagement";
  private viewerConfigs: ExerciseViewerConfig = {
    btnRelation: true,
    showContent: true,
    showAnswer: true,
    showExplain: true
  };
  selectedCategory: any = null;

  // @Ref()
  // private dlgConfirm!: DlgConfirm;

  private dlgSvc = DlgService.instance;

  private exercises: Exercise[] = [];

  private selectedExercise: Exercise | null = null;
  private filters: Filter[] = [];
  private onNewExercise() {
    // this.dlgExerciseEditor.open().then(() => {
    this.dlgExerciseEditor.open().subscribe(() => {
      this.reload();
    });
  }

  private onEditExercise() {
    // this.dlgExerciseEditor.open(this.selectedExercise).then(() => {
    this.dlgExerciseEditor.open(this.selectedExercise!).subscribe(() => {
      this.reload();
    });
  }

  private onDeleteExercise() {
    // this.dlgConfirm.open("请确认", "是否要删除选中的习题?").then(() => {
    this.dlgSvc.confirm("请确认", "是否要删除选中的习题?").subscribe(() => {
      this.exSvc.delete(this.selectedExercise!.id!).subscribe(() => {
        this.reload();
      });
    });
  }

  private exSvc = ExerciseService.instance;

  private reload() {
    this.loading = true;
    this.selectedExercise = null;
    this.exSvc
      .list({ page: this.page, size: this.pageSize, filters: this.filters })
      .subscribe(page => {
        const val = page as Page<Exercise>;
        this.exercises = val.content;
        this.total = val.totalElements;
        this.loading = false;
      });
  }

  @Watch("filters")
  private onFiltersChanged() {
    this.reload();
  }

  getAnswerMethodName(answerMethod: AnswerMethod | null): string {
    let name = "未分类";
    if (null != answerMethod) {
      switch (answerMethod) {
        case AnswerMethod.DANXUAN:
          name = "单选题";
          break;
        case AnswerMethod.DUOXUAN:
          name = "多选题";
          break;
        case AnswerMethod.TIANKONG:
          name = "填空题";
          break;
        case AnswerMethod.JIEDA:
          name = "解答题";
          break;
        case AnswerMethod.ZUOTU:
          name = "作图题";
          break;
        case AnswerMethod.JISUAN:
          name = "计算题";
          break;
        case AnswerMethod.ZONGHE:
          name = "综合题";
          break;
        case AnswerMethod.QITA:
          name = "其它";
          break;
        default:
          break;
      }
    }
    return name;
  }
  mounted() {
    this.reload();
  }

  onPage(event: any) {
    const first = event.page;
    const rows = event.rows;
    const page = first / rows;
    this.page = page;
    this.pageSize = rows;
    this.reload();
  }

  private pageSize = 10;
  private page = 0;
  private total = 0;
  private loading = false;

  @Ref()
  private dlgExerciseEditor!: DlgExerciseEditor;
}
</script>

<style scoped>
.e-tixing {
  font-size: 0.5em;
  color: dimgray;
}
</style>