package com.rdbackend.weather_api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

        @Bean
        @Primary
        public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
                GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
                // The default constructor of GenericJackson2JsonRedisSerializer since Spring
                // Boot 2.x/3.x
                // usually needs explicit time module registration if not handled by
                // auto-config.
                // However, a more robust way in Spring is to configure the ObjectMapper.

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                // Preserve class info for deserialization.
                // NOTE: Records are FINAL classes, so NON_FINAL excludes them.
                // Using EVERYTHING ensures Records get type information stored in JSON.
                mapper.activateDefaultTyping(
                                com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator.instance,
                                com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING,
                                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

                serializer = new GenericJackson2JsonRedisSerializer(mapper);

                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(serializer));

                RedisCacheConfiguration threeHourTTL = defaultConfig.entryTtl(Duration.ofHours(3));
                RedisCacheConfiguration oneDayTTL = defaultConfig.entryTtl(Duration.ofHours(24));

                return RedisCacheManager.builder(factory)
                                .cacheDefaults(defaultConfig)
                                .withInitialCacheConfigurations(
                                                Map.of(
                                                                "weather:current", threeHourTTL,
                                                                "weather:forecast", threeHourTTL,
                                                                "cities:suggest", oneDayTTL))
                                .build();
        }
}
