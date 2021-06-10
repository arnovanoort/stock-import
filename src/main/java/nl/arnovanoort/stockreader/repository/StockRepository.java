package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface StockRepository extends ReactiveCrudRepository<Stock, UUID> {

    @Query("select * from stock where stock_market_id = $1")
    Flux<StockMarket> getStockMarket(UUID stockMarketId);

    @Query("select * from stock where ticker = $1")
    Mono<Stock> findStockByTicker(String ticker);

    @Query("insert into stock values ($1, $2, $3, $4, $5, $6, $7, $8) ON CONFLICT DO NOTHING")
    Mono<Stock> create(
        UUID randomUUID,
        String name,
        String ticker,
        String assetType,
        String currency,
        LocalDate dateListedNullable,
        LocalDate dateUnListedNullable,
        UUID stockMarketId
    );

    @Query("select * from stock where stock_market_id = $1 for update")
    Flux<Stock> getStocksByMarket(UUID id);

}