package frauca.redis.serializer.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.io.Serializable;

@JsonDeserialize(builder = Message.MessageBuilder.class)
@Value
@Builder(toBuilder = true)
public class Message implements Serializable {
    @JsonProperty("id")
    Long id;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MessageBuilder {

    }
}
