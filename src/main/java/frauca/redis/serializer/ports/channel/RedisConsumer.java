package frauca.redis.serializer.ports.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Message;
import org.springframework.data.redis.connection.stream.ObjectRecord;

import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.stream.StreamListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

public class RedisConsumer implements Consumer, StreamListener<String, ObjectRecord<String, String>>  {
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
        return Flux.create(pipe)
                .concatMap(strMessage->{
                    try {
                        var converted =  mapper.readValue(strMessage,Message.class);
                        return Mono.just(converted);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public void onMessage(ObjectRecord<String, String> record) {
        pipe.publish(record.getValue());
    }

    private static class MessagePipe implements java.util.function.Consumer<FluxSink<String>> {

        private FluxSink<String> sink;

        public void publish(String message){
            sink.next(message);
        }

        @Override
        public void accept(FluxSink<String> sink) {
            this.sink = sink;
        }

        @Override
        public java.util.function.Consumer<FluxSink<String>> andThen(java.util.function.Consumer<? super FluxSink<String>> after) {
            return java.util.function.Consumer.super.andThen(after);
        }
    }
}
