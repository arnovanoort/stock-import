package nl.arnovanoort.stockreader.service;

//import org.junit.Assert;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
    import org.flywaydb.core.Flyway;
    import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
    import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

//import javax.inject.Inject;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockServiceIntegrationTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMarketService stockMarketService;

//    @Autowired
//    private StockRepository stockRepository;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setup(){
        flyway.clean();
        flyway.migrate();
    }

    LocalDate today = LocalDate.now();

    @Test
    public void testGetStockByName() throws Exception {
        Mono<StockMarket> stockMarket = stockMarketService.createStockMarket(new StockMarket(null, "NASDAQ"));
        Mono<Stock> findStock = stockMarket.flatMap(sm -> {
            return stockService.createStock(new Stock(
                null,
            "Tesla",
            "TSLA",
            "Stock",
            "EUR",
                today,
                today,
            sm.getId()
            ));
        }).flatMap( stock -> {
           return stockService.findStockByTicker(stock.getTicker());
        });

        StepVerifier.create(findStock)
            .assertNext(stock -> {
                Assertions.assertEquals("TSLA", stock.getTicker());
            })
            .verifyComplete();
    }
}
