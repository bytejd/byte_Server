package com.bytes.net.client;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:07
 *         功能介绍:
 */
public class RPCClient {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //all to connect servers
    private Set<InetSocketAddress> allToConnectServers = Sets.newConcurrentHashSet();

    //每个主机一个连接
    private Map<InetSocketAddress,RpcHandler> connectedSocketHandlerMap = Maps.newConcurrentMap();

    private EventLoopGroup eventLoopGroup;

    private ReentrantLock lock = new ReentrantLock();
    private Condition connectedCondition = lock.newCondition();//是否连接成功




    /**
     *
     * @param servers tcp://192.168.6.238:1222,tcp://192.168.6.238:1221 or zns://zoo1:1221/appName/
     */
    public RPCClient(String servers){
        if(StringUtils.isBlank(servers)){
            throw new IllegalArgumentException("servers should not be null or empty");
        }
        List<String> serverList = Splitter.on(",").splitToList(servers);
        if(serverList.size() > 1){
            for(String server : serverList){
                if(!server.startsWith("tcp://")){
                    throw  new IllegalArgumentException("bad server format");
                }
                List<String> hostPortPair = Splitter.on(":").splitToList(server.substring(6));
                if(hostPortPair.size() != 2){
                    throw  new IllegalArgumentException("bad server format");
                }
                allToConnectServers.add(new InetSocketAddress(hostPortPair.get(0),Integer.parseInt(hostPortPair.get(1))));
            }
        }else {
            String server = serverList.get(0);
            if (server.startsWith("tcp://")) {
                List<String> hostPortPair = Splitter.on(":").splitToList(server.substring(6));
                if (hostPortPair.size() != 2) {
                    throw new IllegalArgumentException("bad server format");
                }
                allToConnectServers.add(new InetSocketAddress(hostPortPair.get(0), Integer.parseInt(hostPortPair.get(1))));
            } else if (server.startsWith("zns://")) {
                //TODO for service discovery
                throw new UnsupportedOperationException("It is not supported now");
            } else {
                throw new IllegalArgumentException("bad server format");
            }
        }

        eventLoopGroup = new NioEventLoopGroup(2);

        for(InetSocketAddress inetSocketAddress : allToConnectServers){
            connect(inetSocketAddress,0);
        }
    }

   private void reconnect(final Channel failedChannel,final InetSocketAddress remoteAddress,long delay){
       connect(remoteAddress,delay);
   }



    private void connect(final InetSocketAddress remoteAddress,long delay) {
        this.eventLoopGroup.schedule(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new RPCClientInitializer());
                    // Make the connection attempt.
                    ChannelFuture channelFuture = b.connect(remoteAddress);
                    channelFuture.addListener(new ChannelFutureListener(){
                        @Override
                        public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                            if(!channelFuture.isSuccess()){
                                logger.info("Can't connect to remote server. remote peer=" + remoteAddress.toString());
                         //       reconnect(channelFuture.channel(), remotePeer );
                            }else{
                                logger.info("Successfully connect to remote server. |remote peer=" + remoteAddress.toString());
                                successConnect(remoteAddress, channelFuture);
//                                DefaultClientHandler handler = channelFuture.channel().pipeline().get(DefaultClientHandler.class);
//                                addHandler(handler);
                            }
                        }
                    });

                } catch (Exception e){

                }finally {
                    // Shut down executor threads to exit.
                    group.shutdownGracefully();
                }
            }
        },delay, TimeUnit.MILLISECONDS);
    }

    private void successConnect(final InetSocketAddress remoteAddress,final ChannelFuture channelFuture){
        RpcHandler rpcHandler =   channelFuture.channel().pipeline().get(RpcHandler.class);
        if(rpcHandler != null){
            this.connectedSocketHandlerMap.put(remoteAddress,rpcHandler);
        }
        lock.lock();
        try{
            connectedCondition.signalAll();
        }finally {
            lock.unlock();
        }
    }


    public byte[] syncCall(String methodName,byte[] requestData) throws Exception {



        // Configure the client.
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new RPCClientInitializer());
//            // Make the connection attempt.
//            Channel ch = b.connect(remoteHost, port).sync().channel();
//            ByteNetProto.RpcRequest rpcRequest = ByteNetProto.RpcRequest.newBuilder().setMethod(methodName).setRequestData(ByteString.copyFrom(requestData)).build();
//            ch.writeAndFlush(rpcRequest);
//            return ResponseHandler.syncGetResult();
//        } catch (InterruptedException e) {
//            throw new Exception("error when syncCallRemote");
//        } finally {
//            // Shut down executor threads to exit.
//            group.shutdownGracefully();
//        }
        return null;
    }
}
