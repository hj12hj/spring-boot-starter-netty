package com.hj.porcessor;

import com.hj.core.NettyContext;
import com.hj.message.Message;
import com.hj.message.PublishMessage;
import io.netty.channel.ChannelHandlerContext;

public class PublishMessageProcessor implements MessageProcessor {


    @Override
    public void processMessage(Message message, ChannelHandlerContext context, NettyContext nettyContext) {
        PublishMessage publishMessage = (PublishMessage) message;
        nettyContext.getTopListener().onMessage(publishMessage.getTopic(), publishMessage.getContent());
    }

    @Override
    public boolean support(Message message) {
        return message instanceof PublishMessage;
    }
}
