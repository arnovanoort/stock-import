package nl.arnovanoort.stockreader.repository;

import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface StockPrizeRepository extends ReactiveCrudRepository<StockPrice, UUID> {

}