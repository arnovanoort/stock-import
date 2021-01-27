package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.client.TiingoStock;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.exception.StockReaderException;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPrizeRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    StockMarketRepository stockMarketRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockPrizeRepository stockPrizeRepository;

    @Autowired
    TiingoClient tiingoClient;

    @Value("${tiingo.supported-tickers-location}")
    String supportedTickersLocation;


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
        Mono<Stock> stock =  stockRepository.findStockByTicker(id);
        Mono<Stock> s = stock.map(s2 -> {
            System.out.println("s2: " + s2);
            return s2;
        });
        return stock;
    }

    @Override
    public Mono<Stock> getStock(UUID uuid) {
        Mono<Stock> stock = stockRepository.findById(uuid);
        stock.doOnNext(stock2 ->
            System.out.println("stock2" + stock2));
        return stock;
    }

    @Override
    public Mono<Stock> createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Flux<StockPrice> updateStockPrize(Stock stock, LocalDate from, LocalDate to) {

        return Flux.concat(stockPrizeRepository.get(stock.getId(), from))
            .doOnNext(stockPrice -> System.out.println("S1 : " + stockPrice))
            .switchIfEmpty(tiingoClient
                .getStockPrize(stock.getTicker(), from, to)
                .map( tingoStockPrice -> tingoStockPrice
                    .toStockPrice(stock.getId())
                )
                .flatMap( stockPrize -> {
                    return stockPrizeRepository.save(stockPrize);
                })
                .doOnNext(stockPrice -> {
                    System.out.println("S2 : " + stockPrice);
                })
            ).log();
    }


    @Override
    public Flux<Stock> importStocks(Flux<String> lines) {
        System.out.println("Start import!!");
        return lines
            .skip(1) // do not process head line
            .map(line -> {
                return TiingoStock.fromCSV(line);
            })
            .flatMap( tiingoStock -> {
                return store(tiingoStock);
            })
            .doOnNext(stock -> System.out.println("STOCK 1: " + stock));
    }



    @Override
    public Flux<Stock> importStocksLocal() {
        String location;
        try {
            location = new FileSystemResource(
                supportedTickersLocation)
                .getFile().getPath();
        } catch(Exception e){
            e.printStackTrace();
            throw new StockReaderException("Could not read ticker file", e);
        }

        return Flux.using(
            () -> Files.lines(Paths.get(location)),
            Flux::fromStream,
            Stream::close
        )
            .skip(1) // do not process head line
            .map( line -> { return TiingoStock.fromCSV(line); })
            .flatMap( tiingoStock -> { return store(tiingoStock); })
            .log();
    }

    @Transactional
     Mono<Stock> store(TiingoStock tiingoStock) {
        System.out.println("Starting new line: " + tiingoStock);
        return stockMarketRepository
            .findByName(tiingoStock.getStockMarket())
            .switchIfEmpty(stockMarketRepository.create(UUID.randomUUID(), tiingoStock.getStockMarket(), "tiingo"))
            .switchIfEmpty(stockMarketRepository.findByName(tiingoStock.getStockMarket()))
            .flatMap(
                market -> stockMarketRepository.findByName(tiingoStock.getStockMarket()))
            .map(stockMarket -> {
                return tiingoStock.toStock(stockMarket.getId());
            }).flatMap(stock -> {
                return stockRepository
                    .findStockByTicker(stock.getTicker())
                    .switchIfEmpty(stockRepository.create(
                        UUID.randomUUID(),
                        stock.getName(),
                        stock.getTicker(),
                        stock.getAssetType(),
                        stock.getCurrency(),
                        stock.getDateListedNullable(),
                        stock.getDateUnListedNullable(),
                        stock.getStockMarketId()))
                    .switchIfEmpty(stockRepository.findStockByTicker(stock.getTicker()));
            })
            .log();
    }
}
