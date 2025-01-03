<script setup lang="ts" name="Download">
import { getList, deleteFileApi } from "@/api/download";
import { reactive, ref } from "vue";
import { showSuccessToast, showFailToast } from 'vant';


const list: any[] = reactive([]);
const loading = ref(false);
const refreshing = ref(false);
const finished = ref(false);
const noFile = ref(false);



const onRefresh = () => {
  console.log('下拉刷新');
  // 清空列表数据
  finished.value = false;

  // 重新加载数据
  // 将 loading 设置为 true，表示处于加载状态
  loading.value = true;
  list.length = 0;
  onLoad();
  refreshing.value = false;
};
const onLoad = () => {
  console.log('加载');
  getList().then((resp) => {
    list.push(...resp.data);
    loading.value = false;
    finished.value = true;
    noFile.value = list.length <= 0;
    console.log('加载完成');
  })
};

const downloadFile = (fileInfo) => {
  const a = document.createElement('a');
  a.href = fileInfo.downloadUrl;
  a.target = '_blank';
  a.download = fileInfo.name;
  a.click();
};

const deleteFile = (md5) => {
  const data = {
    fileMd5: md5
  };
  deleteFileApi(data).then(resp => {
    if (resp.respCode === 200) {
      showSuccessToast('删除成功');
      onRefresh();
    } else {
      showFailToast(resp.msg);
    }
  });
}

</script>

<template>

  <van-pull-refresh v-model="refreshing" @refresh="onRefresh" success-text="刷新成功" :head-height="65">
    <van-empty v-if="noFile" description="暂无文件" />

    <van-list v-if="!noFile" v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <van-row gutter="10" justify="space-between" align="center" v-for="item in list" :key="item.downloadUrl">
        <van-col span="20">
          <van-cell :title="item.name" :value="item.sizeStr" :clickable="true" @click="downloadFile(item)" />
        </van-col>
        <van-col span="4">
          <van-button icon="delete" round size="small" type="danger" @click="deleteFile(item.md5)" />
        </van-col>
      </van-row>
    </van-list>
  </van-pull-refresh>

  <van-back-top />
</template>
