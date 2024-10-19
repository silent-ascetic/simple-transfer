import { defineStore } from 'pinia'

// 第一个参数是应用程序中 store 的唯一 id
export const webSocketStore = defineStore('webSocketStore', {
  // other options...
  state: () => {
    return {
      // 所有这些属性都将自动推断其类型
      chatWebSocket: null,
    }
  },
})