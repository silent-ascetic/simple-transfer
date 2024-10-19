package com.passer.simpletransfer.controller;

import com.passer.simpletransfer.service.ChatService;
import com.passer.simpletransfer.vo.ResponseVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/getUserList")
    public ResponseVo getUserList(HttpServletRequest request) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        try {
            responseVo.setData(chatService.getUserList(request.getRemoteHost()));
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg("系统错误");
            log.error(e.getMessage());
        }
        return responseVo;
    }
}
