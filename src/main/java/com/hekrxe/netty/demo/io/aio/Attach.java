package com.hekrxe.netty.demo.io.aio;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanhuayou on 2019/09/04
 */
public class Attach {
    private CountDownLatch watch;
    private AsynchronousServerSocketChannel asyncServerChannel;

    public Attach(CountDownLatch watch, AsynchronousServerSocketChannel asyncServerChannel) {
        this.watch = watch;
        this.asyncServerChannel = asyncServerChannel;
    }

    public CountDownLatch getWatch() {
        return watch;
    }

    public Attach setWatch(CountDownLatch watch) {
        this.watch = watch;
        return this;
    }

    public AsynchronousServerSocketChannel getAsyncServerChannel() {
        return asyncServerChannel;
    }

    public Attach setAsyncServerChannel(AsynchronousServerSocketChannel asyncServerChannel) {
        this.asyncServerChannel = asyncServerChannel;
        return this;
    }
}
