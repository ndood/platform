package com.fulu.game.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 抽象guava缓存对象
 * @param <K>
 * @param <V>
 */
public abstract class AbsGuavaCache<K, V> {

    /**
     * 缓存自动刷新周期
     */
    protected int refreshDuration;

    /**
     * 缓存刷新周期时间格式
     */
    protected TimeUnit refreshTimeUnitType;

    /**
     * 缓存最大容量
     */
    protected int cacheMaximumSize;

    private LoadingCache<K, V> cache;
    private ListeningExecutorService backgroundRefreshPools = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));

    /**
     * @description: 初始化所有protected字段：
     * refreshDuration、refreshTimeUnitType、cacheMaximumSize
     */
    protected abstract void initCacheFields();

    /**
     * @param key
     * @return
     * @throws Exception
     * @description: 定义缓存值的计算方法
     * @description: 新值计算失败时抛出异常，get操作时将继续返回旧的缓存
     */
    protected abstract V getValueWhenExpire(K key) throws Exception;

    /**
     * @param key
     * @return
     * @description: 提供给外部使用的获取缓存方法，由实现类进行异常处理
     */
    public abstract V getValue(K key);

    /**
     * @return
     * @description: 获取cache实例
     */
    private LoadingCache<K, V> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    initCacheFields();
                    cache = CacheBuilder.newBuilder()
                            .maximumSize(cacheMaximumSize)
                            .refreshAfterWrite(refreshDuration, refreshTimeUnitType)
                            .build(new CacheLoader<K, V>() {
                                @Override
                                public V load(K key) throws Exception {
                                    return getValueWhenExpire(key);
                                }


                                @Override
                                public ListenableFuture<V> reload(final K key, V oldValue) throws Exception {
                                    return backgroundRefreshPools.submit(new Callable<V>() {
                                        public V call() throws Exception {
                                            return getValueWhenExpire(key);
                                        }
                                    });
                                }
                            });
                }
            }
        }
        return cache;
    }


    /**
     * @param key
     * @return
     * @throws ExecutionException
     * @description: 从cache中拿出数据的操作
     */
    protected V fetchDataFromCache(K key) throws ExecutionException {
        return getCache().get(key);
    }

}