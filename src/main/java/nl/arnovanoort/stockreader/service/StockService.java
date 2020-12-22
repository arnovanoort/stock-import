package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public interface StockService {

    Mono<Stock> findStockByName(String id);

    Mono<Stock> getStock(UUID uuid);

    Mono<Stock> createStock(Stock stock);

    Flux<StockPrice> updateStockPrize(Stock stock, Date from, Date to);

    Flux<Stock> importStocks(Flux<String> lines);

    public Flux<Stock> importStocksLocal();
}
