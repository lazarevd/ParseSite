package ru.laz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.laz.db.NewsBlock;
import ru.laz.db.NewsBlockRepo;
import ru.laz.mq.RabbitMqClient;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
public class ParseController {

    @Autowired
    NewsBlockRepo newsBlockRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RabbitMqClient rabbitMqClient;

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
        rabbitMqClient.init();
        return objectMapper.writeValueAsString(nb);
    }

}
