package frauca.redis.serializer.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class Message implements Serializable {
    Long id;
}
