package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
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
        return stockMarketRepository.save(stockMarket);
    }

    @Override
    public Mono<StockMarket> getStockMarket(UUID uuid) {
        return stockMarketRepository.findById(uuid);
    }

    @Override
    public Flux<StockPrice> updateStockPrices(Date from, Date to) {
        return stockMarketRepository
            .findAll()
            .flatMap( market -> {
                return updateStockPrices(market.getId(), from, to);
            });
    }

    @Override
    public Flux<StockPrice> updateStockPrices(UUID id, Date from, Date to) {
         return stockMarketRepository.getStocksByMarket(id).flatMap( stock -> {
            Flux<StockPrice> result = stockService.updateStockPrize(stock, from, to);
            return result;
        });
    }

}
