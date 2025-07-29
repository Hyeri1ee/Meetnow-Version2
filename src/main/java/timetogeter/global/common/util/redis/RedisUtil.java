package timetogeter.global.common.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean getBoolean(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void delete(String s) {
        redisTemplate.delete(s);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, String.valueOf(value), timeout, unit);
    }

    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, String.valueOf(value), duration);
    }
}
