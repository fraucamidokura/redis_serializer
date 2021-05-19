package frauca.redis.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Publisher;
import frauca.redis.serializer.ports.channel.RedisConsumer;
import frauca.redis.serializer.ports.channel.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import javax.annotation.PreDestroy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE;

@Configuration
public class RedisChannelConfiguration {

    private final RedisConnectionFactory factory;

    public RedisChannelConfiguration(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Bean
    public Publisher publisher(ReactiveRedisTemplate<String, String> redisTemplate,
                               ObjectMapper mapper,
                               @Value("${topic}") String topic_name) {
        return new RedisPublisher(redisTemplate, mapper, new ChannelTopic(topic_name));
    }

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory,
                                     StreamListener<String, ObjectRecord<String, String>> stream,
                                     @Value("${topic}") String topic_name) throws UnknownHostException {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(1))
                .targetType(String.class)
                .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer = StreamMessageListenerContainer
                .create(redisConnectionFactory, options);

        Subscription subscription = listenerContainer.receive(
                org.springframework.data.redis.connection.stream.Consumer
                        .from(topic_name, InetAddress.getLocalHost()
                                .getHostName()),
                StreamOffset.create(topic_name, ReadOffset.lastConsumed()),
                stream);
        listenerContainer.start();
        return subscription;
    }

    @Bean
    public Consumer consumer(ObjectMapper mapper,
                             @Value("${topic}") String topic_name) {
        return new RedisConsumer( mapper, new ChannelTopic(topic_name));
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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return jackson2ObjectMapperBuilder -> {

            TypeResolverBuilder<?> typeResolver = new ObjectMapper.DefaultTypeResolverBuilder(OBJECT_AND_NON_CONCRETE);
            typeResolver = typeResolver.init(JsonTypeInfo.Id.CLASS, null);
            typeResolver = typeResolver.inclusion(JsonTypeInfo.As.PROPERTY);

            jackson2ObjectMapperBuilder.defaultTyping(typeResolver);
        };
    }

    @PreDestroy
    public void cleanRedis() {
        factory.getConnection()
                .flushDb();
    }
}
