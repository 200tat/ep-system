package com.primeton.manageProvider.util;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUtil {

    /**
     * 当用户登入时生成 一个 不重复的令牌，并将此令牌作为 键 ，传入的obj作为值存入redis
     * @param redisTemplate
     * @param object
     * @return
     */
    public static String setToken(RedisTemplate redisTemplate,Object object){
        //生成不重复的随机的令牌
        String token = UUID.randomUUID().toString();
        System.out.println(token);
        //将用户存入Redis数据库
        String objString = JSON.toJSONString(object);
        redisTemplate.opsForValue().set(token,objString);
        return token;
    }

    /**
     * 获取 传入 键token 在redis中存储的 值
     * @param redisTemplate
     * @param token
     * @return
     */
    public static Object getObj(RedisTemplate redisTemplate,String token){
        return redisTemplate.opsForValue().get(token);
    }


    /**
     * 删除 redis中此 token
     * @param redisTemplate
     * @param token
     * @return
     */
    public static Boolean destroy(RedisTemplate redisTemplate,String token){
        return redisTemplate.delete(token);
    }
}
