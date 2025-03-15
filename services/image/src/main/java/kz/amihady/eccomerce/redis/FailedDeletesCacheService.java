package kz.amihady.eccomerce.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailedDeletesCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_KEY = "failed_deletes";


    // ✅ Добавление UUID в Redis (множество Set)
    public void addUUID(UUID uuid) {
        redisTemplate.opsForSet().add(CACHE_KEY, uuid.toString());
        log.info("Добавлен UUID в кэш: " + uuid);
    }

    // ❌ Удаление UUID из Redis
    public void removeUUID(UUID uuid) {
        redisTemplate.opsForSet().remove(CACHE_KEY, uuid.toString());
        log.info("Удален UUID из кэша: " + uuid);
    }

    // 📌 Получение всех UUID из Redis
    public Set<String> getAllUUIDs() {
        Set<String> uuids = redisTemplate.opsForSet().members(CACHE_KEY);
        log.info("Все UUID в кэше: " + uuids);
        return uuids;
    }
}