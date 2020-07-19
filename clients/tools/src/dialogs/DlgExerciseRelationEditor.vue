<template>
  <Dialog header="管理试题相似度" :modal="true" :visible.sync="visible" :maximizable="true">
    <ExerciseViewer :configs="titleViewerConfigs" :exercise="exercise"></ExerciseViewer>
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
    >
      <template #header>
        <ExerciseFilterBar v-model="filters"></ExerciseFilterBar>
      </template>
      <Column header="试题">
        <template #body="slotProps">
          <ExerciseViewer :configs="viewerConfigs" :exercise="slotProps.data.exercise"></ExerciseViewer>
        </template>
      </Column>
      <Column header="相似度" headerStyle="width: 20em">
        <template #body="slotProps">
          <h3>相似度:{{slotProps.data.similar}}</h3>
          <Slider v-model="slotProps.data.similar" @change="onChange(slotProps.data)" />
        </template>
      </Column>
    </DataTable>
  </Dialog>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import ExerciseViewer, {
  ExerciseViewerConfig
} from "@/components/ExerciseViewer.vue";
import { Exercise } from "../models/Exercise";
import { Prop, Watch } from "vue-property-decorator";
import ExerciseService from "@/services/ExerciseService";
import Filter from "../queries/Filter";
import ExerciseFilterBar from "@/components/ExerciseFilterBar.vue";
import { debounceTime } from "rxjs/operators";
import { Subject, Observable, from } from "rxjs";
@Component({
  components: { ExerciseViewer, ExerciseFilterBar }
})
export default class ExerciseRelationManagement extends Vue {
  private exercise: Exercise = {};
  private visible = false;
  private titleViewerConfigs: ExerciseViewerConfig = {
    btnRelation: false,
    showContent: true,
    showAnswer: false,
    showExplain: false
  };
  open(exercise: Exercise) {
    this.exercise = exercise;
    this.visible = true;
    this.reload();
  }
  private pageNumber = 0;
  private pageSize = 10;
  private total = 0;

  private exercises: { exercise: Exercise; similar: number }[] = [];
  private filters: Filter[] = [];
  private exSvc = ExerciseService.instance;

  private onChange(data: { exercise: Exercise; similar: number }) {
    const subject = this.subjects[data.exercise.id!];
    subject.next(data);
  }

  @Watch("filters")
  private onFiltersChanged() {
    this.reload();
  }

  onPage(event: any) {
    const first = event.page;
    const rows = event.rows;
    const page = first / rows;
    this.pageNumber = page;
    this.pageSize = rows;
    this.reload();
  }

  private viewerConfigs: ExerciseViewerConfig = {
    btnRelation: false,
    showContent: true,
    showAnswer: false,
    showExplain: false
  };
  private subjects: {
    [key: string]: Subject<{ exercise: Exercise; similar: number }>;
  } = {};
  private reload() {
    this.exSvc
      .listSimilar(this.exercise.id, {
        page: this.pageNumber,
        size: this.pageSize,
        filters: this.filters
      })
      .subscribe(data => {
        this.total = data.totalElements;
        this.exercises = data.content;
        this.subjects = {};
        for (const ex of this.exercises) {
          const subject = new Subject<{
            exercise: Exercise;
            similar: number;
          }>();
          subject.pipe(debounceTime(1000)).subscribe(data => {
            this.exSvc.saveSimilar(
              this.exercise.id!,
              data.exercise.id!,
              data.similar
            );
          });
          this.subjects[ex.exercise.id!] = subject;
        }
      });
  }
}
</script>