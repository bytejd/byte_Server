package com.bytes.net.client;

import com.bytes.net.proto.ByteNetProto.*;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:24
 *         功能介绍:
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Long, RpcFuture> pendingRPC = Maps.newConcurrentMap();

    private volatile Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        RpcFuture rpcFuture = pendingRPC.get(rpcResponse.getSeqNum());
        if(rpcFuture != null){
            rpcFuture.done(rpcResponse);
            pendingRPC.remove(rpcResponse.getSeqNum());
        }
    }

    public RpcFuture doRPC(RpcContext rpcContext){
        RpcFuture rpcFuture = new RpcFuture(rpcContext, this);
        pendingRPC.put(rpcContext.getRpcRequest().getSeqNum(), rpcFuture);
        channel.writeAndFlush(rpcContext.getRpcRequest());
        return rpcFuture;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel registered");
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }


}
