package frauca.redis.serializer.ports.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Message;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RedisConsumer implements Consumer {
    private final ReactiveRedisTemplate<String,String> redisTemplate;
    private final ObjectMapper mapper;
    private final Topic topic;


    public RedisConsumer(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper mapper, Topic topic) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public Flux<Message> consume() {
        return redisTemplate.listenTo(topic)
                .concatMap(message->{
                    try {
                        var converted =  mapper.readValue(message.getMessage(),Message.class);
                        return Mono.just(converted);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
    }
}
