/*
 * 文件名：ByteBuffer.java
 * 版权：Copyright 2000-2013 Sumavision. All Rights Reserved. 
 * 描述：
 * 修改人: ManerFan
 * 修改时间：2014年12月8日
 * 修改内容：初次生成
 *
 */
package com.didihe1988.rscode.robinliew.dealbytesinterface;

import java.util.Arrays;

import org.springframework.util.Assert;



/**
 * <p>自动增加长度的byte缓冲
 *
 * @author ManerFan 2014年12月8日
 */
public class ByteBuffer {

    private static final int DEFAULT_CAPACITY = 1024;

    private java.nio.ByteBuffer byteBuffer;

    private int capacity = DEFAULT_CAPACITY;

    public ByteBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public ByteBuffer(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }

        reset();
    }

    public final byte[] toBytes() {
        if (length() < 1) {
            return new byte[]{};
        }

        return Arrays.copyOf(byteBuffer.array(), length());
    }

    public final int length() {
        return byteBuffer.capacity() - byteBuffer.remaining();
    }

    public void reset() {
        byteBuffer = java.nio.ByteBuffer.allocate(capacity);
    }

    public ByteBuffer put(byte[] bs) {
        if (null == bs || bs.length < 1){
            return this;
        }

        int len = bs.length;

        reAllocate(len);

        put(bs, 0, bs.length);

        return this;
    }

    public ByteBuffer put(byte[] bs, int offset, int length) {
        Assert.notNull(bs);

        int len = bs.length;
        Assert.isTrue(len > 0);
        Assert.isTrue(offset >= 0 && offset < len);
        Assert.isTrue(length > offset && length <= len);

        reAllocate(length);

        byteBuffer.put(bs, offset, length);

        return this;
    }

    public ByteBuffer put(byte b) {
        Assert.notNull(b);

        reAllocate(1);

        byteBuffer.put(b);

        return this;
    }

    private void reAllocate(int len) {
        int remain = byteBuffer.remaining();

        int need = len - remain; // 还需要这么多

        if (need > 0) {
            int add = DEFAULT_CAPACITY * (int) Math.ceil(1.0 * need / DEFAULT_CAPACITY);
            add = (add < 1) ? 1 : add;

            int position = byteBuffer.position();

            java.nio.ByteBuffer tempBuffer = java.nio.ByteBuffer.allocate(byteBuffer.capacity()
                    + add);

            tempBuffer.put(byteBuffer.array());

            byteBuffer = tempBuffer;
            byteBuffer.position(position);
        }
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }
}
