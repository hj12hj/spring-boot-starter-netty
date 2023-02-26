package com.hj.core;


import io.netty.channel.Channel;

/**
 * 客户端连接回调
 */
public interface OnClientConnected {
    /**
     * 客户端连接回调
     *
     * @param channel
     * @throws InterruptedException
     */
    void onConnected(Channel channel) throws InterruptedException;
}
