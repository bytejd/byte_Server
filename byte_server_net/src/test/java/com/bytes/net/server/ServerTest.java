package com.bytes.net.server;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zhangxiaojie
 *         2015-05-29 15:41
 *         功能介绍:
 */
public class ServerTest {
    private ByteRPCServer byteRPCServer ;
    @Before
    public void before(){
        byteRPCServer = new ByteRPCServer("127.0.0.1",1228);
    }

    @Test
    public void testStartRpcServer() throws Exception {
        byteRPCServer.start();
    }
}
