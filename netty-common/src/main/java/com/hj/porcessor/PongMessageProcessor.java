package com.hj.porcessor;

import com.hj.core.NettyContext;
import com.hj.message.Message;
import com.hj.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;


public class PongMessageProcessor implements MessageProcessor {
    @Override
    public void processMessage(Message msg, ChannelHandlerContext context, NettyContext nettyContext) {
    }

    @Override
    public boolean support(Message message) {
        return message instanceof PongMessage;
    }
}
