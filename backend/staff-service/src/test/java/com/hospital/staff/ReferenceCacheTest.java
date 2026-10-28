package com.hospital.staff;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hospital.common.service.ReferenceCache;
import com.hospital.common.util.JsonCodec;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.*;

class ReferenceCacheTest {
  private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
  private final ValueOperations<String, String> values = mock(ValueOperations.class);
  private final SimpleMeterRegistry metrics = new SimpleMeterRegistry();
  private final ReferenceCache cache =
      new ReferenceCache(
          redis, new JsonCodec(tools.jackson.databind.json.JsonMapper.builder().build()), metrics);

  @BeforeEach
  void setup() {
    when(redis.opsForValue()).thenReturn(values);
  }

  @Test
  void cachedReferenceAvoidsDatabase() {
    when(values.get("departments:v1")).thenReturn("{\"content\":[]}");
    assertEquals(
        List.of(),
        cache
            .get(
                "departments:v1",
                Duration.ofMinutes(5),
                () -> {
                  throw new AssertionError("Should not query database");
                })
            .get("content"));
    assertEquals(1, metrics.counter("hospital.cache.hit").count());
  }

  @Test
  void missingReferenceUsesDatabaseAndSetsTtl() {
    var data = Map.<String, Object>of("content", List.of());
    assertEquals(data, cache.get("key", Duration.ofSeconds(30), () -> data));
    verify(values).set(eq("key"), anyString(), eq(Duration.ofSeconds(30)));
    assertEquals(1, metrics.counter("hospital.cache.miss").count());
  }

  @Test
  void redisOutageFallsBackToDatabase() {
    when(values.get("key")).thenThrow(new RedisConnectionFailureException("unavailable"));
    var data = Map.<String, Object>of("source", "database");
    assertEquals(data, cache.get("key", Duration.ofSeconds(30), () -> data));
    assertEquals(1, metrics.counter("hospital.cache.failure").count());
  }

  @Test
  void writesInvalidateCachedReference() {
    cache.invalidate("departments:v1");
    verify(redis).delete("departments:v1");
  }

  @Test
  void invalidationOutageDoesNotFailBusinessWrite() {
    when(redis.delete("key")).thenThrow(new RedisConnectionFailureException("unavailable"));
    assertDoesNotThrow(() -> cache.invalidate("key"));
    assertEquals(1, metrics.counter("hospital.cache.failure").count());
  }
}
