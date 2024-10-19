import Layout from "@/layout/index.vue";
import type { RouteRecordRaw } from "vue-router";
import Share from "@/views/share/index.vue";
import Close from "@/views/close/index.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "root",
    component: Layout,
    redirect: { name: "Share" },
    children: [
      {
        path: "share",
        name: "Share",
        component: Share,
        meta: {
          title: "分享"
        }
      },
      {
        path: "download",
        name: "Download",
        component: () => import("@/views/download/index.vue"),
        meta: {
          title: "下载",
          noCache: true
        }
      },
      {
        path: "chat",
        name: "Chat",
        component: () => import("@/views/chat/index.vue"),
        meta: {
          title: "聊天",
          noCache: true
        }
      }
    ]
  },
  {
    path: "/chatPage/:ip/:userName",
    name: "ChatPage",
    component: () => import("@/views/chat/chatPage.vue"),
    meta: {
      title: "聊天",
      noCache: true
    }
  },
  {
    path: "/close",
    name: "close",
    component: Close,
  }
];

export default routes;
