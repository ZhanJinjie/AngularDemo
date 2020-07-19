<template>
  <ul class="filter-bar">
    <li>
      <InputText
        type="text"
        placeholder="试题编号"
        @blur="onBlur"
        @keypress="onKeyPress"
        v-model="filters.id.value"
      />
    </li>
    <li>
      <Dropdown
        appendTo="body"
        optionLabel="name"
        v-model="filters.answerMethod.value"
        :options="filters.answerMethod.options"
        :filter="true"
        placeholder="试题类型"
        @input="onAnswerMethodFiltered"
        :showClear="true"
      ></Dropdown>
    </li>
    <li>
      <InputText
        type="text"
        placeholder="试题内容"
        @blur="onBlur"
        @keypress="onKeyPress"
        v-model="filters.content.value"
      />
    </li>

    <li>
      <span>
        <AutoComplete
          field="name"
          :dropdown="true"
          :multiple="true"
          v-model="filters.tags.value"
          :suggestions="tagDefSuggestions"
          appendTo="body"
          @complete="searchTagDefMultiple($event)"
          @item-select="onTagDefSelected"
          @item-unselect="onTagDefUnSelected"
          @clear="onTagDefCleared"
          class="p-mr-2"
        />
        <Button
          icon="pi pi-search"
          class="p-button-rounded p-button-success"
          @click="onRefreshClicked"
        />
      </span>
    </li>
  </ul>
</template>
<style scoped>
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
import { AnswerMethod } from "@/models/Exercise";
import { Emit, Prop } from "vue-property-decorator";
import Filter from "@/queries/Filter";
import TagDefinitionService from "../services/TagDefinitionService";
import TagDefinition from "../models/TagDefinition";
interface TagDefOption {
  def: TagDefinition;
  displayName: string;
}
@Component({})
export default class ExerciseFilterBar extends Vue {
  private filters = {
    content: {
      value: null,
      oldValue: null
    },
    id: {
      value: null,
      oldValue: null
    },
    tags: {
      value: [] as TagDefinition[],
      oldValue: [] as TagDefinition[]
    },
    answerMethod: {
      value: { name: null, code: null },
      oldValue: { name: null, code: null },
      options: [
        { name: "单选题", code: AnswerMethod.DANXUAN },
        { name: "多选题", code: AnswerMethod.DUOXUAN },
        { name: "填空题", code: AnswerMethod.TIANKONG },
        { name: "解答题", code: AnswerMethod.JIEDA },
        { name: "作图题", code: AnswerMethod.ZUOTU },
        { name: "计算题", code: AnswerMethod.JISUAN },
        { name: "综合题", code: AnswerMethod.ZONGHE },
        { name: "其它题", code: AnswerMethod.QITA }
      ]
    }
  };

  private tagDevSvc = TagDefinitionService.instance;
  private selectedTagDefs: TagDefinition[] = [];
  private tagDefSuggestions: TagDefinition[] = [];

  private searchTagDefMultiple(event: any) {
    const query = event.query.trim();
    if ("" == query) {
      this.tagDefSuggestions = [...this.tagDefs];
    } else {
      this.tagDefSuggestions = [];
      for (const def of this.tagDefs) {
        const n = String(def.name);
        if (n.includes(query)) {
          this.tagDefSuggestions.push(def);
        }
      }
    }
  }
  private tagDefs!: TagDefinition[];
  private defMap: { [key: string]: TagDefinition } = {};
  mounted() {
    this.tagDevSvc.list().subscribe(defs => {
      this.tagDefs = defs.content;
      this.defMap = {};
      for (const def of this.tagDefs) {
        this.defMap[def.id!] = def;
      }
    });
  }

  private onRefreshClicked() {
    this.input();
  }

  private onTagDefSelected() {
    if (this.hasFilterChanges()) {
      this.input();
    }
  }
  private onTagDefUnSelected() {
    if (this.hasFilterChanges()) {
      this.input();
    }
  }
  private onTagDefCleared() {
    if (this.hasFilterChanges()) {
      this.input();
    }
  }

  private hasFilterChanges() {
    let hasChanges = false;
    if (this.filters.content.value != this.filters.content.oldValue) {
      hasChanges = true;
      this.filters.content.oldValue = this.filters.content.value;
    }
    if (this.filters.answerMethod.value != this.filters.answerMethod.oldValue) {
      hasChanges = true;
      this.filters.answerMethod.oldValue = this.filters.answerMethod.value;
    }
    if (this.filters.tags.oldValue != this.filters.tags.value) {
      hasChanges = true;
      this.filters.tags.oldValue = this.filters.tags.value;
    }
    if (this.filters.id.oldValue != this.filters.id.value) {
      hasChanges = true;
      this.filters.id.oldValue = this.filters.id.value;
    }
    return hasChanges;
  }

  private onAnswerMethodFiltered(event: any) {
    if (this.hasFilterChanges()) {
      this.input();
    }
  }

  private onBlur(event: any) {
    if (this.hasFilterChanges()) {
      this.input();
    }
  }
  private onKeyPress(event: any) {
    if (event.key != "Enter") {
      return;
    }
    if (this.hasFilterChanges()) {
      this.input();
    }
  }
  private notEmpty(value: any) {
    return null != value && "" != (value as string).trim();
  }

  @Prop()
  value!: Filter[];

  get _value() {
    return this.value;
  }

  set _value(value: Filter[]) {
    this.value = value;
    value.forEach(filter => {
      if (!filter.args || filter.args.length == 0) {
        return;
      }
      if (filter.field == "content") {
        this.filters.content.value = filter.args[0];
      }
      if (filter.field == "answerMethod") {
        this.filters.answerMethod.value = filter.args[0];
      }
      if (filter.field == "tags") {
        this.filters.tags.value = [];
        for (const defId of filter.args) {
          const def = this.defMap[defId];
          if (null != def) {
            this.filters.tags.value.push(def);
          }
        }
      }
    });
  }

  @Emit()
  private input() {
    const filters: Filter[] = [];
    if (this.notEmpty(this.filters.content.value)) {
      filters.push({
        field: "content",
        operator: "contains",
        args: [(this.filters.content.value! as string).trim()]
      });
    }
    if (
      this.filters.answerMethod.value != null &&
      this.notEmpty(this.filters.answerMethod.value.code)
    ) {
      filters.push({
        field: "answerMethod",
        operator: "=",
        args: [(this.filters.answerMethod.value.code! as string).trim()]
      });
    }
    if (
      this.filters.tags.value != null &&
      this.filters.tags.value.length != 0
    ) {
      const tagFilter = {
        field: "tags",
        operator: "in",
        args: [] as string[]
      };
      for (const def of this.filters.tags.value) {
        tagFilter.args.push(def.id!);
      }
      filters.push(tagFilter);
    }
    if (this.notEmpty(this.filters.id.value)) {
      filters.push({
        field: "id",
        operator: "eq",
        args: [this.filters.id.value]
      });
    }
    return filters;
  }
}
</script>