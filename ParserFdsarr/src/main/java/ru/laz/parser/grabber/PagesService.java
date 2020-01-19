package ru.laz.parser.grabber;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
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

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Service
public class PagesService {


    private final static Logger logger = LoggerFactory.getLogger(PagesService.class);

    AsyncHttpClient client;

    private final String BASE_URL = "https://fdsarr.ru";

    @PostConstruct
    public void init() {
        SslContext sslContext = null;

        {
            try {
                sslContext = SslContextBuilder
                        .forClient()
                        .sslProvider(SslProvider.JDK)
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        }

        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(3500)
                .setSslContext(sslContext);//ignore wrong certs
        client = Dsl.asyncHttpClient(clientBuilder);
    }


    @Autowired
    private NewsBlockRepo newsBlockRepo;


    public List<NewsBlockEntity> parseHtml(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();
        Elements elems = body.getElementsByTag("section").first()
                .getElementById("newswrap")
                .getElementsByClass("content-wrap").first()
                .getElementsByClass("news-list div-with-shadow").first()
                .getElementsByTag("ul").first()
                .getElementsByTag("li");

        Element elemTop = body.getElementsByTag("section").first()
                .getElementById("newswrap")
                .getElementsByClass("content-wrap").first()
                .getElementsByClass("news-list div-with-shadow").first()
                .getElementsByClass("top-news").first();

        String urlTop = BASE_URL + elemTop.getElementsByTag("a").first().attr("href");
        String dateTop = elemTop.getElementsByClass("arr-news").first().text();
        String titleTop = elemTop.getElementsByTag("div").first().getElementsByTag("h3").first().text();
        NewsBlockEntity nbt = new NewsBlockEntity();
        nbt.setTitle(titleTop);
        nbt.setUrl(urlTop);
        nbt.setDate(dateTop);
        List<NewsBlockEntity> retList = new ArrayList<>();
        retList.add(nbt);
        logger.debug("elems " + elems.size());
        for (Element el : elems) {
            Element li = el.getElementsByTag("li").first();
            String url = BASE_URL + li.getElementsByTag("a").first().attr("href");
            String date = li.getElementsByClass("arr-news").first().text();
            String title = li.getElementsByTag("h3").first().text();

            NewsBlockEntity nb = new NewsBlockEntity();
            nb.setTitle(title);
            nb.setUrl(url);
            nb.setDate(date);
            retList.add(nb);
        }

        return retList;
    }


    @Scheduled(fixedDelayString = "${news.block.refresh}")
    public void getPageContent() {
        BoundRequestBuilder request = client.prepareGet(BASE_URL+"/arr/news/");
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
System.out.println(t.getMessage());
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
