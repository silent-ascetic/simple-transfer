<script setup lang="ts" name="ChatPage">
import { chatInfoStore } from "@/store/modules/chatInfo";
import { webSocketStore } from "@/store/modules/webSocket";
import { getChatUser } from "@/utils/webSocketUtils";
import { onBeforeMount, ref } from "vue";
import { useRoute } from "vue-router";

const socketStore = webSocketStore();
const chatStore = chatInfoStore();
const route = useRoute();
const friendIP = ref(route.params.ip as string);
const friendName = ref(route.params.userName as string);
const content = ref('');
const chatContainer = ref(null);


onBeforeMount(() => {  
  const chatUser = getChatUser(friendIP.value, chatStore)
  if (chatUser) {
    chatUser.content = 0;
  }
});


const send = () => {
  console.log('发送消息');
  let message = `${friendIP.value}#@#${content.value}`;
  socketStore.chatWebSocket.send(message);
  if (friendIP.value !== 'all') {
    let msgObj = { ip: '127.0.0.1', contentText: content.value };
    if (!chatStore.chatContentMap[friendIP.value]) {
      chatStore.chatContentMap[friendIP.value] = [];
    }
    chatStore.chatContentMap[friendIP.value].push(msgObj);
  }
  content.value = '';
};




</script>

<template>
  <div class="chat-page-box" ref="chatContainer" style="background-color: #f7f7f8; height: 100%;">
    <chat-nav-bar :nav-bar-title="friendName" />
    <div class="chat-page-content">
      <van-row v-for="chatContent in chatStore.chatContentMap[friendIP]"
        :justify="chatContent.ip === friendIP ? 'end' : 'start'">
        <van-col span="6" class="chat-content-box mt-[6px] mb-[6px]">
          <p class="ip-text">{{ chatContent.ip }}</p>
          <div class="ip-chat-content px-[6px]">
            {{ chatContent.contentText }}
          </div>
        </van-col>
      </van-row>
    </div>
    <van-cell-group class="chat-page-input-box">
      <van-field v-model="content" autosize type="textarea" rows="1" center left-icon="edit">
        <template #button>
          <van-button size="small" type="primary" @click="send">发送</van-button>
        </template>
      </van-field>
    </van-cell-group>
  </div>
</template>

<style lang="css" scoped>
.chat-page-box {
  display: flex;
  flex-direction: column;
}

.chat-page-content {
  background-color: #f7f7f8;
  /* height: 100%; */
  /* 设置一个最大高度 */
  overflow-y: auto;
  /* 当内容超过最大高度时，垂直滚动 */
}

.ip-text {
  font-size: smaller;
}

.ip-chat-content {
  background-color: white;
  border-radius: 6px;
  word-wrap: break-word;
  /* 允许在单词内部换行 */
}

.chat-page-input-box {
  margin-top:auto
}
</style>