package com.hj.server;

import com.hj.core.NettyContext;
import com.hj.message.Message;
import com.hj.message.PublishMessage;
import com.hj.message.RegisterMessage;
import com.hj.message.SubScribeMessage;
import com.hj.porcessor.MessageProcessor;
import com.hj.porcessor.ProcessorCore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.*;

public class NettyServer {

    private int port;

    private ServerBootStrap serverBootStrap = new ServerBootStrap();

    private Map<Channel, String> channelMap = new HashMap<>();

    private Map<String, Set<Channel>> subScribeMap = new HashMap<>();

    private List<MessageProcessor> messageProcessors;

    public List<MessageProcessor> getMessageProcessors() {
        return messageProcessors;
    }

    public void setMessageProcessors(List<MessageProcessor> messageProcessors) {
        this.messageProcessors = messageProcessors;
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

    public Channel getChannel() {
       return serverBootStrap.getChannel();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }



    public void init() {
        serverBootStrap.setChanalHandlers(new ServerHandler());
        serverBootStrap.setPort(port);
        serverBootStrap.start();
    }

    public void publish(String topic, String message) {
        PublishMessage publishMessage = new PublishMessage(topic, message);
        Set<Channel> channels = subScribeMap.get(topic);
        if (channels != null) {
            channels.forEach(channel -> {
                channel.writeAndFlush(publishMessage);
            });
        }
    }

    @ChannelHandler.Sharable
    class ServerHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
            if (message instanceof RegisterMessage || message instanceof SubScribeMessage || message instanceof PublishMessage) {
                //注册
                if (message instanceof RegisterMessage) {
                    if (!channelMap.containsKey(channelHandlerContext.channel())) {
                        channelMap.put(channelHandlerContext.channel(), ((RegisterMessage) message).getClientId());
                    }
                }

                if (message instanceof SubScribeMessage) {
                    //订阅添加
                    SubScribeMessage subScribeMessage = (SubScribeMessage) message;
                    String topic = subScribeMessage.getTopic();
                    Set<Channel> channels = subScribeMap.get(topic);
                    if (channels == null) {
                        channels = new HashSet<>();
                        channels.add(channelHandlerContext.channel());
                        subScribeMap.put(topic, channels);
                    } else {
                        channels.add(channelHandlerContext.channel());
                    }

                }

                if (message instanceof PublishMessage) {
                    //发布消息
                    PublishMessage publishMessage = (PublishMessage) message;
                    String topic = publishMessage.getTopic();
                    Set<Channel> channels = subScribeMap.get(topic);
                    if (channels != null) {
                        channels.forEach(channel -> {
                            channel.writeAndFlush(publishMessage);
                        });
                    }
                }

            } else {
                NettyContext nettyContext = new NettyContext();
                nettyContext.setChannelMap(channelMap);
                nettyContext.setSubScribeMap(subScribeMap);
                messageProcessors.stream().forEach(processor -> {
                    if (processor.support(message)) {
                        processor.processMessage(message, channelHandlerContext, nettyContext);
                    }
                });
            }
        }


        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            channelMap.remove(ctx.channel());
            subScribeMap.entrySet().forEach(entry -> {
                entry.getValue().remove(ctx.channel());
            });
        }
    }


}
