package com.hj.message;

import lombok.Data;

@Data
public class PingMessage extends Message {
    String ip;


    @Override
    public String toString() {
        return "PingMessage{" +
                "ip='" + ip + '\'' +
                "} " + super.toString();
    }
}