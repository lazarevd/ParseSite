package ru.laz.sender.senders.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.sender.mq.MqService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Service
@PropertySource("file:config/telegram.properties")
public class TelegramSender {

    private Set<Integer> process = new HashSet<>();

    @Value("${telegram.bot.protocol}")
    private String botProtocol;
    @Value("${telegram.bot.url}" )
    private String botUrl;
    @Value("${telegram.bot.token}")
    private String botToken;
    private final String SEND_METHOD = "/sendMessage";
    @Value("${async.http.connect.timeout}")
    private int httpConnectionTimeout;

    @Value("${telegram.bot.chat_id}")
    int botChatId;


    @Autowired
    private MqService mqService;
    @Autowired
    private ObjectMapper objectMapper;

    private final static Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config().setConnectTimeout(httpConnectionTimeout);
    private AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

    @PostConstruct
    public void init() {
        log.info(new File("").getAbsolutePath());
        log.info(botProtocol+"://"+botUrl+botToken);
    }



    public void sendToTelegram(NewsBlockDTO newsBlockDTO) {
        int id = newsBlockDTO.getId();
        if (!process.contains(id)) {
            try {
                sendToChannel(newsBlockDTO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            process.add(id);
        }
    }

    private void sendToChannel (NewsBlockDTO newsBlockDTO) throws JsonProcessingException {
        int id = newsBlockDTO.getId();
        TelegramDTO telegramDTO = new TelegramDTO(botChatId, newsBlockDTO.getTitle());
        String jsonTelegramDTO = objectMapper.writeValueAsString(telegramDTO);
        BoundRequestBuilder request = client.preparePost(botProtocol+"://"+botUrl+botToken+SEND_METHOD)
                .setBody(jsonTelegramDTO);
        log.info("Strart send: "+ jsonTelegramDTO);
        request.execute(new AsyncHandler<Object>() {
            @Override
            public State onStatusReceived(HttpResponseStatus response) {
                if (response.getStatusCode() == 200) {
                    log.info("Sent :" + jsonTelegramDTO);
                    mqService.sendOkStatus(newsBlockDTO.getId()); }
                else {
                    log.error("Failed to send :" + ", "
                            + jsonTelegramDTO + ", "
                            + response.getRemoteAddress() + ", "
                            + response.getStatusCode() + ", "
                            + response.getStatusText());
                }
                process.remove(id);
                return null;
            }

            @Override
            public State onHeadersReceived(HttpHeaders httpHeaders) {
                return null;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) {
                return null;
            }

            @Override
            public void onThrowable(Throwable throwable) {
                log.error(throwable.getMessage());
                process.remove(id);
            }

            @Override
            public Object onCompleted() {
                process.remove(id);
                return null;
            }
        });
    }
}

