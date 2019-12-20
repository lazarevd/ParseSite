package ru.laz.parser.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.parser.db.repository.NewsBlockRepo;
import ru.laz.parser.mq.MqSenderService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ParseController {

    @Autowired
    NewsBlockRepo newsBlockRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MqSenderService mqSenderService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    /*

    @RequestMapping("/getAllNews")
    public String getDbJson() throws Exception {
        Iterable<NewsBlockEntity> it = newsBlockRepo.findAll();
        List<NewsBlockEntity> nBlocks  = (List<NewsBlockEntity>) newsBlockRepo.findAll();
        List<NewsBlockDTO> nbdto = new ArrayList<>();
        nBlocks.forEach((nb) -> nbdto.add(modelMapper.map(nb, NewsBlockDTO.class)));
        return objectMapper.writeValueAsString(nbdto);
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
        NewsBlockEntity nb = newsBlockRepo.findById(id).get();
        if (null != nb)
        {nb.setSent(1);}
        newsBlockRepo.save(nb);
        rabbitTemplate.convertAndSend(nb);
        return objectMapper.writeValueAsString(nb);
    }


    @RequestMapping("/startSend")
    public ResponseEntity startSend() {
        List<NewsBlockDTO> sent = mqSenderService.startSend();
        return ResponseEntity.ok("sent: " + sent.size());
    }


    @Transactional
    @RequestMapping("/setUnsent")
    public String setUnsent(@RequestParam int id) throws JsonProcessingException {
        NewsBlockEntity nb = newsBlockRepo.findById(id).get();
        if (null != nb)
        {
            nb.setProcessing(0);
            nb.setSent(0);
        }
        newsBlockRepo.save(nb);
        return objectMapper.writeValueAsString(nb);
    }


    @Transactional
    @RequestMapping("/setProcessing")
    public String setProcessing(@RequestParam int id) throws Exception {
        NewsBlockEntity nb = newsBlockRepo.findById(id).get();
        if (null != nb)
        {nb.setProcessing(1);}
        newsBlockRepo.save(nb);
        rabbitTemplate.convertAndSend(nb);
        return objectMapper.writeValueAsString(nb);
    }
    
     */
}
