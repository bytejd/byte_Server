package com.bytes.net.client;

import com.bytes.net.proto.ByteNetProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:24
 *         功能介绍:
 */
public class RpcHandler extends SimpleChannelInboundHandler<ByteNetProto.RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteNetProto.RpcResponse msg) throws Exception {

    }


}
