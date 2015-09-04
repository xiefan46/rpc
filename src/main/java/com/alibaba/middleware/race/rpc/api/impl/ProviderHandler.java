package com.alibaba.middleware.race.rpc.api.impl;


import java.lang.reflect.Method;

import com.alibaba.middleware.race.rpc.context.RpcContext;
import com.alibaba.middleware.race.rpc.model.RpcRequest;
import com.alibaba.middleware.race.rpc.model.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProviderHandler extends ChannelInboundHandlerAdapter
{	
	private Object serviceInstance;
	
	public ProviderHandler(Object serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
	 @Override
	 public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	 {
		 //System.out.println("收到客户端请求");
	     RpcResponse resp = handleRequest((RpcRequest)msg);
	     ctx.writeAndFlush(resp);
	 }  
	 
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	            throws Exception 
	 {
	      cause.printStackTrace();
	      ctx.close();
	 }  
	 
	 public RpcResponse handleRequest(RpcRequest request)
	 {
		 RpcResponse response = new RpcResponse();
		 try
		 {
			 RpcContext.localMap.set(request.getContextMap());
			 Method method = serviceInstance.getClass()
					 	.getMethod(request.getMethodName(),request.getParameterTypes());
			 Object result = method.invoke(serviceInstance, request.getArgs());
			 response.setAppResponse(result);
		 }catch(Throwable t)
		 {
			 response.setErrorMsg("err");
			 response.setAppResponse(t);
		 }
		 return response;
	 }
	
}
