package kr.hhplus.be.server.supporters.cache;

import java.time.Duration;

public interface Cacheable {
    String createKey(String key);

    String cacheName();

    Duration ttl();
}
