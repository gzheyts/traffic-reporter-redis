package com.gzheyts.trafficreporterredis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzheyts.trafficreporterredis.model.Traffic;
import com.gzheyts.trafficreporterredis.service.TrafficService;
import com.gzheyts.trafficreporterredis.service.TrafficServiceImpl;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public final class TrafficControllerIntTest {

    private Traffic time1Traffic;
    private Traffic time2Traffic;
    private Traffic baseTimeTraffic;
    private long baseTime;
    private long time1;
    private long time2;

    @Autowired
    private TrafficService trafficService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RedisTemplate<String, Traffic> redisTemplate;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    private MockMvc mockMvc;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        redisTemplate.delete(TrafficServiceImpl.REDIS_HASH);

        time1Traffic = Traffic.builder().links(singletonList(link(2, 1))).build();
        time2Traffic = Traffic.builder().links(singletonList(link(3, 1))).build();
        baseTimeTraffic = Traffic.builder().links(asList(link(1, 1), link(1, 2))).build();

        baseTime = Instant.now().getEpochSecond();
        time1 = baseTime + 60;
        time2 = baseTime + 60 * 2;

    }

    @Test
    public void testErrorHandling() throws Exception {
        mockMvc.perform(get("/visited_domains").param("from", "[none]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", endsWith("For input string: \"[none]\"")));
    }

    @Test
    public void testSaveTraffic() throws Exception {
        long baseTime = Instant.now().getEpochSecond();
        mockMvc.perform(post("/visited_links")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(baseTimeTraffic)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("ok")));
        final Set<Traffic> byScore = redisTemplate.opsForZSet()
                .rangeByScore(TrafficServiceImpl.REDIS_HASH, baseTime, Long.MAX_VALUE);
        assertThat(byScore, Matchers.hasSize(1));
        assertThat(byScore.iterator().next().getLinks(), equalTo(baseTimeTraffic.getLinks()));
    }

    @Test
    public void testListUniqueDomains() throws Exception {

        trafficService.saveTraffic(baseTimeTraffic, baseTime);
        trafficService.saveTraffic(time1Traffic, time1);
        trafficService.saveTraffic(time2Traffic, time2);

        mockMvc.perform(get("/visited_domains"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domains.length()", equalTo(3)))
                .andExpect(jsonPath("$.domains[0]", equalTo("domain1")))
                .andExpect(jsonPath("$.domains[1]", equalTo("domain2")))
                .andExpect(jsonPath("$.domains[2]", equalTo("domain3")))
                .andExpect(jsonPath("$.status", equalTo("ok")));

        mockMvc.perform(get("/visited_domains")
                .param("from", String.valueOf(time1))
                .param("to", String.valueOf(time2 - 30))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domains[0]", equalTo("domain2")))
                .andExpect(jsonPath("$.domains.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo("ok")));

        mockMvc.perform(get("/visited_domains")
                .param("from", String.valueOf(time2))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domains[0]", equalTo("domain3")))
                .andExpect(jsonPath("$.domains.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo("ok")));
    }


    private static URI link(int domain, int path) {
        return URI.create(String.format("http://domain%d/path%d", domain, path));
    }

}