package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StockMarketRepository extends ReactiveCrudRepository<StockMarket, UUID> {

  @Query("select * from stock where stock_market_id = $1 for update")
  Flux<Stock> getStocksByMarket(UUID id);

  @Query("select * from stock_market where name = $1")
  Mono<StockMarket> findByName(String stockMarket);

  @Query("insert into stock_market values($1, $2, $3) ON CONFLICT DO NOTHING")
  Mono<StockMarket> create(UUID id, String stockMarketName, String client);

}