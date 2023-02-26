package com.hj.core;


import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NettyContext {

    /**
     * 登录map
     */
    private Map<Channel, String> channelMap = new HashMap<>();

    /**
     * 订阅map
     */
    private Map<String, Set<Channel>> subScribeMap = new HashMap<>();

    /**
     * topic监听器
     */
    TopicListener topListener;


    public TopicListener getTopListener() {
        return topListener;
    }

    public void setTopListener(TopicListener topListener) {
        this.topListener = topListener;
    }

    public Map<Channel, String> getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Map<Channel, String> channelMap) {
        this.channelMap = channelMap;
    }

    public Map<String, Set<Channel>> getSubScribeMap() {
        return subScribeMap;
    }

    public void setSubScribeMap(Map<String, Set<Channel>> subScribeMap) {
        this.subScribeMap = subScribeMap;
    }

}
