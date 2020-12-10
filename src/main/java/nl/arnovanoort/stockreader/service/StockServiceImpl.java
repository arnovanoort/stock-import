package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.repository.StockPrizeRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockPrizeRepository stockPrizeRepository;

    @Autowired
    TiingoClient tiingoClient;



//    public void updateStocks(){
//        Flux<StockMarket> markets = stockRepository.getStockMarkets();
//        markets.flatMap( market -> {
//            return stockRepository.getStocksByMarket(market.getId()).flatMap( stock -> {
//                return tiingoClient.getStock(stock.getTicker()).flatMap( stockPrize -> {
//                    Mono<StockPrize> savedPrize =  stockPrizeRepository.save(stockPrize);
//                    return savedPrize;
//                });
//            });
//        })
//        .then()
//        .subscribe();
//    }

    public Mono<Stock> findStockByName(String id) {
        Mono<Stock> stock =  stockRepository.findStockByName(id);
        Mono<Stock> s = stock.map(s2 -> {
            System.out.println("s2: " + s2);
            return s2;
        });
        return stock;
    }

    @Override
    public Mono<Stock> getStock(UUID uuid) {
        return stockRepository.findById(uuid);
    }

    @Override
    public Mono<Stock> createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Flux<StockPrice> updateStockPrize(Stock stock, Date from, Date to) {
        return tiingoClient
            .getStockPrize(stock.getTicker(), from, to)
            .map( tingoStockPrice -> tingoStockPrice
                .toStockPrice(stock.getId()))
            .flatMap( stockPrize -> {
                return stockPrizeRepository.save(stockPrize);
            });
    }

    @Override
    public Mono<Void> importStocks() {

        return null;
    }
}
