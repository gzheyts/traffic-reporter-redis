package com.gzheyts.trafficreporterredis.service;

import com.gzheyts.trafficreporterredis.model.Traffic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TrafficServiceImpl implements TrafficService {
    public static final String REDIS_HASH = "traffic";

    private final RedisTemplate<String, Traffic> redisTemplate;

    /**
     * Save traffic
     *
     * @param traffic  payload with uri links
     * @param unixTime request accept unix time
     */
    @Override
    public void saveTraffic(Traffic traffic, Long unixTime) {
        redisTemplate.opsForZSet().add(REDIS_HASH, traffic, unixTime);

    }

    /**
     * Query unique visited domains
     *
     * @param from unix time - start
     * @param to   unix time - end
     * @return Unique domains
     */
    @Override
    public List<String> queryUniqueDomains(Long from, Long to) {
        return redisTemplate.opsForZSet()
                .rangeByScore(REDIS_HASH,
                        Optional.ofNullable(from).orElse(Long.MIN_VALUE),
                        Optional.ofNullable(to).orElse(Long.MAX_VALUE)
                )
                .stream()
                .map(Traffic::getLinks)
                .flatMap(Collection::stream)
                .map(uri -> Optional.ofNullable(uri.getHost()).orElse(uri.getPath()))
                .distinct()
                .collect(Collectors.toList());
    }
}
