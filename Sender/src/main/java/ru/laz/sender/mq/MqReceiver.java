package ru.laz.sender.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import ru.laz.common.models.NewsBlockDTO;

@RabbitListener(queues = "toSender")
public class MqReceiver {

    @RabbitHandler
    public void receive(NewsBlockDTO in) {
        System.out.println(" [x] Received '" + in.getTitle() + "'");
    }




}
