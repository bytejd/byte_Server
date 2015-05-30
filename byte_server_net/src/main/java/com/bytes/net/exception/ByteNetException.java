package com.bytes.net.exception;

/**
 * @author zhangxiaojie
 *         2015-05-28 20:39
 *         功能介绍:
 */
public class ByteNetException extends Exception {
    public ByteNetException() {
    }

    public ByteNetException(String s) {
        super(s);
    }

    public ByteNetException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
