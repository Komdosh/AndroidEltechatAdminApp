package com.komdosh.eltechat.chat;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by komdosh on 14.02.17.
 */

@Getter
@Setter
public class Message {

    private Long id;
    private String fromName;
    private String message;
    private boolean isSelf;
    private boolean isServer;

    public Message(Long id, String fromName, String message, boolean isSelf) {
        this.id = id;
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
        this.isServer = false;
    }

    public Message(Long id, String fromName, String message, boolean isSelf, boolean isServer) {
        this.id = id;
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
        this.isServer = isServer;
    }
}