package com.passer.simpletransfer.service;

import com.passer.simpletransfer.vo.ChatUserVo;

import java.util.List;

public interface ChatService {
    List<ChatUserVo> getUserList(String remoteHost);
}
