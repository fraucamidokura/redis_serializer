package frauca.redis.serializer.ports.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import frauca.redis.serializer.channel.Message;
import frauca.redis.serializer.channel.Publisher;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

public class RedisPublisher implements Publisher {

    private final ReactiveRedisTemplate<String,String> redisTemplate;
    private final ObjectMapper mapper;
    private final Topic topic;

    public RedisPublisher(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper mapper, Topic topic) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
        this.topic = topic;
    }

    @SneakyThrows
    @Override
    public void publish(Message message) {
        redisTemplate.convertAndSend(topic.getTopic(), mapper.writeValueAsString(message)).block();
    }
}
