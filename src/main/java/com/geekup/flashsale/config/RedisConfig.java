package com.geekup.flashsale.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> deductInventoryScript() {
        ResourceScriptSource scriptSource =
                new ResourceScriptSource(
                        new ClassPathResource("scripts/deduct_inventory.lua"));

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(scriptSource);
        script.setResultType(Long.class);

        return script;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer (Giữ nguyên - Rất chuẩn)
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Cấu hình ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Hỗ trợ LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Tránh serialize timestamp dạng số
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // BẮT BUỘC THÊM ĐOẠN NÀY: Kích hoạt lưu thông tin Class (Default Typing)
        // Nếu không có, khi Deserialize từ Redis lên, Object sẽ bị biến thành LinkedHashMap
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        // Value serializer
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}