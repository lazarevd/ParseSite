package ru.laz.sender.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.laz.sender.senders.telegram.TelegramSender;

import java.io.IOException;

@Service
@ComponentScan(basePackages = {"ru.laz"})
public class MqService {

    Logger log = LoggerFactory.getLogger(MqService.class);

    private final String SENDER_QUEUE = "toSender";
    private final String STATUS_QUEUE = "toParser";


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    AmqpTemplate rabbitTemplate;
    @Autowired
    TelegramSender telegramSender;

    @RabbitListener(queues = SENDER_QUEUE)
    public void receive(String in) throws IOException {
        NewsBlockDTO newsBlockDTO = objectMapper.readValue(in, NewsBlockDTO.class);
        log.info("Received from MQ" + newsBlockDTO.getTitle() + "'");
        telegramSender.sendToTelegram(newsBlockDTO);
    }

    public void sendOkStatus(int id) {
        NewsBlockSendStatusDTO nStatus = new NewsBlockSendStatusDTO(id, true);
        String respString = null;
        try {
            respString = objectMapper.writeValueAsString(nStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("Send status to MQ" + nStatus.id);
        rabbitTemplate.convertAndSend(STATUS_QUEUE, respString);
    }

}
