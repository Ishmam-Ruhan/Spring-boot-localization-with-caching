package org.ishmamruhan.cache_configuration.configurations;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Configuration
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder cacheName = new StringBuilder();

        //Class name
        cacheName.append(target.getClass().getSimpleName()).append(".");

        //Method name
        cacheName.append(method.getName());

        for (Object param : params) {
            cacheName.append(":").append(param.toString());
        }

        return cacheName.toString();
    }
}
