package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.client.TiingoStock;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.exception.StockReaderException;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
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
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    StockMarketRepository stockMarketRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockPriceRepository stockPriceRepository;

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

    public Mono<Stock> findStockByTicker(String id) {
        return stockRepository.findStockByTicker(id);
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
    public Flux<StockPrice> importStockPrices(Stock stock, LocalDate from, LocalDate to) {
        return Flux.concat(stockPriceRepository.get(stock.getId(), from))
            .switchIfEmpty(
                tiingoClient
                .importStockPrices(stock.getTicker(), from, to)
                .map( tingoStockPrice -> {
                    return tingoStockPrice.toStockPrice(stock.getId());
                })
                .flatMap( stockPrize -> {
                    return stockPriceRepository.save(stockPrize);
                })
            )
            .log();
    }

    @Override
    public Flux<Stock> importStocks(Flux<String> lines) {
        return lines
            .skip(1) // do not process head line
            .map(line -> {
                return TiingoStock.fromCSV(line);
            })
            .flatMap( tiingoStock -> {
                return store(tiingoStock);
            });
    }

    @Override
    public Flux<Stock> importStocksLocal() {
        String location;
        try {
            location = new FileSystemResource(supportedTickersLocation).getFile().getPath();
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
        return stockMarketRepository
            .findByName(tiingoStock.getStockMarket())
            .switchIfEmpty(stockMarketRepository.create(UUID.randomUUID(), tiingoStock.getStockMarket(), "tiingo"))
            .switchIfEmpty(stockMarketRepository.findByName(tiingoStock.getStockMarket()))
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
