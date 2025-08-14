package com.example.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class RedisRateLimiter {

    @Autowired
    private StringRedisTemplate redis;

    private static final String LUA = "\n" +
            "local tokens_key = KEYS[1]\n" +
            "local timestamp_key = KEYS[2]\n" +
            "local capacity = tonumber(ARGV[1])\n" +
            "local rate = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local requested = tonumber(ARGV[4])\n" +
            "local last_tokens = tonumber(redis.call('get', tokens_key))\n" +
            "if last_tokens == nil then last_tokens = capacity end\n" +
            "local last_refreshed = tonumber(redis.call('get', timestamp_key))\n" +
            "if last_refreshed == nil then last_refreshed = now end\n" +
            "local delta = (now - last_refreshed) / 1000.0\n" +
            "if delta < 0 then delta = 0 end\n" +
            "local filled_tokens = last_tokens + (delta * rate)\n" +
            "if filled_tokens > capacity then filled_tokens = capacity end\n" +
            "local allowed = 0\n" +
            "if filled_tokens >= requested then\n" +
            "  allowed = 1\n" +
            "  filled_tokens = filled_tokens - requested\n" +
            "end\n" +
            "redis.call('set', tokens_key, filled_tokens)\n" +
            "redis.call('set', timestamp_key, now)\n" +
            "return { allowed, filled_tokens }\n";

    private final DefaultRedisScript<List> script;

    public RedisRateLimiter() {
        this.script = new DefaultRedisScript<>();
        this.script.setScriptText(LUA);
        this.script.setResultType(List.class);
    }

    public boolean allow(String bucketKey, long capacity, double ratePerSecond) {
        return allow(bucketKey, capacity, ratePerSecond, 1);
    }

    public boolean allow(String bucketKey, long capacity, double ratePerSecond, long requestedTokens) {
        long now = Instant.now().toEpochMilli();
        List<String> keys = Arrays.asList("rl:" + bucketKey + ":tokens", "rl:" + bucketKey + ":ts");
        List<String> res = (List<String>) redis.execute(script, keys,
                String.valueOf(capacity), String.valueOf(ratePerSecond), String.valueOf(now), String.valueOf(requestedTokens));
        if (res == null || res.isEmpty()) return true;
        String allowed = String.valueOf(res.get(0));
        return "1".equals(allowed) || "1L".equals(allowed);
    }
}






