package ru.laz.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.Application;
import ru.laz.db.NewsBlock;
import ru.laz.db.NewsBlockRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class MQSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MQSenderService.class);

    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    AmqpTemplate rabbitTemplate;

    private Map<Integer, Long> processTimes = new HashMap<>();

    long expiryTime = 3000;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void startSend() {
        List<NewsBlock> unsent = newsBlockRepo.findBySentAndProcessing(0, 0);
        unsent.forEach(nb -> {
            nb.setProcessing(1);
            logger.debug("start send" + nb);
            rabbitTemplate.convertAndSend(nb);
            synchronized (processTimes) {
                processTimes.put(nb.getId(), System.currentTimeMillis());
            }
        });
        newsBlockRepo.saveAll(unsent);
    }


    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processExpires() {
        List<Integer> ids = new ArrayList<>();
            processTimes.entrySet().forEach(en -> {
                if (isExpired(en.getKey())) {
                    ids.add(en.getKey());
                }
            });

        List<NewsBlock> news = StreamSupport.stream(newsBlockRepo.findAll().spliterator(), false)
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