package nl.arnovanoort.stockreader.repository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface StockMarketTestUtil extends StockMarketRepository {

    public Mono<Void> deleteAll();
}
