import { defineStore } from 'pinia'

// 第一个参数是应用程序中 store 的唯一 id
export const chatInfoStore = defineStore('chatInfoStore', {
  // other options...
  state: () => {
    return {
      // 所有这些属性都将自动推断其类型
      // { 'all': [{ ip: '192.168.11.1', contentText: '是大' }, { ip: '192.168.11.3', contentText: 'dsfsf' }] }
      chatContentMap: {},
      chatUserList: []
    }
  },
  persist: true,
})