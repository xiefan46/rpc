package com.alibaba.middleware.race.rpc.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * Created by huangsheng.hs on 2015/4/8.
 */
public class RpcContext {
    
    public static ThreadLocal<Map<String,Object> > localMap = new ThreadLocal<Map<String,Object> >(){
    	@Override
        protected Map<String,Object> initialValue() {
            Map<String,Object> m = new HashMap<String,Object>();
            return m;
        }
    };

    public static void addProp(String key ,Object value){
        localMap.get().put(key,value);
    }

    public static Object getProp(String key){
        return localMap.get().get(key);
    }

    public static Map<String,Object> getProps(){
       return Collections.unmodifiableMap(localMap.get());
    }
    
    public static boolean isAsyn(String methodName)
	{
		Map<String,Object> m = RpcContext.localMap.get();
		Set<String> asynMethods = (Set<String>)m.get("AsynMethods");
		if(asynMethods != null && asynMethods.contains(methodName))
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
    
    public static void addAsyn(String methodName)
    {
    	Set<String> s = (Set<String>)localMap.get().get("AsynMethods");
    	s.add(methodName);
    }
    
    public static void removeAsyn(String methodName)
    {
    	Set<String> s = (Set<String>)localMap.get().get("AsynMethods");
    	if(s.contains(methodName))
    	{
    		s.remove(methodName);
    	}
    }
}
