<template>
  <Dialog header="请选择试题" :visible.sync="visible" :modal="true" @show="onShow" :maximizable="true">
    <DataTable
      :scrollable="true"
      scrollHeight="flex"
      :value="exercises"
      :lazy="true"
      
      :paginator="true"
      :rows="pageSize"
      :totalRecords="total"
      @page="onPage($event)"
      :selection.sync="selectedExercises"
    >
      <Column selectionMode="multiple" headerStyle="width: 3em"></Column>
      <Column>
        <template #filter>
          <ExerciseFilterBar v-model="filters"></ExerciseFilterBar>
        </template>
        <template #body="slotProps">
          <ExerciseViewer :exercise="slotProps.data"></ExerciseViewer>
        </template>
      </Column>
    </DataTable>
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
import ExerciseViewer from "@/components/ExerciseViewer.vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import { Exercise } from "../models/Exercise";
import ExerciseService from "@/services/ExerciseService";
import { Subject } from "rxjs";
import { Page } from "../queries/Page";
import ExerciseFilterBar from "@/components/ExerciseFilterBar.vue";
import Filter from "@/queries/Filter";
import { Watch } from "vue-property-decorator";
@Component({
  components: { ExerciseViewer, ExerciseFilterBar }
})
export default class DlgExerciseSelector extends Vue {
  onOkClicked() {
    this.subject!.next(this.selectedExercises);
    this.subject!.complete();
    this.visible = false;
  }
  onCancelClicked() {
    this.visible = false;
  }

  open() {
    this.visible = true;
    this.subject = new Subject();
    return this.subject;
  }
  private filters: Filter[] = [];
  private subject?: Subject<Exercise[]>;
  private exercises: Exercise[] = [];
  private loading = false;
  private pageSize = 10;
  private page = 0;
  private total = 0;
  private selectedExercises = [];
  private visible = false;
  private onPage(event: any) {
    const first = event.page;
    const rows = event.rows;
    const page = first / rows;
    this.page = page;
    this.pageSize = rows;
    this.reload();
  }
  private exSvc = ExerciseService.instance;
  @Watch("filters")
  private onFiltersChanged() {
    this.reload();
  }
  private reload() {
    this.loading = true;
    this.exSvc
      .list({ page: this.page, size: this.pageSize, filters: this.filters })
      .subscribe(page => {
        const val = page as Page<Exercise>;
        this.exercises = val.content;
        this.total = val.totalElements;
        this.loading = false;
      });
  }
  onShow() {
    this.selectedExercises = [];
    this.reload();
  }
}
</script>