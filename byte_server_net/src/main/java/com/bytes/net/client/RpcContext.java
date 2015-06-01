package com.bytes.net.client;

import com.bytes.net.proto.ByteNetProto.RpcRequest;
import com.bytes.net.proto.ByteNetProto.RpcResponse;

import java.util.Map;

/**
 * @author zhangxiaojie
 *         2015-06-01 20:05
 *         功能介绍:
 */
public class RpcContext {
    private RpcRequest rpcRequest;
    private RpcResponse rpcResponse;
    private Map<String,Object> attributes;

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
