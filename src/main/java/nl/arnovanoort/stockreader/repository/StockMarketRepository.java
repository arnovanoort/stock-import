package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface StockMarketRepository extends ReactiveCrudRepository<StockMarket, UUID> {

  @Query("select * from stock where stock_market_id = $1")
  Flux<Stock> getStocksByMarket(UUID id);

}