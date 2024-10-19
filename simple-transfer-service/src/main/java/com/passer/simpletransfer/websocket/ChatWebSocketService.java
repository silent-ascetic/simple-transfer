package com.passer.simpletransfer.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.passer.simpletransfer.config.STWebSocketConfigurer;
import com.passer.simpletransfer.vo.ChatUserVo;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/chat/{token}",configurator = STWebSocketConfigurer.class)
public class ChatWebSocketService extends BaseWebSocketServer{

    @OnOpen
    @Override
    public void onOpen(Session session, @PathParam("token") String token) {
        Map<String, Object> userProperties = session.getUserProperties();
        String ip = (String) userProperties.get(STWebSocketConfigurer.IP_ADDR);
        super.onOpen(session, ip);
        sendUserList(ip);
    }

    @Override
    @OnClose
    public void onClose() {
        super.onClose();
        sendUserList("");
    }

    @OnMessage
    @Override
    public void onMessage(String message, Session session) {
        Map<String, Object> userProperties = session.getUserProperties();
        String ip = (String) userProperties.get(STWebSocketConfigurer.IP_ADDR);
        String[] messageArray = message.split("#@#");
        String msg = ip + "#@#" + messageArray[0] + "#@#" + messageArray[1];
        if ("all".equals(messageArray[0])) {
            sendAllMessage(msg);
        } else {
            sendMessage(messageArray[0], msg);
        }
    }

    private void sendUserList(String ip) {
        List<ChatUserVo> chatUserVos = getChatUserVos(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "userList#@#";
        for (Map.Entry<String, BaseWebSocketServer> entry : webSocketMap.entrySet()) {
            String key = entry.getKey();
            if (entry.getKey().equals(ip)) {
                continue;
            }
            try {
                jsonString += objectMapper.writeValueAsString(chatUserVos.stream().filter(chatUserVo -> !key.equals(chatUserVo.getIp())).collect(Collectors.toList()));
            } catch (JsonProcessingException e) {
                jsonString += "[{ \"ip\": \"all\", \"userName\": \"大群\", \"content\": 0 }]";
                log.error("【ChatWebSocketService】连接用户信息数组转JSON失败：{}", e.getMessage());
            }
            sendMessage(jsonString, entry.getValue());
        }
    }

    public static List<ChatUserVo> getChatUserVos(String selfIp) {
        List<ChatUserVo> chatUserVos = new ArrayList<>();
        webSocketMap.forEachKey(2, key -> {
            if (!key.equals(selfIp)) {
                ChatUserVo chatUserVo = new ChatUserVo();
                chatUserVo.setIp(key);
                chatUserVo.setUserName(key);
                chatUserVos.add(chatUserVo);
            }
        });
        ChatUserVo chatUserVo = new ChatUserVo();
        chatUserVo.setIp("all");
        chatUserVo.setUserName("大群");
        chatUserVos.add(chatUserVo);
        return chatUserVos;
    }

}
