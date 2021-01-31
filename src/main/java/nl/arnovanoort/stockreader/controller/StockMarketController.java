package nl.arnovanoort.stockreader.controller;

import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.service.StockMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
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
    private Mono<ResponseEntity> updateStockPrizes(
        @PathVariable UUID                                                                       uuid,
        @RequestParam(value = "from") @DateTimeFormat(pattern="yyyy-MM-dd") Optional<LocalDate>  from,
        @RequestParam(value = "to"  ) @DateTimeFormat(pattern="yyyy-MM-dd") Optional<LocalDate>  to
    ){
        LocalDate today = LocalDate.now();
        return stockMarketService.importStockPrices(
            uuid,
            from.orElse(today),
            to.orElse(today)
        ).then(Mono.just(ResponseEntity.created(URI.create("/stocks")).build()));
    }

    public void setStockMarketService(StockMarketService stockMarketService) {
        this.stockMarketService = stockMarketService;
    }
}