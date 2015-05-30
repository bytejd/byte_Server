package com.bytes.net.client;

import com.bytes.net.proto.ByteNetProto;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:07
 *         功能介绍:
 */
public class RPCClient {
    private String remoteHost;
    private int port;

    public RPCClient(String remoteHost, int port) {
        this.remoteHost = remoteHost;
        this.port = port;
    }

    public byte[] syncCall(String methodName,byte[] requestData) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RPCClientInitializer());
            // Make the connection attempt.
            Channel ch = b.connect(remoteHost, port).sync().channel();
            ByteNetProto.RpcRequest rpcRequest = ByteNetProto.RpcRequest.newBuilder().setMethod(methodName).setRequestData(ByteString.copyFrom(requestData)).build();
            ch.writeAndFlush(rpcRequest);
            return ResponseHandler.syncGetResult();
        } catch (InterruptedException e) {
            throw new Exception("error when syncCallRemote");
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }
}
