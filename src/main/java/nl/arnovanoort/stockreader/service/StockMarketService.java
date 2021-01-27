package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public interface StockMarketService {

    public Mono<StockMarket> createStockMarket(StockMarket stockMarket);

    public Mono<StockMarket> getStockMarket(UUID uuid);

    public Flux<StockPrice> updateStockPrices(UUID uuid, LocalDate from, LocalDate to);

    public Flux<StockPrice> updateStockPrices(LocalDate from, LocalDate to);
}
