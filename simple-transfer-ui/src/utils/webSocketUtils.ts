export function createWebSocket(url: string, onmessage: any): WebSocket {
    // 实例化socket
    const socket = new WebSocket(url);
    // 监听socket连接
    socket.onopen = open;
    // 监听socket错误信息
    socket.onerror = error;
    // 监听socket消息
    socket.onmessage = onmessage || getMessage;
    // 关闭socket连接
    socket.onclose = close;

    return socket;
}

const open = () => {
    console.log("socket连接成功");
};
const error = () => {
    console.log("socket连接错误");
};
const getMessage = (messageEvent: MessageEvent) => {
    console.log('接收到消息', messageEvent.data);
};

const close = () => {
    console.log("socket已经关闭");
};

export function getChatUser(ip: string, store: any): any {
    for (let i = 0; i < store.chatUserList.length; i++) {
        const chatUser = store.chatUserList[i];
        if (chatUser.ip === ip) {
            return chatUser;
        }
    }
}