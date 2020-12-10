package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.controller.StockController;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.flywaydb.core.Flyway;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockIntegrationTest {

    StockController stockController;
    StockMarket stockMarket;
    Stock stock;

    @Autowired
    private StockService stockService;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setup(){
        flyway.clean();
        flyway.migrate();
    }

    public StockIntegrationTest(){
        stockController = new StockController();
        stockController.setStockService(stockService);
        stockMarket = new StockMarket(null, "Nasdaq");

    }

    @Test
    public void createNewStock() throws Exception {
        StockMarket newStockMarket = createTestStockMarket();
        stock = new Stock(null, "Amazon", "AMZ", newStockMarket.getId());

        FluxExchangeResult<Stock> result = webTestClient
                .post()
                .uri("/stocks")
                .body(Mono.just(stock), Stock.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Stock.class)
                ;

        StepVerifier.create(result.getResponseBody())
                .assertNext(stock -> {
                    Assert.assertEquals("Amazon", stock.getName());
                })
                .verifyComplete();
    }

    private StockMarket createTestStockMarket() {
        return webTestClient
                .post()
                .uri("/stockmarkets")
                .body(Mono.just(stockMarket), StockMarket.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(StockMarket.class)
                .getResponseBody()
                .blockFirst();
    }

//    @Test
//    public void getStockMarketByUuid() throws Exception {
//        FluxExchangeResult<StockMarket> result = webTestClient
//                .post()
//                .uri("/stockmarkets")
//                .body(Mono.just(market), StockMarket.class)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .returnResult(StockMarket.class)
//                ;
//
//        StepVerifier.create(result.getResponseBody())
//                .assertNext(stockMarket -> Assert.assertEquals("Nasdaq", stockMarket.getName()))
//                .verifyComplete();
//    }
//
//    UUID createdUuid = webTestClient
//                .post()
//                .uri("/stockmarkets")
//                .body(Mono.just(market), StockMarket.class)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .returnResult(StockMarket.class)
//                .getResponseBody()
//                .blockFirst()
//                .getId();
//
//        FluxExchangeResult<StockMarket> result = webTestClient
//                .get()
//                .uri("/stockmarkets/" + createdUuid.toString())
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .returnResult(StockMarket.class)
//                ;
//
//        StepVerifier.create(result.getResponseBody())
//                .assertNext(stockMarket -> Assert.assertEquals("Nasdaq", stockMarket.getName()))
//                .verifyComplete();
//    }

    @Test
    public void importStocksTest(){

    }
}

