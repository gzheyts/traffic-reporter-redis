package com.gzheyts.trafficreporterredis.config;

import com.gzheyts.trafficreporterredis.model.Traffic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@ComponentScan("com.gzheyts.trafficreporterredis")
public class RedisConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, ?> redisTemplate() {
        final RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Traffic.class));
        template.setEnableTransactionSupport(true);
        return template;
    }


    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSource dataSource() throws SQLException {

        DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getConnection()).thenReturn(Mockito.mock(Connection.class));
        return ds;
    }
}
