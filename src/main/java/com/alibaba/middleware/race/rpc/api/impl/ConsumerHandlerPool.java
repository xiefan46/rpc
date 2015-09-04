package com.alibaba.middleware.race.rpc.api.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;


public class ConsumerHandlerPool implements PoolableObjectFactory<ConsumerHandler>{
	
	private GenericObjectPool<ConsumerHandler> pool; 
	private EventLoopGroup group;
	private Bootstrap b;
	private String host;
	private int port;
	
	public ConsumerHandlerPool(String host,int port) 
	{
		try
		{
			int coreCount = Runtime.getRuntime().availableProcessors()*2;
			this.host = host;
			this.port = port;
			pool = new GenericObjectPool<ConsumerHandler>(this);
			group = new NioEventLoopGroup();
			b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new FstDecoder(4096));
					ch.pipeline().addLast(new FstEncoder());
					ch.pipeline().addLast(new ConsumerHandler());
					
				}
			});
			for(int i = 0;i < coreCount ; i++)
				pool.addObject();
			System.out.println("初始化完成");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ConsumerHandler getConsumerHandler() throws Exception
	{
		return pool.borrowObject();
	}
	
	public void recycleHandler(ConsumerHandler h) throws Exception
	{
		if(h != null)
		{
			pool.returnObject(h);
		}
	}
	
	@Override
	public ConsumerHandler makeObject() throws Exception {
		ChannelFuture f = b.connect(host, port).sync();
		ConsumerHandler h = f.channel().pipeline().get(ConsumerHandler.class);
		return h;
	}

	@Override
	public void destroyObject(ConsumerHandler obj) throws Exception {
		obj.close();
		
	}

	@Override
	public boolean validateObject(ConsumerHandler obj) {
		if(obj != null) return true;
		return false;
	}

	@Override
	public void activateObject(ConsumerHandler obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void passivateObject(ConsumerHandler obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
