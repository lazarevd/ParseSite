package ru.laz.parser.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.laz.parser.db.NewsBlock;
import ru.laz.parser.db.NewsBlockRepo;
import ru.laz.parser.mq.MqSenderService;

import javax.transaction.Transactional;
import java.util.List;

@RestController
public class ParseController {

    @Autowired
    NewsBlockRepo newsBlockRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MqSenderService mqSenderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/getAllNews")
    public String getDbJson() throws Exception {
        Iterable<NewsBlock> it = newsBlockRepo.findAll();
        List<NewsBlock> nBlocks  = (List<NewsBlock>) newsBlockRepo.findAll();
        return objectMapper.writeValueAsString(nBlocks);
    }


    @RequestMapping("/getUnsent")
    public String getUnsent() throws Exception {
        return objectMapper.writeValueAsString(newsBlockRepo.findBySent(0));
    }

    @RequestMapping("/getUnprocessing")
    public String getUnprocessing() throws Exception {
        return objectMapper.writeValueAsString(newsBlockRepo.findByProcessing(0));
    }


    @Transactional
    @RequestMapping("/setSent")
    public String setSent(@RequestParam int id) throws Exception {
        NewsBlock nb = newsBlockRepo.findById(id).get();
        if (null != nb)
        {nb.setSent(1);}
        newsBlockRepo.save(nb);
        rabbitTemplate.convertAndSend(nb);
        return objectMapper.writeValueAsString(nb);
    }


    @RequestMapping("/startSend")
    public ResponseEntity startSend() {

        List<NewsBlock> sent = mqSenderService.startSend();
        return ResponseEntity.ok("sent: " + sent.size());
    }


    @Transactional
    @RequestMapping("/setProcessing")
    public String setProcessing(@RequestParam int id) throws Exception {
        NewsBlock nb = newsBlockRepo.findById(id).get();
        if (null != nb)
        {nb.setProcessing(1);}
        newsBlockRepo.save(nb);
        rabbitTemplate.convertAndSend(nb);
        return objectMapper.writeValueAsString(nb);
    }

}
