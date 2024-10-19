package com.passer.simpletransfer.service.impl;

import com.passer.simpletransfer.service.ChatService;
import com.passer.simpletransfer.vo.ChatUserVo;
import com.passer.simpletransfer.websocket.ChatWebSocketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("chatService")
public class ChatServiceImpl implements ChatService {
    @Override
    public List<ChatUserVo> getUserList(String remoteHost) {
        return ChatWebSocketService.getChatUserVos(remoteHost);
    }
}
