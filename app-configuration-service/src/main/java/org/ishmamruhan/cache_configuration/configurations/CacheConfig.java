package org.ishmamruhan.cache_configuration.configurations;

import org.ishmamruhan.cache_configuration.constants.CacheConstants;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {
    private final CacheManager cacheManager;

    public CacheConfig(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Async
    public void clearCache(Class<?> clazz,String methodName) {
        try{
            String cacheKeyBuilder = clazz.getSimpleName() + "." + methodName;

            Cache cache = cacheManager.getCache(CacheConstants.CACHE_NAME);

            if(cache == null)return;

            getCacheKeys(cache)
                    .stream()
                    .filter(key-> key instanceof String)
                    .map(String::valueOf)
                    .filter(cacheKey -> cacheKey.startsWith(cacheKeyBuilder))
                    .forEach(cache::evict);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<?> getCacheKeys(Cache cache) {
        try{
            if (cache != null) {
                Object nativeCache = cache.getNativeCache();
                if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                    return caffeineCache.asMap().keySet().stream().toList();
                }
            }
            return List.of();
        }catch (Exception e){
            e.printStackTrace();
        }
        return List.of();
    }
}
