<script setup lang="ts" name="Chat">
import { getUserListApi } from "@/api/chat";
import { chatInfoStore } from "@/store/modules/chatInfo";
import { ref } from "vue";
import { useRouter } from "vue-router";


const chatStore = chatInfoStore();
const loading = ref(false);
const refreshing = ref(false);
const finished = ref(false);

const router = useRouter();

const onRefresh = () => {
  // 清空列表数据
  finished.value = false;

  // 重新加载数据
  // 将 loading 设置为 true，表示处于加载状态
  loading.value = true;
  chatStore.chatUserList = [];
  onLoad();
  refreshing.value = false;
};
const onLoad = () => {
  getUserListApi().then((resp) => {
    chatStore.chatUserList = resp.data;
    loading.value = false;
    finished.value = true;
  })
};

const toChatPage = (chatUser: any) => {
    router.push(`/chatPage/${chatUser.ip}/${chatUser.userName}`)
};

</script>

<template>
  <div class="chat-content px-[12px]">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功" :head-height="65">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
        <van-cell v-for="item in chatStore.chatUserList" is-link @click="toChatPage(item)">
          <template #title>
            <van-badge :content="item.content" max="9" :show-zero="false">
              <div class="custom-title p-[4px]">
                <span>{{ item.userName }}</span>
              </div>
            </van-badge>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <van-back-top />
  </div>

</template>

<style lang="css" scoped>

</style>