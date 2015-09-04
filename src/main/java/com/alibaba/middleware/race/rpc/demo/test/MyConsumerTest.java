package com.alibaba.middleware.race.rpc.demo.test;

import java.util.Map;

import org.junit.Assert;

import com.alibaba.middleware.race.rpc.api.*;
import com.alibaba.middleware.race.rpc.context.RpcContext;
import com.alibaba.middleware.race.rpc.demo.service.*;
public class MyConsumerTest 
{
	private static RpcConsumer consumer;
    private static RaceTestService apiService;
    
	static {
        try {
            consumer = (RpcConsumer) getProviderImplClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(consumer == null){
            System.out.println("Start rpc consumer failed");
            System.exit(1);
        }
        //通过apiService即可调用远程服务
        apiService = (RaceTestService) consumer
                .interfaceClass(RaceTestService.class)
                .version("1.0.0.api")
                .clientTimeout(3000)
                .hook(new RaceConsumerHook()).instance();

    }
	
	public static void main(String[] args)
	{
		System.out.println("自定义的测试类，测试各种方法的调用");
		RpcContext.addProp("context", "context");
		Map<String,Object> m = apiService.getMap();
		Map<String,Object> m2 = new RaceTestServiceImpl().getMap();
		System.out.println(m);
		System.out.println(m2);
		/*
		System.out.println("getString方法："+apiService.getString());
		RaceDO rd = apiService.getDO();
		System.out.println("getRaceDO方法："+" |getNum:"+rd.getNum()+"|getStr:"+rd.getStr()
				+"|getList:"+rd.getList());
		RaceChildrenDO rcd = rd.getChild();
		System.out.println("检查raceDo的child接受:"+"|childNum:"+rcd.getChildNum()+"|longValue:"+
				rcd.getLongValue());
		char[] chars = rcd.getChars();
		for(int i=0;i<chars.length;i++)
			System.out.print(chars[i]+" ");
		System.out.println();
		System.out.println("测试longTimeMethod方法");
		long beginTime = System.currentTimeMillis();
        try {
            boolean result = apiService.longTimeMethod();
        } catch (Exception e) {
            long period = System.currentTimeMillis() - beginTime;
            System.out.println(period);
        }
		/*System.out.println("测试异常捕获");
		 try {
	            Integer result = apiService.throwException();
	        } catch (Exception e) {
	            RaceException p = (RaceException)e;
	            System.out.println(p.getFlag());
	        }
		*/
		System.out.println("所有调用完成");
	}
			
			
	private static Class<?> getProviderImplClass(){
        try {
            return Class.forName("com.alibaba.middleware.race.rpc.api.impl.RpcConsumerImpl");
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot found the class which must exist and override all RpcProvider's methods");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
	
	
}
