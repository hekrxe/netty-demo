package com.hekrxe.netty.demo.apollo.exception;

/**
 * Created by tanhuayou on 2019/01/17
 */
public class TransportException extends Exception {

    public TransportException(String message) {
        super(message);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }
}
