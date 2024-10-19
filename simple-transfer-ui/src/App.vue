<script setup lang="ts" name="App">
import { webSocketStore } from "@/store/modules/webSocket";
import { createWebSocket } from "@/utils/webSocketUtils";
import { onBeforeMount, ref } from "vue";
import { chatInfoStore } from "./store/modules/chatInfo";

const socketStore = webSocketStore();
const chatStore = chatInfoStore();
const webSocketUrl = ref(`ws://${location.host}${import.meta.env.VITE_BASE_API}/websocket/chat/${location.hostname}`);


onBeforeMount(() => {
  init();
});

const init = () => {
  if (typeof WebSocket === "undefined") {
    console.log("您的浏览器不支持socket");
  } else if (!socketStore.chatWebSocket) {
    socketStore.chatWebSocket = createWebSocket(webSocketUrl.value, getMessage);
  }
};

const getMessage = (messageEvent) => {
  console.log('接受消息');
  let msgArr = messageEvent.data.split('#@#');
  if ('userList' === msgArr[0]) {
    chatStore.chatUserList = JSON.parse(msgArr[1]);
    return;
  }
  let friendIP = 'all' === msgArr[1] ? 'all' : msgArr[0];
  let msgObj = { ip: msgArr[0], contentText: msgArr[2] };
  if (!chatStore.chatContentMap[friendIP]) {
    chatStore.chatContentMap[friendIP] = [];
  }
  chatStore.chatContentMap[friendIP].push(msgObj);
  for (let i = 0; i < chatStore.chatUserList.length; i++) {
    const chatUser = chatStore.chatUserList[i];
    if (chatUser.ip === friendIP) {
      chatUser.content = chatUser.content + 1;
      break;
    }
  }
};
</script>

<template>
  <router-view />
</template>

<style></style>
