package ru.laz.parser.grabber;

import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.parser.db.repository.NewsBlockRepo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Service
public class PagesService {

    private final static Logger logger = LoggerFactory.getLogger(PagesService.class);

    DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config().setConnectTimeout(500);
    AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

    @Autowired
    NewsBlockRepo newsBlockRepo;


    public List<NewsBlockEntity> parseHtml(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();
        Elements elems = body.getElementsByTag("main")
                .first().getElementsByClass("item-block item-news");
        List<NewsBlockEntity> retList = new ArrayList<>();

        logger.debug("elems " + elems.size());
        for (Element el : elems) {
            Element nElementHref = el.getElementsByTag("a").first();
            String date = el.getElementsByClass("date").text();
            String url = nElementHref.attr("href");
            String title = nElementHref.getElementsByTag("h2").first().text();
            NewsBlockEntity nb = new NewsBlockEntity();
            nb.setTitle(title);
            nb.setUrl(url);
            nb.setDate(date);
            retList.add(nb);
        }
        return retList;
    }


    @Scheduled(fixedDelay = 30000)
    public void getPageContent() {
        BoundRequestBuilder request = client.prepareGet("http://mosfarr.ru/category/новости/");
        request.execute(new AsyncHandler<Object>() {
            int status;
            StringBuilder sb = new StringBuilder();
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus)
                    throws Exception {
                logger.debug(responseStatus.toString());
                status = responseStatus.getStatusCode();
                if (status == 200) {
                    return State.CONTINUE;
                } else {
                    return State.ABORT;
                }
            }

            @Override
            public State onHeadersReceived(HttpHeaders headers)
                    throws Exception {
                return State.CONTINUE;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                    throws Exception {
                    sb.append(new String( bodyPart.getBodyPartBytes(), StandardCharsets.UTF_8));
                return State.CONTINUE;
            }

            @Override
            public void onThrowable(Throwable t) {

            }

            @Override
            public Object onCompleted() {
                if (status == 200) {
                    List<NewsBlockEntity> news = parseHtml(sb.toString());
                    logger.debug("Fetched: " + news.size());
                    for (NewsBlockEntity nb : news) {
                            newsBlockRepo.insertF(nb);
                    }
                }
                return null;
            }
        });
    }
}
