<template>
  <div>
    <div ref="editorElement" :style="editorStyle"></div>
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import Component from "vue-class-component";
import { Prop } from "vue-property-decorator";
import Quill from "quill";

@Component
export default class FormulaEditor extends Vue {
  $refs!: { editorElement: Element; toolbarElement: Element };

  @Prop()
  value!: string;
  get _value(): string {
    return this._value;
  }
  set _value(value: string) {
    const newValue = value;
    const oldValue = this.value;
    this.value = value;
    if (newValue !== oldValue && this.quill && !this.quill.hasFocus()) {
      this.renderValue(newValue);
    }
  }

  @Prop()
  editorStyle!: string;
  quill: Quill | null = null;
  @Prop({
    default: false
  })
  readOnly!: boolean;
  @Prop()
  formats!: [string];
  @Prop({
    default: function() {
      return [
        ["formula"],
        ["bold", "italic", "underline", "strike"], // toggled buttons
        ["blockquote", "code-block"],
        [{ header: 1 }, { header: 2 }], // custom button values
        [{ list: "ordered" }, { list: "bullet" }],
        [{ script: "sub" }, { script: "super" }], // superscript/subscript
        [{ indent: "-1" }, { indent: "+1" }], // outdent/indent
        [{ direction: "rtl" }], // text direction

        [{ size: ["small", false, "large", "huge"] }], // custom dropdown
        [{ header: [1, 2, 3, 4, 5, 6, false] }],

        [{ color: [] }, { background: [] }], // dropdown with defaults from theme
        [{ font: [] }],
        [{ align: [] }],
        ["image", "video"],
        ["clean"] // remove formatting button
      ];
    }
  })
  toolbar!: [];

  @Prop({ default: "snow" })
  theme!: string;

  @Prop({ default: "请输入内容" })
  placeholder!: string;

  set _placeholder(value: string) {
    this.placeholder = value;
  }

  get _placeholder() {
    return this.placeholder;
  }

  mounted() {
    this.quill = new Quill(this.$refs.editorElement, {
      modules: {
        toolbar: this.toolbar
      },
      placeholder: this.placeholder,
      theme: this.theme,
      readOnly: this.readOnly
    });

    this.renderValue(this.value);

    this.quill.on("text-change", (delta, oldContents, source) => {
      if (source === "user") {
        let html = this.$refs.editorElement.children[0].innerHTML;
        const text = this.quill!.getText().trim();
        if (html === "<p><br></p>") {
          html = "";
        }
        this.$emit("input", html);
        this.$emit("text-change", {
          htmlValue: html,
          textValue: text,
          delta: delta,
          source: source
        });
      }
    });
  }

  beforeDestroy() {
    this.quill = null;
  }
  renderValue(value: any) {
    if (this.quill) {
      if (value) this.quill.pasteHTML(value);
      else this.quill.setText("");
    }
  }
}
</script>

<style scoped>
@import "../../node_modules/quill/dist/quill.core.css";
@import "../../node_modules/quill/dist/quill.bubble.css";
@import "../../node_modules/katex/dist/katex.css";
</style>