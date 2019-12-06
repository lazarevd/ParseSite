package ru.laz.parser.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.parser.db.repository.NewsBlockRepo;

import java.util.*;

@Service
public class MqSenderService {

    private static final Logger log = LoggerFactory.getLogger(MqSenderService.class);

    private final static int EXPIRY_TIME = 10000;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NewsBlockRepo newsBlockRepo;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    private final String QUEUE_NAME = "toSender";

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
        rabbitTemplate.convertAndSend(QUEUE_NAME, jsonNB);
    }


    @Transactional
    private void cleanExpires() {
        List<Integer> processingIds = new ArrayList<>();
        synchronized (processing) {
            processing.entrySet().forEach(nb -> {
                if (false == nb.getValue().isExpired(EXPIRY_TIME)) {
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