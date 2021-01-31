package nl.arnovanoort.stockreader.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.BaseStream;

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
    public Flux<TingoStockPrice> importStockPrices(String ticker, LocalDate from, LocalDate to){
        // TODO: read from config

        String path = "/tiingo/daily/" + ticker + "/prices"  ;
        Flux<TingoStockPrice> result =  client.get()
            .uri(builder -> {
                return builder
                    .path(path)
                    .queryParam("token",     tingoConfig.getToken())
                    .queryParam("startDate", from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .queryParam("endDate",   to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
            })
            .retrieve()
            .bodyToFlux(TingoStockPrice.class)
            .onErrorResume(error -> {
                error.printStackTrace();
                return Mono.empty();
            });
        return result;
    }

    private static Flux<String> fromPath(Path path) {
        return Flux.using(() -> Files.lines(path),
            Flux::fromStream,
            BaseStream::close
        );
    }
}
