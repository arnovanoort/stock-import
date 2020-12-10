package nl.arnovanoort.stockreader.controller;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.service.StockMarketService;
import nl.arnovanoort.stockreader.service.StockService;
import nl.arnovanoort.stockreader.service.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stocks/{uuid}")
    private Mono<Stock> getStock(@PathVariable UUID uuid) {
        return stockService.getStock(uuid);
    }

    @PostMapping("/stocks")
    private Mono<Stock> create(@RequestBody Stock stock){
        return stockService.createStock(stock);
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stocks/import")
    private Mono<Void> importstocks(){
        return stockService.importStocks();
    }

}

