package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

public interface StockPrizeRepository extends ReactiveCrudRepository<StockPrice, UUID> {

  @Query("select * from stock_price where id = $1 AND date = $2")
  Mono<StockPrice> get(UUID id, Date from);
}