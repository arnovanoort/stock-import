package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public interface StockService {

    Mono<Stock> findStockByTicker(String id);

    Flux<Stock> findStocksByStockMarket(UUID stockMarketUuid);

    Mono<Stock> getStock(UUID uuid);

    Mono<Stock> createStock(Stock stock);

    Flux<StockPrice> importStockPrices(Stock stock, LocalDate from, LocalDate to);

    Flux<Stock> importStocks(Flux<String> lines);

    Flux<Stock> importStocksLocal();

    Flux<StockPrice> getStockPrices(UUID uuid, LocalDate from, LocalDate to);
}
