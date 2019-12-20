package ru.laz.parser.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.common.models.NewsBlockSendStatusDTO;
import ru.laz.parser.db.repository.NewsBlockRepo;

import java.io.IOException;
import java.util.*;




@Service
public class MqSenderService {

    private static final Logger log = LoggerFactory.getLogger(MqSenderService.class);

    @Value("${news.block.expiry}")
    private static int newsBlockExpiry = 10000;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NewsBlockRepo newsBlockRepo;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    private final String SENDER_QUEUE = "toSender";
    private final String STATUS_QUEUE = "toParser";

    private final Map<Integer, NewsBlockDTO> processing = Collections.synchronizedMap(new HashMap<>());

    @Transactional
    List<NewsBlockDTO> findAndSetProcessingUnsent() {
        List<NewsBlockEntity> unsent = newsBlockRepo.findBySentAndProcessing(0, 0);
        unsent.forEach(nb -> nb.setProcessing(1));
        newsBlockRepo.saveAll(unsent);
        return convertToDtos(unsent);
    }

    @Scheduled(fixedDelay = 1000)
    public List<NewsBlockDTO> startSend() {
        cleanExpires();
        List<NewsBlockDTO> unsent = findAndSetProcessingUnsent();
        unsent.forEach(nb -> {
            log.info("start send "+ nb.getId());
            synchronized (processing) {
                sendToMq(nb);
                nb.setCreatedTime(System.currentTimeMillis());
                processing.put(nb.getId(), nb);
            }
        });
        return unsent;
    }

    private void sendToMq(NewsBlockDTO nbDto) {
        String jsonNB = null;
        try {
            jsonNB = objectMapper.writeValueAsString(nbDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        rabbitTemplate.convertAndSend(SENDER_QUEUE, jsonNB);
    }


    @Transactional
    private void cleanExpires() {
        List<Integer> processingIds = new ArrayList<>();
        synchronized (processing) {
            processing.entrySet().forEach(nb -> {
                if (false == nb.getValue().isExpired(newsBlockExpiry)) {
                    processingIds.add(nb.getKey());
                }
            });
            List<NewsBlockEntity> news = newsBlockRepo.findByProcessing(1);
            news.forEach(nb -> {
                if (false == processingIds.contains(nb.getId())) {
                    nb.setProcessing(0);
                    processing.remove(nb.getId());
                    log.info("cleaned expired " + nb.getId());
                }
            });
            newsBlockRepo.saveAll(news);
        }
    }


    @RabbitListener(queues = STATUS_QUEUE)
    @Transactional
    private void processSent(String statStr) throws IOException {
        log.info("Status received" + statStr);
        NewsBlockSendStatusDTO status = objectMapper.readValue(statStr, NewsBlockSendStatusDTO.class);
        Optional<NewsBlockEntity> nbOptional = newsBlockRepo.findById(status.id);
        nbOptional.ifPresent(nb -> {
            if (status.sent) {
                nb.setSent(1);
                nb.setProcessing(0);
                newsBlockRepo.save(nb);
            }
        });
    }





    private List<NewsBlockDTO> convertToDtos(List<NewsBlockEntity> input) {
        List<NewsBlockDTO> returnList = new ArrayList<>();
        input.forEach(nb -> returnList.add(modelMapper.map(nb, NewsBlockDTO.class)));
        return returnList;
    }

    private List<NewsBlockEntity> convertToEntities(List<NewsBlockDTO> input) {
        List<NewsBlockEntity> returnList = new ArrayList<>();
        input.forEach(nb -> returnList.add(modelMapper.map(nb, NewsBlockEntity.class)));
        return returnList;
    }

}