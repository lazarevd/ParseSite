package ru.laz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.laz.db.NewsBlock;
import ru.laz.db.NewsBlockRepo;

import java.util.List;

@RestController
public class ParseController {

    @Autowired
    NewsBlockRepo newsBlockRepo;
    @Autowired
    ObjectMapper objectMapper;

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

    @RequestMapping("/setSent")
    public String setSent() throws Exception {
        return objectMapper.writeValueAsString(newsBlockRepo.findBySent(0));
    }

}
