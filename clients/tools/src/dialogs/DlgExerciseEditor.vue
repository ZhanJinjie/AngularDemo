<template>
  <Dialog header="编辑试题" :visible.sync="visible" :modal="true" @hide="onDlgHide" :maximizable="true">
    <ExerciseEditor :exercise="exercise" />
    <template #footer>
      <div style="margin-right: 10em;margin-left: auto;width:40em;">
        <Button label="保存" icon="pi pi-check" class="p-button-primary" @click="onSave" />
        <Button label="取消" icon="pi pi-times" class="p-button-secondary" @click="onCancel" />
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop, Emit, Watch } from "vue-property-decorator";
import ExerciseEditor from "../components/ExerciseEditor.vue";
import { Exercise } from "../models/Exercise";
import ExerciseService from "../services/ExerciseService";
import { Observable, observable, Subscriber, Subject } from "rxjs";

@Component({
  components: { ExerciseEditor }
})
export default class DlgExerciseEditor extends Vue {
  private visible = false;

  private exSvc = ExerciseService.instance;

  private exercise: Exercise = {
    tags: []
  };

  open(exercise?: Exercise, outlineNodeId?: string): Observable<void> {
    if (null == exercise) {
      this.exercise = {
        tags: []
      };
    } else {
      this.exercise = {
        id: exercise.id,
        content: exercise.content,
        answerMethod: exercise.answerMethod,
        explain: exercise.explain,
        difficulty: exercise.difficulty,
        tags: exercise.tags
      };
    }
    if (outlineNodeId != null) {
      this.exercise.outlineNodeId = outlineNodeId;
    }
    this.visible = true;
    const observable = new Observable<void>(subscriber => {
      this.subscriber = subscriber;
    });
    return observable;
  }

  private subscriber?: Subscriber<void>;
  onSave() {
    this.save().subscribe(() => {
      this.visible = false;
    });
  }

  save(): Observable<any> {
    if (null == this.exercise.id) {
      return this.exSvc.create(this.exercise);
    } else {
      return this.exSvc.update(this.exercise);
    }
  }

  onSaveAndNew() {
    this.save().subscribe(() => {
      this.exercise = {
        tags: []
      };
      this.exercise.content = "";
      this.exercise.answer = "";
      this.exercise.explain = "";
    });
  }

  onCancel() {
    this.visible = false;
  }

  onDlgHide() {
    if (null != this.subscriber) {
      this.subscriber.next();
      this.subscriber.complete();
    }
  }
}
</script>