package com.alibaba.middleware.race.rpc.api.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FstEncoder  extends MessageToByteEncoder<Object>{
	
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		int lengthPos = out.writerIndex();
		out.writeBytes(LENGTH_PLACEHOLDER);
		out.writeBytes(FstUtil.objectToByte(msg));
		out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
	}

}
