package com.gzheyts.trafficreporterredis.service;

import com.gzheyts.trafficreporterredis.model.Traffic;

import java.util.List;

public interface TrafficService {

    /**
     * Save traffic
     *
     * @param traffic  payload with uri links
     * @param unixTime request accept unix time
     */
    void saveTraffic(Traffic traffic, Long unixTime);

    /**
     * Query unique visited domains
     *
     * @param from unix time - start
     * @param to   unix time - end
     * @return Unique domains
     */
    List<String> queryUniqueDomains(Long from, Long to);
}
