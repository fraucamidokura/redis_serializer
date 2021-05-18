package frauca.redis.serializer.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import frauca.redis.serializer.channel.Message;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Getter
@SuperBuilder
public class Type2Message extends Message {
    @JsonProperty("text")
    String text;
}
