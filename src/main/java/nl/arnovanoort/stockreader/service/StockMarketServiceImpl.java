package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class StockMarketServiceImpl implements StockMarketService {

    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockMarketRepository stockMarketRepository;

    public Mono<StockMarket> createStockMarket(StockMarket stockMarket){
            return stockMarketRepository.findByName(stockMarket.getName())
            .switchIfEmpty(Mono.defer(() -> stockMarketRepository.create(UUID.randomUUID(), stockMarket.getName(), "tiingo")))
            .switchIfEmpty(Mono.defer(() -> stockMarketRepository.findByName(stockMarket.getName())));
    }

    @Override
    public Mono<StockMarket> getStockMarket(UUID uuid) {
        return stockMarketRepository.findById(uuid);
    }

    @Override
    public Flux<StockPrice> importStockPrices(LocalDate from, LocalDate to) {
        return stockMarketRepository
            .findAll()
            .flatMap( market -> {
                return importStockPrices(market.getId(), from, to);
            });
    }

    @Override
    public Flux<StockPrice> importStockPrices(UUID id, LocalDate from, LocalDate to) {
       return stockMarketRepository
           .getStocksByMarket(id)
           .flatMap( stock -> {
                return stockService.importStockPrices(stock, from, to);
            })
           .log();
    }
}
