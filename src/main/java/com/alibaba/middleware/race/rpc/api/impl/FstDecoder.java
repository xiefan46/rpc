package com.alibaba.middleware.race.rpc.api.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class FstDecoder extends LengthFieldBasedFrameDecoder{
	public FstDecoder(int maxObjectSize)
	{
		super(maxObjectSize, 0, 4, 0, 4);
	}
	
	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        byte[] out = new byte[frame.readableBytes()];
        frame.readBytes(out);
        return FstUtil.byteToObject(out);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
