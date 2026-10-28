package com.hospital.common.service;

import com.hospital.common.util.JsonCodec;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReferenceCache {
  private final StringRedisTemplate redis;
  private final JsonCodec codec;
  private final MeterRegistry metrics;

  public ReferenceCache(StringRedisTemplate redis, JsonCodec codec, MeterRegistry metrics) {
    this.redis = redis;
    this.codec = codec;
    this.metrics = metrics;
  }

  public Map<String, Object> get(String key, Duration ttl, Supplier<Map<String, Object>> load) {
    try {
      String cached = redis.opsForValue().get(key);
      if (cached != null) {
        metrics.counter("hospital.cache.hit").increment();
        return codec.object(cached);
      }
    } catch (DataAccessException e) {
      metrics.counter("hospital.cache.failure").increment();
    }
    metrics.counter("hospital.cache.miss").increment();
    var result = load.get();
    try {
      redis.opsForValue().set(key, codec.json(result), ttl);
    } catch (DataAccessException e) {
      metrics.counter("hospital.cache.failure").increment();
    }
    return result;
  }

  public void invalidate(String key) {
    if (org.springframework.transaction.support.TransactionSynchronizationManager
        .isSynchronizationActive()) {
      org.springframework.transaction.support.TransactionSynchronizationManager
          .registerSynchronization(
              new org.springframework.transaction.support.TransactionSynchronization() {
                public void afterCommit() {
                  evict(key);
                }
              });
    } else evict(key);
  }

  private void evict(String key) {
    try {
      redis.delete(key);
    } catch (DataAccessException e) {
      metrics.counter("hospital.cache.failure").increment();
    }
  }
}
