package com.alibaba.middleware.race.rpc.demo.test;

import com.alibaba.middleware.race.rpc.context.RpcContext;
import com.alibaba.middleware.race.rpc.demo.service.*;

public class TestContext {
	public static void main(String[] args)
	{
		RpcContext.addProp("abc", "def");
		RaceTestService s = new RaceTestServiceImpl();
		System.out.println(s.getMap());
	}
}
