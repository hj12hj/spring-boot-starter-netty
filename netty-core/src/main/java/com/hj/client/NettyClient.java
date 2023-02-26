package com.hj.client;

import com.hj.core.NettyContext;
import com.hj.core.OnClientConnected;
import com.hj.core.TopicListener;
import com.hj.message.Message;
import com.hj.message.PublishMessage;
import com.hj.porcessor.MessageProcessor;
import com.hj.porcessor.ProcessorCore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class NettyClient {

    private ClientBootStrap clientBootStrap = new ClientBootStrap();

    private TopicListener topListener;

    private String clientId;

    private OnClientConnected onClientConnected;

    private String host;

    private int port;

    private List<MessageProcessor> messageProcessors;

    public List<MessageProcessor> getMessageProcessors() {
        return messageProcessors;
    }

    public void setMessageProcessors(List<MessageProcessor> messageProcessors) {
        this.messageProcessors = messageProcessors;
    }


    public OnClientConnected getOnClientConnected() {
        return onClientConnected;
    }

    public void setOnClientConnected(OnClientConnected onClientConnected) {
        this.onClientConnected = onClientConnected;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public TopicListener getTopListener() {
        return topListener;
    }

    public void setTopListener(TopicListener topListener) {
        this.topListener = topListener;
    }


    public Channel getChannel() {
        return clientBootStrap.getChannel();
    }

    public void init() {
        clientBootStrap.setHost(host);
        clientBootStrap.setPort(port);
        clientBootStrap.setOnClientConnected(onClientConnected);
        clientBootStrap.setChanalHandlers(new ClientHandle());
        clientBootStrap.setClientName(clientId);
        clientBootStrap.start();
    }


    public void publish(String topic, String message) {
        PublishMessage publishMessage = new PublishMessage(topic, message);
        clientBootStrap.getChannel().writeAndFlush(publishMessage);
    }


    @ChannelHandler.Sharable
    class ClientHandle extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

            NettyContext nettyContext = new NettyContext();
            nettyContext.setTopListener(topListener);
            messageProcessors.stream().forEach(processor -> {
                if (processor.support(message)) {
                    processor.processMessage(message, channelHandlerContext, nettyContext);
                }
            });

        }
    }


}
