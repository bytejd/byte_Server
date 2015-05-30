package com.bytes.net.server;

import com.bytes.net.proto.ByteNetProto.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @author zhangxiaojie
 *         2015-05-28 20:49
 *         功能介绍:初始化handler
 */
public class ByteRpcServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        channelPipeline.addLast("protobufDecoder",new ProtobufDecoder(RpcRequest.getDefaultInstance()));
        //encoder
        channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        channelPipeline.addLast("protobufEncoder", new ProtobufEncoder());

        channelPipeline.addLast("serverHandler",new RequestHandler());


    }
}
