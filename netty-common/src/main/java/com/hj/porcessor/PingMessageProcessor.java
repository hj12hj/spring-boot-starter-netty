package com.hj.porcessor;

import com.hj.core.NettyContext;
import com.hj.message.Message;
import com.hj.message.PingMessage;
import com.hj.message.PongMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

public class PingMessageProcessor implements MessageProcessor {
    Logger log = org.slf4j.LoggerFactory.getLogger(PingMessageProcessor.class);

    int index = 0;
    @Override
    public void processMessage(Message msg, ChannelHandlerContext context, NettyContext nettyContext) {
        log.info("收到客户端的心跳消息:{}", msg);
        PingMessage pingMessage = (PingMessage) msg;
        PongMessage pongMessage = new PongMessage();
        pongMessage.setSequenceId(index++);
        pongMessage.setSequenceId(msg.getSequenceId());
        context.writeAndFlush(pongMessage);
    }

    @Override
    public boolean support(Message message) {
        return message instanceof PingMessage;
    }
}
