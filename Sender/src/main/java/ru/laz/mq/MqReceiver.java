package ru.laz.mq;

import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqReceiver {
    @Autowired
    AsyncRabbitTemplate asyncRabbitTemplate;


}
