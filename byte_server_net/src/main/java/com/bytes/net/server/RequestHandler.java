package com.bytes.net.server;

import com.bytes.net.proto.ByteNetProto.*;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * @author zhangxiaojie
 *         2015-05-29 14:21
 *         功能介绍:
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        System.out.println(msg.getMethod());
        RpcResponse rpcResponse = RpcResponse.newBuilder().setResponseData(ByteString.copyFrom("中国你好".getBytes())).build();
        ctx.writeAndFlush(rpcResponse);
        ctx.close();
    }
}