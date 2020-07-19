<template>
  <div class="about">
    <FormulaEditor v-model="value" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import FormulaEditor from "../components/FormulaEditor.vue";
export default Vue.extend({
  components: { FormulaEditor },
  data() {
    return {
      value: null
    };
  }
});
</script>