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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MqSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MqSenderService.class);

    private final static int EXPIRY_TIME = 10000;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    AmqpTemplate rabbitTemplate;

    private Map<Integer, NewsBlockDTO> processing = Collections.synchronizedMap(new HashMap<>());

    long expiryTime = 1000;



    @Transactional
    List<NewsBlockDTO> findAndSetProcessingUnsent() {
        List<NewsBlockEntity> unsent = newsBlockRepo.findBySentAndProcessing(0, 0);
        unsent.forEach(nb -> nb.setProcessing(1));
        newsBlockRepo.saveAll(unsent);
        return convertToDtos(unsent);
    }

    @Scheduled(fixedDelay = 2000)
    public List<NewsBlockDTO> startSend() {
        List<NewsBlockDTO> unsent = findAndSetProcessingUnsent();
        unsent.forEach(nb -> {
            logger.debug("start send" + nb);
            String jsonNB = null;
            try {
                jsonNB = objectMapper.writeValueAsString(nb);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
            rabbitTemplate.convertAndSend("toSender", jsonNB);
            synchronized (processing) {
                nb.setCreatedTime(System.currentTimeMillis());
                processing.put(nb.getId(), nb);
            }
        });
        return unsent;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processExpires() {
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