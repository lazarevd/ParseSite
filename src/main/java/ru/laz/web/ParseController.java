package ru.laz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.laz.db.NewsBlockRepo;

@RestController
public class ParseController {

    @Autowired
    NewsBlockRepo newsBlockRepo;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/getAllNews")
    public String getDbJson() throws Exception {
        //List<NewsBlock> nBlocks  = newsBlockRepo.findAll();
        //return objectMapper.writeValueAsString(nBlocks);
        return null;
    }


}
