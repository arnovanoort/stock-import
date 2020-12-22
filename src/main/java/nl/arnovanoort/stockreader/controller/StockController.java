package nl.arnovanoort.stockreader.controller;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    MultipartReader multipartReader;

    @GetMapping("/stocks/{uuid}")
    private Mono<Stock> getStock(@PathVariable UUID uuid) {
        return stockService.getStock(uuid);
    }

    @PostMapping("/stocks")
    private Mono<ResponseEntity<Stock>> create(@RequestBody Stock stock){
        return stockService.createStock(stock)
        .map(createdStock -> {
            return ResponseEntity.created(URI.create("/stocks")).body(createdStock);
        });
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stocks/import")
    public Flux<Stock> upload(@RequestPart("file") FilePart file) {
        return stockService
            .importStocks(multipartReader.getLines(file));
    }

    @PostMapping("/stocks/importlocal")
    public Mono<Void> upload() {
        return stockService
            .importStocksLocal()
            .then();
    }


}

