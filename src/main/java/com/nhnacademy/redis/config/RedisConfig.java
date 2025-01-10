package com.nhnacademy.redis.config;


import com.nhnacademy.redis.credentials.DatabaseCredentials;
import com.nhnacademy.redis.service.SecureKeyManagerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
public class RedisConfig {

    private final SecureKeyManagerService secureKeyManagerService;

    public RedisConfig(SecureKeyManagerService secureKeyManagerService) {
        this.secureKeyManagerService = secureKeyManagerService;
    }


    @Bean
    public RedisConnectionFactory redisConnectionFactory(){

        DatabaseCredentials databaseCredentials =new DatabaseCredentials(secureKeyManagerService.fetchSecretFromKeyManager());

        Map<String, String> credentialsMap = databaseCredentials.getCredentialsMap();

        String host = credentialsMap.get("host");
        int port = Integer.parseInt(credentialsMap.get("port"));
        String password = credentialsMap.get("password");
        int database = Integer.parseInt(credentialsMap.get("database"));


        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        config.setDatabase(database);
        return new JedisConnectionFactory(config);
    }


        @Bean
        public RedisTemplate<String, String> redisTemplate() {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            return template;
        }
}
