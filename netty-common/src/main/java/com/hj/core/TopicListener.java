package com.hj.core;

/**
 * 主题监听器 客户端主题接受消息
 */
public interface TopicListener {
    /**
     * 订阅消息回调
     *
     * @param topic
     * @param message
     */
    void onMessage(String topic, String message);
}
