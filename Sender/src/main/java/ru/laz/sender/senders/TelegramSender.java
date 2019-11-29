package ru.laz.sender.senders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
@PropertySource("classpath:telegram.properties")
public class TelegramSender {

    @Value("${telegram.bot.url}" )
    String botUrl;

    @Value("${telegram.bot.token}")
    String botToken;

    @PostConstruct
    public void init() {
        System.out.println( new File("").getAbsolutePath());
        System.out.println(botUrl+botToken);
    }
}

