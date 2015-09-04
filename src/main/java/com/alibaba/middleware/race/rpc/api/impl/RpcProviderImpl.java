package com.alibaba.middleware.race.rpc.api.impl;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;






import com.alibaba.middleware.race.rpc.api.*;


public class RpcProviderImpl extends RpcProvider
{
	private static int PORT = 8888;
	
    public RpcProviderImpl() {
    	super();
    	System.out.println("rpc provider start");
    }

 
    /**
     * set the interface which this provider want to expose as a service
     * @param serviceInterface
     */
    //这个方法是服务器向外部展示自己所能提供的服务,服务在RaceTestService类中
    @Override
    public RpcProvider serviceInterface(Class<?> serviceInterface){
        this.serviceInterface = serviceInterface;
        return this;
    }

    /**
     * set the version of the service
     * @param version
     */
    @Override
    public RpcProvider version(String version){
        //TODO
        return this;
    }

    /**
     * set the instance which implements the service's interface
     * @param serviceInstance
     */
    @Override
    public RpcProvider impl(Object serviceInstance){
    	this.serviceInstance = serviceInstance;
        return this;
    }

    /**
     * set the timeout of the service
     * @param timeout
     */
    @Override
    public RpcProvider timeout(int timeout){
        //TODO
        return this;
    }

    /**
     * set serialize type of this service
     * @param serializeType
     */
    @Override
    public RpcProvider serializeType(String serializeType){
        //TODO
        return this;
    }

    /**
     * publish this service
     * if you want to publish your service , you need a registry server.
     * after all , you cannot write servers' ips in config file when you have 1 million server.
     * you can use ZooKeeper as your registry server to make your services found by your consumers.
     */
    //这个函数可以用来向外暴露自己的服务,暂时先约定服务的端口是9000
    @Override
    public void publish() 
    {
    	publish(PORT);
    }
    
    public void publish(int port)
    {
    	EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .option(ChannelOption.SO_BACKLOG,100)
			 .option(ChannelOption.TCP_NODELAY, true)
			 .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
			 .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			 .childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
			 .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			 .childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new FstDecoder(4096));
					ch.pipeline().addLast(new FstEncoder());
					ch.pipeline().addLast(new ProviderHandler(serviceInstance));
				}
			});
			ChannelFuture f = b.bind(port).sync();
			System.out.println("server start! port:"+port);
			f.channel().closeFuture().sync();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
    }
    
   
    
    private Object serviceInstance;
    private Class<?> serviceInterface;
    
   
}
