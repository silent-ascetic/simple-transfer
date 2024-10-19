<script setup lang="ts">

const props = defineProps({
  navBarTitle: String
})


const onClickLeft = () => {
  history.back();
}
</script>

<template>
  <van-nav-bar fixed placeholder :title="navBarTitle" :left-arrow="true" @click-left="onClickLeft" />
</template>

<style scoped></style>
