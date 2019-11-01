package ru.laz.parser.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class MqConfiguration {


    @Value( "${parser.rabbitmq.host}" )
    private String mqHost;

    @Bean
    public Queue hello() {
        return new Queue("toSender");
    }

}
