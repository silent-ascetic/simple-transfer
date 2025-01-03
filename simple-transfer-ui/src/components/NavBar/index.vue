<script setup lang="ts">
import { closeApi } from "@/api/share";
import { useDarkMode, useToggleDarkMode } from "@/hooks/useToggleDarkMode";
import router from "@/router";
// package.json
import config from '../../../package.json'



const onClickLeft = () => {
  closeApi().then(resp => {
    router.push("/close");
  });
}

const onClickRight = (event: TouchEvent | MouseEvent) => {
  useToggleDarkMode(event);
};
</script>

<template>
  <van-nav-bar fixed placeholder :title="config.name" left-text="关闭" @click-left="onClickLeft"
    @click-right="onClickRight">
    <template #right>
      <svg-icon class="text-[18px]" :name="useDarkMode() ? 'light' : 'dark'" />
    </template>
  </van-nav-bar>
</template>

<style scoped></style>
