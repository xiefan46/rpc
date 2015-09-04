package com.alibaba.middleware.race.rpc.demo.service;

import com.alibaba.middleware.race.rpc.api.impl.FstUtil;
import com.alibaba.middleware.race.rpc.context.RpcContext;
import com.alibaba.middleware.race.rpc.model.RpcRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangsheng.hs on 2015/3/26.
 */
public class RaceTestServiceImpl implements RaceTestService{
    @Override
    public Map<String, Object> getMap() {
        Map<String,Object> newMap = new HashMap<String,Object>();
        newMap.put("race","rpc");
        if(RpcContext.getProps() != null )
        	newMap.putAll(RpcContext.getProps());
        return newMap;
    }

    @Override
    public String getString() {
        return "this is a rpc framework";
    }

    @Override
    public RaceDO getDO() {
        return new RaceDO();
    }

    @Override
    public boolean longTimeMethod() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Integer throwException() throws RaceException{
        throw new RaceException("just a exception");
    }
    
    public static void main(String[] args) throws Exception
    {
    	Map<String,Object> m = new HashMap<>();
    	m.put("fejaifjais", "ffeasfeas");
    	m.put("fei", "fesf");
    	RpcRequest request = new RpcRequest("getDo", null, null, m);
    	byte[] out = FstUtil.objectToByte(request);
    	System.out.println(out.length);
    }
}
