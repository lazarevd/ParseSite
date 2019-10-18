package ru.laz.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitMqClient {


    public void init()  {
    try {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("NB_QUEUE", false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", "NB_QUEUE", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    }
