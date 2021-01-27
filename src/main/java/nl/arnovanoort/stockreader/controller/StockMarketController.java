package nl.arnovanoort.stockreader.controller;

import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.service.StockMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
public class StockMarketController {

    @Autowired
    private StockMarketService stockMarketService;

    @GetMapping("/stockmarkets/{uuid}")
    private Mono<StockMarket> create(@PathVariable UUID uuid){
        return stockMarketService.getStockMarket(uuid);
    }

    @PostMapping("/stockmarkets")
    private Mono<StockMarket> create(@RequestBody StockMarket stockMarket){
        return stockMarketService.createStockMarket(stockMarket);
    }

    @PostMapping("/stockmarkets/{uuid}/prices")
    private Mono<Void> updateStockPrizes(
        @PathVariable UUID                                                                  uuid,
        @RequestParam(value = "from") @DateTimeFormat(pattern="yyyy-MM-dd") Optional<LocalDate>  from,
        @RequestParam(value = "to"  ) @DateTimeFormat(pattern="yyyy-MM-dd") Optional<LocalDate>  to
    ){
        LocalDate today = LocalDate.now();
        return stockMarketService.updateStockPrices(
            uuid,
            from.orElse(today),
            to.orElse(today)
        ).then();
    }

    public void setStockMarketService(StockMarketService stockMarketService) {
        this.stockMarketService = stockMarketService;
    }
}