<script setup lang="ts" name="Share">
import { uploadChunkFileApi, mergeFileApi, getQrCodeApi } from "@/api/share";
import getAssetsImgUrl from "@/utils/imageUtils";
import { closeToast, showLoadingToast } from 'vant';
import { ref, onMounted } from "vue";

const qrCode = ref('');

onMounted(() => {
  getQrCode();
})

const getQrCode = () => {
  getQrCodeApi().then(resp => {
    if (resp.respCode === 200) {
      qrCode.value = resp.data;
    }
  })
}

const afterRead = (file: any) => {
  uploadChunkFile(file)
}

const uploadChunkFile = (uploadFile) => {
  const toast = showLoadingToast({
    duration: 0,
    forbidClick: true,
    message: '上传中',
  });
  if (!uploadFile) {
    toast.message = '上传失败';
    const timer = setInterval(() => {
      clearInterval(timer);
      closeToast();
    }, 1000);
    return
  }

  const file = uploadFile.file

  uploadFile.status = 'uploading';
  toast.message = '分片计算中...';

  let chunkSize = 104857600 // 100MB
  let chunkNum = 1
  let chunkStart = 0
  let chunkEnd = (chunkStart + chunkSize) > file.size ? file.size : (chunkStart + chunkSize)
  let totalChunks = Math.ceil(file.size / chunkSize)
  let threadNum = totalChunks > 8 ? 8 : totalChunks
  let finishNum = 0
  let noError = true
  const blobSlice = File.prototype.slice;       //兼容方式获取slice方法

  console.log(`${file.name} 分片个数${totalChunks}`)

  let uploadChunk = () => {
    console.log(`${file.name} 第${chunkNum}个分片开始上传`)

    const chunk_file = blobSlice.call(file, chunkStart, chunkEnd);
    let formData = new FormData()
    formData.append('chunkFile', chunk_file)
    formData.append("fileName", file.name)
    formData.append("chunkNum", chunkNum + "")
    uploadChunkFileApi(formData)
      .then((resp) => {
        if (resp.respCode === 200) {
          finishNum = finishNum + 1
          let rate = Math.round(finishNum / totalChunks * 100)
          toast.message = `${rate}%`;

          if (finishNum === totalChunks) {
            console.log(`合并文件 ${file.name}`)
            toast.message = '合并中...';

            mergeFile(uploadFile, totalChunks, toast)
          }
        }
        if (resp.respCode === 500) {
          noError = false;
          toast.message = resp.msg;
          const timer = setInterval(() => {
            clearInterval(timer);
            closeToast();
          }, 1000);
        }
      })
  }

  toast.message = '0%';

  while (chunkNum <= threadNum && noError) {
    uploadChunk()
    chunkStart = chunkStart + chunkSize
    chunkEnd = chunkStart + chunkSize
    chunkEnd = chunkEnd > file.size ? file.size : chunkEnd
    chunkNum = chunkNum + 1
  }

  let preFinishNum = finishNum
  if (totalChunks > 8) {
    // 分片个数大于8，监听之前的8个请求，完成多少个就新建多少个请求
    let interval = setInterval(() => {
      // 较上次完成请求个数
      const num = finishNum - preFinishNum
      // 更新上次监听时请求完成个数
      preFinishNum = finishNum
      if (chunkNum > totalChunks) {
        clearInterval(interval)
      } else if (chunkNum <= totalChunks && num > 0) {
        for (let i = 0; i < num; i++) {
          uploadChunk()
          chunkStart = chunkStart + chunkSize
          chunkEnd = chunkStart + chunkSize
          chunkEnd = chunkEnd > file.size ? file.size : chunkEnd
          chunkNum = chunkNum + 1
        }
      }
    }, 1)
  }

}


function mergeFile(uploadFile: any, totalChunks: number, toast: any) {
  const data = {
    'fileName': uploadFile.file.name,
    'totalChunks': totalChunks
  }
  mergeFileApi(data)
    .then((resp) => {
      toast.message = resp.msg;
      const timer = setInterval(() => {
        clearInterval(timer);
        closeToast();
      }, 1000);
    })
}

</script>

<template>
  <div class="share-content mt-[12px] px-[12px]">
    <van-row justify="space-around" align="center">
      <van-col span="6">
        <div v-html="qrCode"></div>
        <p class="col-text">移动端扫码访问</p>
      </van-col>
      <van-col span="6">
        <van-uploader accept="*" :after-read="afterRead" upload-icon="plus" result-type="file">
          <van-image width="100%" height="100%" fit="contain"
            :src="getAssetsImgUrl('upload.png')" />
        </van-uploader>
        <p class="col-text">点击选择或拖到此处</p>
      </van-col>
    </van-row>
  </div>
</template>

<style lang="css" scoped>
svg {
  width: 100%;
  height: 100%;
}

.col-text {
  margin-top: 12px;
  text-align: center;
}
</style>