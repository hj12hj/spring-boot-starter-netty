package com.hj.client;

import com.hj.core.OnClientConnected;
import com.hj.core.RemoteBootStrap;
import com.hj.handle.MessageCodec;
import com.hj.handle.ProcotolFrameDecoder;
import com.hj.message.PingMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;

public class ClientBootStrap implements RemoteBootStrap {

    Logger logger = org.slf4j.LoggerFactory.getLogger(ClientBootStrap.class);

    private Bootstrap bootstrap;

    private NioEventLoopGroup group;

    private ChannelHandler[] chanalHandlers;

    private String clientName;

    private String host;

    private int port;

    private Channel channel;

    private OnClientConnected onClientConnected;


    public OnClientConnected getOnClientConnected() {
        return onClientConnected;
    }

    public void setOnClientConnected(OnClientConnected onClientConnected) {
        this.onClientConnected = onClientConnected;
    }


    public ChannelHandler[] getChanalHandlers() {
        return chanalHandlers;
    }

    public void setChanalHandlers(ChannelHandler... chanalHandlers) {
        this.chanalHandlers = chanalHandlers;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void start() {

        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(new MessageCodec());
                // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                // ChannelDuplexHandler 可以同时作为入站和出站处理器
                ch.pipeline().addLast(new ChannelDuplexHandler() {
                    int index = 0;

                    // 用来触发特殊事件
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        IdleStateEvent event = (IdleStateEvent) evt;
                        // 触发了写空闲事件
                        if (event.state() == IdleState.WRITER_IDLE) {
//                            log.debug("3s 没有写数据了，发送一个心跳包");
                            PingMessage pingMessage = new PingMessage();
                            pingMessage.setSequenceId(index++);
                            ctx.writeAndFlush(pingMessage);
                        }
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        // 重连
                        start();
                        logger.debug("channelInactive");
                    }
                });

                for (ChannelHandler chanalHandler : chanalHandlers) {
                    ch.pipeline().addLast(chanalHandler);
                }

            }
        });
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("client connect success");
                channel = channelFuture.channel();
                if (onClientConnected != null) {
                    onClientConnected.onConnected(channel);
                }
            } else {
                Thread.sleep(2000);
                start();
                logger.debug("client connect fail");
            }
        });

    }

    @Override
    public void stop() {

        channel.close();
        group.shutdownGracefully();

    }
}
