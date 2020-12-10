package nl.arnovanoort.stockreader.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TiingoClient {

    private WebClient client;

    @Autowired
    TingoConfig tingoConfig;

    public TiingoClient(TingoConfig tingoConfig){
        this.tingoConfig = tingoConfig;
        client = WebClient.create(tingoConfig.getHost());
    }

    /*
        https://api.tiingo.com/tiingo/daily/aapl/prices?token=<TOKEN HERE>&startDate=12-02-2020&endDate=12-02-2020
     */
    public Flux<TingoStockPrice> getStockPrize(String ticker, Date from, Date to){
        String path = tingoConfig.getPath() + ticker;
        Flux<TingoStockPrice> result =  client.get()
            .uri(builder -> {
                return builder
<<<<<<< Updated upstream
                    .path(tingoConfig.path)
                    .queryParam("token",     "4616db0e5891b1fcadd8f338d94df8f72b7949d7")//tingoConfig.getToken())
=======
                    .path(tingoConfig.getPath())
                    .queryParam("token",     tingoConfig.getToken())
>>>>>>> Stashed changes
                    .queryParam("startDate", new SimpleDateFormat("yyyy-MM-dd").format(from))
                    .queryParam("endDate",   new SimpleDateFormat("yyyy-MM-dd").format(to))
                    .build();
            })
            .retrieve()
            .bodyToFlux(TingoStockPrice.class);
        return result;
    }
}
