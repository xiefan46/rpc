package com.alibaba.middleware.race.rpc.api.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import com.alibaba.middleware.race.rpc.model.RpcRequest;




public class FstUtil {
	
	private static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
	
	public static byte[] objectToByte(Object obj) 
	{
		try{
			byte[] outByte = new byte[256];
			FSTObjectOutput output = conf.getObjectOutput(outByte);
			output.writeObject(obj);
			output.flush();
			return outByte;	
		}catch(IOException e)
		{
			System.out.println("1");
			try {
				byte[] outByte = new byte[4096];
				FSTObjectOutput output = conf.getObjectOutput(outByte);
				output.writeObject(obj);
				output.flush();
				return outByte;	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		return null;
	}
	
	public static Object byteToObject(byte[] b) throws Exception
	{
		FSTObjectInput input = conf.getObjectInput(b);
		return input.readObject();
	}
	
	public static void main(String[] args) throws Exception
	{
		RpcRequest r = new RpcRequest("hello", null, null, null);
		RpcRequest r2 = (RpcRequest)byteToObject(objectToByte(r));
		System.out.println(r2.getMethodName());
	}
}
