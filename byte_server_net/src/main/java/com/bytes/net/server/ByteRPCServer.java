package com.bytes.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangxiaojie
 *         2015-05-28 20:21
 *         功能介绍:rpc server point
 */
public class ByteRPCServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String host;
    private int port;
    private int bossThreadNum = 1;//netty boss thread Num
    private int workerThreadNum = 4;//netty worker thread Num

    ByteRPCServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ByteRPCServer(int port, int bossThreadNum, int workerThreadNum) {
        this.host = "127.0.0.1";
        this.port = port;
        this.bossThreadNum = bossThreadNum;
        this.workerThreadNum = workerThreadNum;
    }

    public ByteRPCServer(String host, int port, int bossThreadNum, int workerThreadNum) {
        this.host = host;
        this.port = port;
        this.bossThreadNum = bossThreadNum;
        this.workerThreadNum = workerThreadNum;
    }

    public void start() throws Exception {
        validate();
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadNum);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadNum);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ByteRpcServerInitializer());
            Channel ch = b.bind(this.port).sync().channel();
            logger.info("init server success host[{}] port[{}]",host,port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new Exception("error when init server");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void validate() {
        if(StringUtils.isBlank(host) || port<=0){
            throw new IllegalArgumentException("you must init host and port first");
        }
    }


}
