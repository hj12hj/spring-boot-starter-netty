package com.hj.porcessor;

import com.hj.core.NettyContext;
import com.hj.message.Message;
import io.netty.channel.ChannelHandlerContext;

public interface MessageProcessor {


    void processMessage(Message message, ChannelHandlerContext context, NettyContext nettyContext);

    boolean support(Message message);

}
