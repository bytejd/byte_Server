package com.bytes.net.client;

import com.bytes.net.proto.ByteNetProto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author zhangxiaojie
 *         2015-06-01 15:22
 *         功能介绍:调用future
 */
public class RPCFuture implements Future<RpcResponse>{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Sync sync;
    private RpcHandler handler;
    private long startTime;
    private RpcContext rpcContext;
    private long responseTimeThreshold = 1000;//ms


    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;
        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {
            return getState()==done?true:false;
        }

        protected  boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, 1)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone(){
            getState();
            return getState()==done;
        }
    }

    public RPCFuture(RpcContext rpcContext,RpcHandler handler) {
        this.rpcContext = rpcContext;
        this.handler = handler;
        this.sync = new Sync();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }


    //wake up caller thread or summit task to excute async callback , will be called by event loop thread when received response from Server.
    public void done(RpcResponse reponse){
        this.rpcContext.setRpcResponse(reponse);
        sync.release(1);
        //Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if(responseTime > this.responseTimeThreshold){
            logger.warn("Service response time is too slow |responseTime=" + responseTime);
        }
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        return processResponse();
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, timeUnit.toNanos(timeout));
        if(success){
            return processResponse();
        }else{
            throw new RuntimeException("Timeout exception");
        }
    }


    private RpcResponse processResponse() {
        return this.rpcContext.getRpcResponse();
    }


}
