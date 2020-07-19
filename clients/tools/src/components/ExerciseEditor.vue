<template >
  <div>
    <Card>
      <template #content>
        <p>【试题内容】</p>
        <FormulaEditor v-model="exercise.content" placeholder="请输入试题内容">
          <template slot="toolbar"></template>
        </FormulaEditor>
        <ul class="answerContainer">
          <li>
            <p>【答题形式】</p>
            <ul class="radioGroup">
              <li>
                <RadioButton
                  :id="'rddanxuan'+_uid"
                  name="answermethod"
                  value="danxuan"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbdanxuan'+_uid">单选</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbduoxuan'+_uid"
                  name="answermethod"
                  value="duoxuan"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbduoxuan'+_uid">多选</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbtiankong'+_uid"
                  name="answermethod"
                  value="tiankong"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbtiankong'+_uid">填空</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbjieda'+_uid"
                  name="answermethod"
                  value="jieda"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbjieda'+_uid">解答</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbzuotu'+_uid"
                  name="answermethod"
                  value="zuotu"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbzuotu'+_uid">作图</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbjisuan'+_uid"
                  name="answermethod"
                  value="jisuan"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbjisuan'+_uid">计算</label>
              </li>
              <li>
                <RadioButton
                  :id="'rbzonghe'+_uid"
                  name="answermethod"
                  value="zonghe"
                  v-model="exercise.answerMethod"
                />
                <label :for="'rbzonghe'+_uid">综合</label>
              </li>
            </ul>
          </li>
          <li>
            <p>【答案】</p>
            <FormulaEditor v-model="exercise.answer" placeholder="请输入正确答案"></FormulaEditor>
          </li>
        </ul>
        <div class="expalinContainer">
          <p>【试题解析】</p>
          <FormulaEditor v-model="exercise.explain" />
        </div>
        <div>
          <p>【难度系数】</p>
          <div class="p-grid">
            <div class="p-col-11">
              <Slider style="margin-top:2em" v-model="exercise.difficulty" />
            </div>
            <div class="p-col-1">
              <h1>{{floatDificulty}}</h1>
            </div>
          </div>
        </div>
        <div>
          <p>【标签】</p>
          <DataTable :value="tagsvm" editMode="cell">
            <Column field="catName" header="类别"></Column>
            <Column field="tags" header="标签">
              <template #editor="slotProps">
                <AutoComplete
                  field="name"
                  :dropdown="true"
                  :multiple="true"
                  v-model="slotProps.data[slotProps.column.field]"
                  :suggestions="tagSuggestions"
                  appendTo="body"
                  @complete="searchTagMultiple($event,slotProps.data)"
                  @item-select="onTagSelected"
                  @item-unselect="onTagUnSelected"
                  @clear="onTagCleared"
                />
              </template>
              <template #body="slotProps">
                <AutoComplete
                  field="name"
                  :dropdown="true"
                  :multiple="true"
                  v-model="slotProps.data[slotProps.column.field]"
                  :suggestions="tagSuggestions"
                  appendTo="body"
                  @complete="searchTagMultiple($event,slotProps.data)"
                  @item-select="onTagSelected"
                  @item-unselect="onTagUnSelected"
                  @clear="onTagCleared"
                />
              </template>
            </Column>
          </DataTable>
        </div>
      </template>
    </Card>
  </div>
</template>
<script lang="ts">
import { Component, Prop, Vue, Watch, Model } from "vue-property-decorator";
import { Exercise, AnswerMethod } from "../models/Exercise";
import FormulaEditor from "./FormulaEditor.vue";
import katex from "katex";
import Tag from "../models/Tag";
import TagCategory from "../models/TagCategory";
import TagDefinition from "../models/TagDefinition";
import TagCategoryService from "@/services/TagCategoryService";
import TagDefinitionService from "@/services/TagDefinitionService";
import { merge, forkJoin } from "rxjs";
window.katex = katex;
@Component({ components: { FormulaEditor } })
export default class ExerciseEditor extends Vue {
  @Model()
  exercise!: Exercise;

  onTagSelected(event: any) {
    this.addTag(event.value);
  }
  onTagUnSelected(event: any) {
    this.removeTag(event.value);
  }
  onTagCleared(event: any) {
    console.log(event);
  }

  addTag(tagDef: TagDefinition) {
    if (this.exercise.tags == null) {
      this.exercise.tags = [];
    }
    let hasDuplicated = false;
    for (const tag of this.exercise.tags) {
      if (tag.defId == tagDef.id || (tag.defId == null && tagDef.id == "0")) {
        hasDuplicated = true;
        break;
      }
    }
    if (!hasDuplicated) {
      this.exercise.tags.push({
        name: tagDef.name,
        defId: tagDef.id,
        categoryId: tagDef.categoryId == null ? undefined : tagDef.categoryId
      });
    }
  }

  get floatDificulty() {
    if (this.exercise.difficulty) {
      return this.exercise.difficulty / 100;
    } else {
      return 0;
    }
  }

  removeTag(tagDef: TagDefinition) {
    if (this.exercise.tags == null) {
      this.exercise.tags = [];
    }
    this.exercise.tags = this.exercise.tags.filter((tag, index) => {
      return tag.defId != tagDef.id && tag.defId != null && tagDef.id != "0";
    });
  }

  clearTag() {
    console.log("clear tag");
  }

  mounted() {
    this.reloadTagMgnData();
  }

  @Watch("exercise")
  onExerciseChanged(newVal: Exercise, oldVal: Exercise) {
    this.reloadTagMgnData();
  }
  private tagCatSvc = TagCategoryService.instance;
  private tagDefSvc = TagDefinitionService.instance;
  /**
   * 加载标签管理需要的基础数据
   */
  private reloadTagMgnData() {
    const catPromise = this.tagCatSvc.list();
    const defPromise = this.tagDefSvc.list();

    // return Promise.all([catPromise, defPromise]).then(vals => {
    return forkJoin(catPromise, defPromise).subscribe(vals => {
      this.tagCategories = vals[0].content;
      this.tagCategories.unshift({
        id: "0",
        name: "未分类的标签"
      });

      this.tagCategoriesMap = {};
      for (const cat of this.tagCategories) {
        this.tagCategoriesMap[cat.id!] = cat;
      }
      this.tagDefsMap = {};
      this.tagDefs = vals[1].content;
      for (const def of this.tagDefs) {
        this.tagDefsMap[def.id!] = def;
      }

      //建立以类别id为索引的表，每个表项是一个tagDef对象数组
      const tagIndex: { [catId: string]: TagDefinition[] } = {};
      if (null != this.exercise.tags) {
        for (const tag of this.exercise.tags) {
          const catId = undefined == tag.categoryId ? "0" : tag.categoryId;
          let defs = tagIndex[catId];
          if (null == defs) {
            defs = [];
            tagIndex[catId] = defs;
          }
          defs.push(this.tagDefsMap[tag.defId!]);
        }
      }
      //初始化tag数据
      this.tagsvm = [];
      for (const cat of this.tagCategories) {
        let tags = tagIndex[cat.id!];
        if (null == tags) {
          tags = [];
        }
        this.tagsvm.push({
          catId: cat.id!,
          catName: cat.name!,
          cat: cat,
          tags: tags
        });
      }
    });
  }

  private tagsvm: {
    catId: string;
    catName: string;
    cat: TagCategory;
    tags: TagDefinition[];
  }[] = [];
  private tagCategoriesMap: { [id: string]: TagCategory } = {}; //标签分类快查表
  private tagDefsMap: { [id: string]: TagDefinition } = {}; //标签定义快查表
  private tagCategories: TagCategory[] = [];
  private tagDefs: TagDefinition[] = [];

  private tagSuggestions: TagDefinition[] = [];

  private searchTagMultiple(event: any, data: any) {
    const query = event.query;
    this.tagSuggestions = [];
    this.tagCategoriesMap;
    const catId = data.catId as string;
    if ("" == query) {
      for (const def of this.tagDefs) {
        const defCatId = null == def.categoryId ? "0" : def.categoryId;
        if (defCatId == catId) {
          this.tagSuggestions.push(def);
        }
      }
    } else {
      for (const def of this.tagDefs) {
        const s = String(def.name);
        const defCatId = null == def.categoryId ? "0" : def.categoryId;
        if (s.includes(query) && defCatId == catId) {
          this.tagSuggestions.push(def);
        }
      }
    }
  }

  private explain = "";

  get isDanXuan() {
    return this.exercise.answerMethod == AnswerMethod.DANXUAN;
  }

  get isDuoXuan() {
    return this.exercise.answerMethod == AnswerMethod.DUOXUAN;
  }

  private selectableOptions: any = [
    {
      name: "单选题",
      code: AnswerMethod.DANXUAN
    },
    {
      name: "多选题",
      code: AnswerMethod.DUOXUAN
    },
    {
      name: "填空题",
      code: AnswerMethod.TIANKONG
    },
    {
      name: "解答题",
      code: AnswerMethod.JIEDA
    },
    {
      name: "作图题",
      code: AnswerMethod.ZUOTU
    },
    {
      name: "计算题",
      code: AnswerMethod.JISUAN
    },
    {
      name: "综合题",
      code: AnswerMethod.ZONGHE
    }
  ];
}
</script>

<style scoped>
ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.radioGroup {
  display: flex;
}

.radioGroup li {
  margin: 1em;
}

.radioGroup label {
  margin-left: 1em;
}

button {
  margin: 1em;
}
.xuanxiangliebiao {
  display: flex;
}
.xuanxiangliebiao li {
  padding: 1em;
  width: 20em;
}
@import "../../node_modules/katex/dist/katex.css";
</style>
