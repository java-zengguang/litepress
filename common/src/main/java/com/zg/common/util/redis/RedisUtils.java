package com.zg.common.util.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

public class RedisUtils {

    private static JedisPool jedisPool;
    private static boolean initFlag = true;


    private static void init() {
        if (jedisPool == null && initFlag) {
            jedisPool = new JedisPool("localhost", 6379);
            initFlag = false;
        }
    }

    public static Jedis getConnection() {
        init();
        Jedis jedis = null;

        jedis = jedisPool.getResource();
        return jedis;
    }

    public static void append(Map<String, String> map) {
        if (map != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                append(key, map.get(key));
            }
        }
    }

    public static long append(String key, String value) {
        Jedis jedis = null;
        jedis = getConnection();
        return jedis.append(key, value);
    }

    public static long delete(String key) {
        Jedis jedis = null;
        jedis = getConnection();
        return jedis.del(key);
    }

    public static String get(String key) {

        Jedis jedis = null;
        jedis = getConnection();
        return jedis.get(key);
    }

}
