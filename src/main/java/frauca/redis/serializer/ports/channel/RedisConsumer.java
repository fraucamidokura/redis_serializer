package frauca.redis.serializer.ports.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Message;
import lombok.SneakyThrows;
import org.springframework.data.redis.connection.stream.ObjectRecord;

import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.stream.StreamListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

public class RedisConsumer implements Consumer, StreamListener<String, ObjectRecord<String, Message>>  {
    private final ObjectMapper mapper;
    private final Topic topic;
    private final MessagePipe pipe;


    public RedisConsumer(ObjectMapper mapper, Topic topic) {
        this.pipe = new MessagePipe();
        this.mapper = mapper;
        this.topic = topic;
    }

    @Override
    public Flux<Message> consume() {
        return Flux.create(pipe);
    }

    @SneakyThrows
    @Override
    public void onMessage(ObjectRecord<String, Message> record) {
        Message value = record.getValue();
        pipe.publish(value);
    }

    private static class MessagePipe implements java.util.function.Consumer<FluxSink<Message>> {

        private FluxSink<Message> sink;

        public void publish(Message message){
            sink.next(message);
        }

        @Override
        public void accept(FluxSink<Message> sink) {
            this.sink = sink;
        }

        @Override
        public java.util.function.Consumer<FluxSink<Message>> andThen(java.util.function.Consumer<? super FluxSink<Message>> after) {
            return java.util.function.Consumer.super.andThen(after);
        }
    }
}
