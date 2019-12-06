package ru.laz.sender.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockSendStatusDTO;

import java.io.IOException;

@Service
@ComponentScan(basePackages = {"ru.laz"})
public class MqReceiver {

    Logger log = LoggerFactory.getLogger(MqReceiver.class);

    private final String SENDER_QUEUE = "toSender";
    private final String STATUS_QUEUE = "toParser";


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    AmqpTemplate rabbitTemplate;

    @RabbitListener(queues = SENDER_QUEUE)
    public void receive(String in) throws IOException {
        NewsBlockDTO newsBlockDTO = objectMapper.readValue(in, NewsBlockDTO.class);
        log.info(" [x] Received '" + newsBlockDTO.getTitle() + "'");
        NewsBlockSendStatusDTO nStatus = new NewsBlockSendStatusDTO(newsBlockDTO.getId(), true);
        String respString = objectMapper.writeValueAsString(nStatus);
        log.info("Send status " + nStatus.id);
        rabbitTemplate.convertAndSend(STATUS_QUEUE, respString);
    }
}
