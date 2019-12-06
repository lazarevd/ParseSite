package ru.laz.sender.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockDTO;

import java.io.IOException;

@Service
@ComponentScan(basePackages = {"ru.laz"})
public class MqReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "toSender")
    public void receive(String in) throws IOException {
        NewsBlockDTO newsBlockDTO = objectMapper.readValue(in, NewsBlockDTO.class);
        System.out.println(" [x] Received '" + newsBlockDTO.getTitle() + "'");
    }
}
