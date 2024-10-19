<template>
  <van-tabbar v-model="active" :route="true" :placeholder="true" fixed>
    <van-tabbar-item
      v-for="(item, index) in tabbarData"
      :key="index"
      :icon="item.icon"
      :to="item.to"
    >
      {{ item.title }}
    </van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";

const active = ref(0);
const tabbarData = reactive([
  {
    icon: "share-o",
    title: "分享",
    to: {
      name: "Share"
    }
  },
  {
    icon: "down",
    title: "下载",
    to: {
      name: "Download"
    }
  },
  {
    icon: "chat-o",
    title: "聊天",
    to: {
      name: "Chat"
    }
  }
]);
</script>
