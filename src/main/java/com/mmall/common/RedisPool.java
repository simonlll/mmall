package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by binlin on 2018/9/9.
 */
public class RedisPool {
    private static JedisPool pool; //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));///最大空闲连接数

    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));///最小空闲连接数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));///在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值未true,则取到的jedis实例肯定是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));///在return一个jedis实例的时候，是否要进行验证操作，如果赋值未true,则放回的jedis实例肯定是可用的


    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port", "6379"));///最大空闲连接数

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true); //连接耗尽时是否阻塞

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);

    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }


    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }


    public static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("simon", "simon");
        returnResource(jedis);
        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end ");
    }
}
