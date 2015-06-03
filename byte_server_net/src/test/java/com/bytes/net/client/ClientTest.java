package com.bytes.net.client;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:48
 *         功能介绍:
 */
public class ClientTest {
    private RpcClient rpcClient;
    @Before
    public void before(){
        rpcClient = new RpcClient("tcp://127.0.0.1:1228");
    }

    @Test
    public void testStartClient() throws Exception {
        System.out.println(new String(rpcClient.syncCall("testCall","我爱中国".getBytes(),10000L)));;
        System.out.println(new String(rpcClient.syncCall("testCall","我爱中国".getBytes(),10000L)));;
        System.out.println(new String(rpcClient.syncCall("testCall","我爱中国".getBytes(),10000L)));;

    }
}
