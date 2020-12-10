package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StockRepository extends ReactiveCrudRepository<Stock, UUID> {

    @Query("select * from stock_market where stock_market_id = $1")
    Flux<StockMarket> getStockMarkets();

    @Query("select * from stock where name = $1")
    Mono<Stock> findStockByName(String name);


//    @Query("select * from stock where stock_market_id = $1")
//    Mono<StockPrize> createStockPrize(StockPrize stockPrize);
}