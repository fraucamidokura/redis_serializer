package frauca.redis.serializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.annotation.PreDestroy;

@Configuration
public class RedisChannelConfiguration {

    private final RedisConnectionFactory factory;

    public RedisChannelConfiguration(RedisConnectionFactory factory) {
        this.factory = factory;
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
