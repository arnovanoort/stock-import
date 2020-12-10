package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

public interface StockMarketService {

    public Mono<StockMarket> createStockMarket(StockMarket stockMarket);

    public Mono<StockMarket> getStockMarket(UUID uuid);

    public Flux<StockPrice> updateStockPrizes(UUID uuid, Date from, Date to);

    public Flux<StockPrice> updateStockPrizes(Date from, Date to);
}
