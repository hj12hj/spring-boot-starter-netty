package com.hj.server;

import com.hj.core.RemoteBootStrap;
import com.hj.handle.MessageCodec;
import com.hj.handle.ProcotolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBootStrap implements RemoteBootStrap {

    private Logger logger = LoggerFactory.getLogger(ServerBootStrap.class);

    private NioEventLoopGroup boss = new NioEventLoopGroup();

    private NioEventLoopGroup worker = new NioEventLoopGroup();

    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private ChannelHandler[] chanalHandlers;

    private Channel channel;

    private int port;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelHandler[] getChanalHandlers() {
        return chanalHandlers;
    }

    public void setChanalHandlers(ChannelHandler... chanalHandlers) {
        this.chanalHandlers = chanalHandlers;
    }

    @Override
    public void start() {

        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(new MessageCodec());
                // 5秒没有读取到数据，就关闭连接
                ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                ch.pipeline().addLast(new ChannelDuplexHandler() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        IdleStateEvent event = (IdleStateEvent) evt;
                        if (event.state() == IdleState.READER_IDLE) {
                            ctx.channel().close();
                        }
                    }
                });

                for (ChannelHandler channelHandler : chanalHandlers) {
                    ch.pipeline().addLast(channelHandler);
                }
            }
        });

        try {
            ChannelFuture future = serverBootstrap.bind(port);
            future.addListener(future1 -> {
                if (future1.isSuccess()) {
                    channel = future.channel();
                    logger.info("Server started success");
                } else {
                    logger.info("Server started failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void stop() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }


}
