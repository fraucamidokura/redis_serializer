package frauca.redis.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Publisher;
import frauca.redis.serializer.ports.channel.RedisConsumer;
import frauca.redis.serializer.ports.channel.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import javax.annotation.PreDestroy;

@Configuration
public class RedisChannelConfiguration {

    private final RedisConnectionFactory factory;

    public RedisChannelConfiguration(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Bean
    public Publisher publisher(ReactiveRedisTemplate<String, String> redisTemplate,
                               ObjectMapper mapper,
                               @Value("topic") String topic_name) {
        return new RedisPublisher(redisTemplate, mapper, new ChannelTopic(topic_name));
    }

    @Bean
    public Consumer consumer(ReactiveRedisTemplate<String, String> redisTemplate,
                             ObjectMapper mapper,
                             @Value("topic") String topic_name) {
        return new RedisConsumer(redisTemplate, mapper, new ChannelTopic(topic_name));
    }

    @Bean
    public ReactiveKeyCommands keyCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .keyCommands();
    }

    @Bean
    public ReactiveStringCommands stringCommands(final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return reactiveRedisConnectionFactory.getReactiveConnection()
                .stringCommands();
    }

    @PreDestroy
    public void cleanRedis() {
        factory.getConnection()
                .flushDb();
    }
}
