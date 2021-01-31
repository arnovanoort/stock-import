package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public interface StockPriceRepository extends ReactiveCrudRepository<StockPrice, UUID> {

  @Query("select * from stock_price where id = $1 AND date = $2")
  Mono<StockPrice> get(UUID id, LocalDate from);

  @Query("select * from stock_price where stock_id = $1 AND date >= $2 AND date <= $3")
  Flux<StockPrice> getByStockUuid(UUID stockId, LocalDate from, LocalDate until);

}