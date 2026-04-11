package com.github.iappapp.panda.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheUtils {
    // 构建caffeine的缓存对象，并指定在写入后的10分钟内有效，且最大允许写入的条目数为10000
    private final static Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(2)
            .removalListener((k, v, cause) -> {
                System.out.println("key=" + k + " value=" + v + " cause=" + cause);
            })
            .build(new CacheLoader<String, String>() {
                @Override
                public @Nullable String load(@NonNull String key) throws Exception {
                    return key + "wod";
                }
            });


    private static String create(Object key) {
        return key + " world";
    }
}
