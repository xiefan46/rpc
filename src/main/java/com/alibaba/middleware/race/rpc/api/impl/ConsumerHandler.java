package com.alibaba.middleware.race.rpc.api.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;




import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ConsumerHandler extends ChannelInboundHandlerAdapter {
	private final BlockingQueue<RpcResponse> answer = 
			new LinkedBlockingQueue<RpcResponse>(); 
	private volatile Channel channel;
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("通道建立");
		this.channel = ctx.channel();
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RpcResponse response = (RpcResponse)msg;
		answer.offer(response, 3000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	public RpcResponse sendRpcRequest(RpcRequest request)
	{
		channel.writeAndFlush(request);
		RpcResponse response;
		boolean interrupted = false;
			for(;;)
			{
				try
				{
					response = answer.take();
					break;
				}catch(InterruptedException e)
				{
					interrupted = true;
				}
			}
		if (interrupted) 
		{  
			Thread.currentThread().interrupt();  
		} 
		return response;
	}
	
	public void close() throws Exception
	{
		this.channel.close().sync();
	}
}
