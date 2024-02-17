package com.peanut.diff.runtime.spi;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.checker.DefaultDiffChecker;
import com.peanut.diff.runtime.checker.ChangeCheckerInitialContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings({ "rawtypes"})
public class ServiceLocator {
    private static final String defaultChecker = DefaultDiffChecker.class.getName();
    private static Map<String, ChangeChecker> cache;
    public static ChangeCheckerInitialContext context;

    static {
        cache = new ConcurrentHashMap<>();
        context = new ChangeCheckerInitialContext();
    }

    public static ChangeChecker get(Class<?> service){
        return get(service.getName());
    }

    public static ChangeChecker get(String serviceName){
        if(cache.containsKey(serviceName)){
            return cache.get(serviceName);
        }
        init(serviceName);

        return cache.get(serviceName);
    }

    public static ChangeChecker getDefault(){
        return get(defaultChecker);
    }

    public static ChangeChecker getBeanChecker(Class beanClass){
        return getBeanChecker(beanClass.getName());
    }
    public static ChangeChecker getBeanChecker(String beanClass){
        String checker = context.getBeanChecker(beanClass);
        if(checker == null) {
            throw new RuntimeException(beanClass+"没有注册比较器");
        }
        return get(checker);
    }

    synchronized static void init(String serviceName){
        ChangeChecker service = cache.get(serviceName);
        if(service != null){
            return ;
        }
        ChangeChecker service1 = (ChangeChecker)context.lookup(serviceName);
        cache.put(serviceName,service1);
    }
}
