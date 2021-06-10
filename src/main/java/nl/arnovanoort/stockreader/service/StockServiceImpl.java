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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
    StockMarketService stockMarketService;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockPriceRepository stockPriceRepository;

    @Autowired
    TiingoClient tiingoClient;

    @Autowired
    UuidGenerator uuidGenerator;

    @Value("${tiingo.supported-tickers-location}")
    Resource supportedTickersLocation;


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

    @Override
    public Flux<Stock> findStocksByStockMarket(UUID stockMarketUuid) {
        return stockRepository.getStocksByMarket(stockMarketUuid);
    }

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
        return tiingoClient
            .importStockPrices(stock.getTicker(), from, to)
            .map( tingoStockPrice -> {
                return tingoStockPrice.toStockPrice(stock.getId());
            })
            .filterWhen( stockPrice ->
                stockPriceRepository.getByStockUuidAndDate(stockPrice.getStockId(), stockPrice.getDate()).hasElement().map(a -> !a)
            )
            .flatMap( stockPrize -> {
                return stockPriceRepository.save(stockPrize);
            })
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

            var fileFlux = Flux.using(
                () -> Files.lines(
                    Paths
                        .get(supportedTickersLocation
                            .getFile()
                            .getPath()
                        )
                ),
                Flux::fromStream,
                Stream::close
            );
            return importStocks(fileFlux);
        } catch(Exception e){
            e.printStackTrace();
            throw new StockReaderException("Could not read ticker file", e);
        }
    }

    @Override
    public Flux<StockPrice> getStockPrices(UUID uuid, LocalDate from, LocalDate to) {
        return stockPriceRepository.getByStockUuidAndDateWindow(uuid, from, to);
    }

    @Transactional
     Mono<Stock> store(TiingoStock tiingoStock) {
        return stockMarketService
            // all
            .createStockMarket(new StockMarket(null, tiingoStock.getStockMarket()))
            .map(stockMarket -> {
                return tiingoStock.toStock(stockMarket.getId());
            }).flatMap(stock -> {
                return stockRepository
                    .findStockByTicker(stock.getTicker())
                    .switchIfEmpty(Mono.defer(() -> stockRepository.create(
                        uuidGenerator.generate(),
                        stock.getName(),
                        stock.getTicker(),
                        stock.getAssetType(),
                        stock.getCurrency(),
                        stock.getDateListedNullable(),
                        stock.getDateUnListedNullable(),
                        stock.getStockMarketId())))
                    .switchIfEmpty(Mono.defer(()-> stockRepository.findStockByTicker(stock.getTicker())));
            })
            .log();
    }
}
