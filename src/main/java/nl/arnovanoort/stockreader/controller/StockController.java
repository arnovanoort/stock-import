package nl.arnovanoort.stockreader.controller;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    MultipartReader multipartReader;

    /**
     * Read a stock from the database.
     * @param uuid uuid of the stock to be retrieved.
     * @return a [[Stock]] corresponding to the requested uuid.
     */
    @GetMapping("/stocks/{uuid}")
    private Mono<Stock> getStock(@PathVariable UUID uuid) {
        return stockService.getStock(uuid);
    }

    /**
     * Request body contains a single stock that will be read and stored in the database.
     * @param stock Data that makes a stock.
     * @return the stored stock.
     */
    @PostMapping("/stocks")
    private Mono<ResponseEntity<Stock>> create(@RequestBody Stock stock){
        return stockService.createStock(stock)
        .map(createdStock -> {
            return ResponseEntity.created(URI.create("/stocks")).body(createdStock);
        });
    }

    @GetMapping("/stockmarkets/{uuid}/stocks")
    private Flux<Stock> getStocksByStockMarket(@PathVariable UUID uuid){
        return stockService.findStocksByStockMarket(uuid);
    }

    @GetMapping("/stocks/{uuid}/prices")
    private Flux<StockPrice> getStocksPrices(
            @PathVariable UUID uuid,
            @RequestParam(value = "from") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "to") @DateTimeFormat(pattern="yyyy-MM-dd") Optional<LocalDate> endDate
    ){
        return stockService.getStockPrices(uuid, startDate, endDate.orElse(LocalDate.now()));
    }

    /**
     * Takes a csv file with stock data as input and propagates that file to the stockservice
     * which will create a [[Stock]] in the database for each line.
     * @param file csv file containing stock information
     * @return all created stocks.
     */
    // TODO: Do not return all the stocks.
    //@PostMapping("/stocks/import")
    //public Flux<Stock> upload(@RequestPart("file") FilePart file) {
    //    return stockService
    //        .importStocks(multipartReader.getLines(file));
    //}

    /**
     * This endpoint will forward a request to the stockService to read an online csv file and
     * create a [[Stock]] in the database for each entry
     * @return nothing
     */
    @PostMapping("/stocks/importlocal")
    public Mono<ResponseEntity> upload() {
        return stockService
            .importStocksLocal()
            .count()
            .map(nrOfStocks -> ResponseEntity.created(URI.create("/stocks")).body(nrOfStocks));
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

}

