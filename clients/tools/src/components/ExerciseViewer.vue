<template>
  <Card>
    <template #content>
      <Button v-if="configs.btnRelation" label="管理相似题型" @click="onEditRelation" style="height:2em;"></Button>
      <p>【编号】{{exercise.id}}</p>
      <p>【难度系数】{{exercise.difficulty}}</p>
      <p>【题型】{{getAnswerMethodName(exercise.answerMethod)}}</p>
      <div>【试题内容】</div>
      <div v-html="exercise.content"></div>
      <p v-if="configs.showAnswer">【答案】</p>
      <div v-if="configs.showAnswer" v-html="exercise.answer"></div>
      <p v-if="configs.showExplain">【解析】</p>
      <div v-if="configs.showExplain" v-html="exercise.explain"></div>
    </template>
  </Card>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Exercise, AnswerMethod } from "../models/Exercise";
import { Prop } from "vue-property-decorator";
import DlgService from "@/dialogs/DlgService";

export interface ExerciseViewerConfig {
  btnRelation: boolean;
  showContent: boolean;
  showAnswer: boolean;
  showExplain: boolean;
}

@Component
export default class ExerciseViewer extends Vue {
  @Prop({
    default: {}
  })
  exercise!: Exercise;

  private dlgSvc = DlgService.instance;

  @Prop({
    default: () => {
      return {
        btnRelation: false,
        showContent: true,
        showExplain: true,
        showAnswer: true
      } as ExerciseViewerConfig;
    }
  })
  configs!: ExerciseViewerConfig;

  private onEditRelation() {
    this.dlgSvc.openExerciseRelationEditor(this.exercise);
  }

  private getAnswerMethodName(answerMethod: AnswerMethod | null): string {
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
}
</script>