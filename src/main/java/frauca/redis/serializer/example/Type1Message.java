package frauca.redis.serializer.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import frauca.redis.serializer.channel.Message;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Getter
@SuperBuilder
public class Type1Message extends Message {
    @JsonProperty("name")
    String name;
}
