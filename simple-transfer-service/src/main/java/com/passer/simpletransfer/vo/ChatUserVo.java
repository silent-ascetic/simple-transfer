package com.passer.simpletransfer.vo;

import lombok.Data;

@Data
public class ChatUserVo {

    private String ip;

    private String userName;

    private int content;

    public String toJsonString() {
        return "{" +
                "\"ip\": \"" + ip + '\"' +
                ", \"userName\": \"" + userName + '\"' +
                ", \"content\": " + content +
                '}';
    }
}
