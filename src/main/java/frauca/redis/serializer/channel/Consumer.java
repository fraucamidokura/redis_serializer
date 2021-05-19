package frauca.redis.serializer.channel;

import reactor.core.publisher.Flux;

public interface  Consumer {

    Flux<Message> consume();
}
