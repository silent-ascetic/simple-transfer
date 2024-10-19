package com.passer.simpletransfer.websocket;

import com.passer.simpletransfer.utils.StringUtils;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BaseWebSocketServer {
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    @Getter
    protected static final ConcurrentHashMap<String, BaseWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 客户端的连接会话
     */
    protected Session session;

    protected String userToken;


    /**
     * 链接成功调用的方法
     * @param session 客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        if (StringUtils.isEmpty(token)) {
            log.error("【{}】未获取到登录用户token", getClass().getSimpleName());
            return;
        }
        this.session = session;
        this.userToken = token;
        webSocketMap.remove(token);
        webSocketMap.put(token, this);
        log.info("【{}】有新的连接", getClass().getSimpleName());
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            this.session.close();
        } catch (IOException e) {
            log.error("【{}】Session 关闭失败：{}", getClass().getSimpleName(), e.getMessage());
        }
        webSocketMap.remove(this.userToken);
        log.info("【{}】连接关闭", getClass().getSimpleName());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("【{}】收到客户端消息：{}", getClass().getSimpleName(), message);
    }

    /**
     * 发送错误时的处理
     *
     * @param session Session
     * @param error 异常对象
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【{}】消息发送错误,原因：{}", getClass().getSimpleName(), error.getMessage());
    }


    /**
     * 此为广播消息
     */
    public static void sendAllMessage(String message) {
        for (BaseWebSocketServer baseWebSocketServer : webSocketMap.values()) {
            sendMessage(message, baseWebSocketServer);
        }
    }

    public static void sendMessage(String userToken, String message) {
        BaseWebSocketServer baseWebSocketServer = webSocketMap.get(userToken);
        if (baseWebSocketServer == null) {
            log.error("【{}】消息发送失败：websocket连接已关闭", BaseWebSocketServer.class.getSimpleName());
        }
        sendMessage(message, baseWebSocketServer);
    }

    public static void sendMessage(String message, BaseWebSocketServer baseWebSocketServer) {
        if (baseWebSocketServer != null && baseWebSocketServer.session.isOpen()) {
            boolean success = false;
            while (!success) {
                try {
                    baseWebSocketServer.session.getAsyncRemote().sendText(message);
                    success = true;
                } catch (RuntimeException e) {
                    log.error("【{}】消息发送失败：[{}] {}", BaseWebSocketServer.class.getSimpleName(), e.getClass().getName(), e.getMessage());
                    // 延时 200ms 后再发送
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }
}
