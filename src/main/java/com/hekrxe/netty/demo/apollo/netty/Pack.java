package com.hekrxe.netty.demo.apollo.netty;

/**
 * Created by tanhuayou on 2019/01/19
 */
final public class Pack {
    private byte type;
    private byte[] message;


    public interface Type {
        byte SYS_IDLE = -1;

        byte UNDEFINE = 0;

        byte NORMAL = 1;
    }

    public Pack() {
    }

    public Pack(byte[] message) {
        this.type = Type.NORMAL;
        this.message = message;
    }

    public Pack(byte type, byte[] message) {
        this.type = type;
        this.message = message;
    }

    public byte getType() {
        return type;
    }

    public Pack setType(byte type) {
        this.type = type;
        return this;
    }

    public byte[] getMessage() {
        return message;
    }

    public Pack setMessage(byte[] message) {
        this.message = message;
        return this;
    }
}
