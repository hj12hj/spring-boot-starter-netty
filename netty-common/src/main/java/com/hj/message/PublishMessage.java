package com.hj.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishMessage extends Message{
    String topic;
    String content;

    @Override
    public String toString() {
        return "PublishMessage{" +
                "topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
