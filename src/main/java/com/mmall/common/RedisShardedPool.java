package com.mmall.common;

import com.mmall.utils.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binlin on 2018/9/13.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool; //sharded jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));//最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));///最大空闲连接数

    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));///最小空闲连接数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));///在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值未true,则取到的jedis实例肯定是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));///在return一个jedis实例的时候，是否要进行验证操作，如果赋值未true,则放回的jedis实例肯定是可用的


    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port", "6379"));///最大空闲连接数


    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port", "6379"));///最大空闲连接数

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true); //连接耗尽时是否阻塞


        JedisShardInfo jedisShardInfo1 = new JedisShardInfo(redis1Ip, redis1Port, 2000);
        JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis2Ip, redis2Port, 2000);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        jedisShardInfoList.add(jedisShardInfo1);
        jedisShardInfoList.add(jedisShardInfo2);

        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);


    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }


    public static void returnResource(ShardedJedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }


    public static void returnBrokenResource(ShardedJedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0; i < 10; i++) {
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
//        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end ");
    }
}
