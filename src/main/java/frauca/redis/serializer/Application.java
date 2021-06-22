package frauca.redis.serializer;

import frauca.redis.serializer.channel.Consumer;
import frauca.redis.serializer.channel.Message;
import frauca.redis.serializer.channel.Publisher;
import frauca.redis.serializer.example.Type1Message;
import frauca.redis.serializer.example.Type2Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    private final Publisher publisher;
    private final Consumer consumer;

    public Application(Publisher publisher, Consumer consumer) {

        this.publisher = publisher;
        this.consumer = consumer;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        consumer.consume()
                .doOnNext(message -> {
                    var msg = String.valueOf(message.getId());
                    if(message instanceof Type1Message){
                        var type1 = (Type1Message) message;
                        msg = type1.getName();
                    }else if (message instanceof Type2Message){
                        var type2 = (Type2Message) message;
                        msg = type2.getText();
                    }
                    log.info("message ==> {}", msg);
                })
                .subscribe();
        Thread.sleep(1000);
        publisher.publish(Message.builder().id(1L).build());
        //publisher.publish(Type1Message.builder().id(1L).name("type 1 example").build());
        //publisher.publish(Type2Message.builder().id(1L).text("type 2 example").build());
    }
}
