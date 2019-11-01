package ru.laz.parser.mq;

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
import ru.laz.parser.db.NewsBlockRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MqSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MqSenderService.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    AmqpTemplate rabbitTemplate;

    private Map<Integer, Long> processTimes = new HashMap<>();

    long expiryTime = 1000;



    //@Scheduled(fixedDelay = 10000)
    @Transactional
    public List<NewsBlockEntity> startSend() {
        List<NewsBlockEntity> unsent = newsBlockRepo.findBySentAndProcessing(0, 0);
        unsent.forEach(nb -> {
            nb.setProcessing(1);
            logger.debug("start send" + nb);
            NewsBlockDTO newsBlockDTO = modelMapper.map(nb, NewsBlockDTO.class);
            rabbitTemplate.convertAndSend("toSender", newsBlockDTO);
            synchronized (processTimes) {
                processTimes.put(nb.getId(), System.currentTimeMillis());
            }
        });
        newsBlockRepo.saveAll(unsent);
        return unsent;
    }


    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processExpires() {
        List<Integer> ids = new ArrayList<>();
        synchronized (processTimes) {
            processTimes.entrySet().forEach(en -> {
                if (isExpired(en.getKey())) {
                    ids.add(en.getKey());
                }
            });
        }

        List<NewsBlockEntity> news = StreamSupport.stream(newsBlockRepo.findAll().spliterator(), false)
                .filter(nb -> isExpired(nb.getId()))
                .collect(Collectors.toList());

        news.forEach(nb -> nb.setProcessing(0));
            newsBlockRepo.saveAll(news);
    }

    private boolean isExpired(int id) {
        Long idTime = null;
        synchronized (processTimes) {
            idTime = processTimes.get(id);
        }
        if (idTime != null && (System.currentTimeMillis()- idTime.longValue()) < expiryTime) {
            return false;
        }
        return true;
    }
}