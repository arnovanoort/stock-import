package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.controller.StockMarketController;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
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

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockMarketIntegrationTest {

    StockMarketController stockMarketController;
    StockMarket market;

    @Autowired
    private StockMarketService stockMarketService;

    @Autowired
    private StockMarketRepository stockMarketRepository;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    Flyway flyway;

    @BeforeEach
    public void setup(){
        flyway.clean();
        flyway.migrate();
    }

    public StockMarketIntegrationTest(){
        stockMarketController = new StockMarketController();
        stockMarketController.setStockMarketService(stockMarketService);
        market = new StockMarket(null, "Nasdaq");
    }

    @Test
    public void createNewStockMarket() throws Exception {
        FluxExchangeResult<StockMarket> result = webTestClient
                .post()
                .uri("/stockmarkets")
                .body(Mono.just(market), StockMarket.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(StockMarket.class)
                ;

        StepVerifier.create(result.getResponseBody())
                .assertNext(stockMarket -> Assert.assertEquals("Nasdaq", stockMarket.getName()))
                .verifyComplete();
    }

    @Test
    public void getStockMarketByUuid() throws Exception {
        UUID createdUuid = webTestClient
            .post()
            .uri("/stockmarkets")
            .body(Mono.just(market), StockMarket.class)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(StockMarket.class)
            .getResponseBody()
            .blockFirst()
            .getId();

        FluxExchangeResult<StockMarket> result = webTestClient
            .get()
            .uri("/stockmarkets/" + createdUuid.toString())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(StockMarket.class)
            ;

        StepVerifier.create(result.getResponseBody())
            .assertNext(stockMarket -> Assert.assertEquals("Nasdaq", stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void getStockMarketByName() throws Exception {

        Mono<StockMarket> foundMarket = stockMarketService.createStockMarket(market)
            .flatMap(sm -> {
                return stockMarketRepository.findByName(sm.getName());
            });

        StepVerifier.create(foundMarket)
            .assertNext(stockMarket -> Assert.assertEquals("Nasdaq", stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void importStockPrices(){
//        stockMarketService.createStockMarket(market).subscribe();
//            .flatMap(sm -> {
//                return stockMarketRepository.findUsingTheName(sm.getName());
//            });

        //        UUID createdUuid = webTestClient
//            .post()
//            .uri("/stockmarkets")
//            .body(Mono.just(market), StockMarket.class)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .returnResult(StockMarket.class)
//            .getResponseBody()
//            .blockFirst()
//            .getId();
//
//        UUID importedStockPrices = webTestClient
//            .get()
//            .uri("/stockmarkets/" + createdUuid + "/prices?from=\"2020-11-01\"&to=\"2020-11-10\"")
//            .body(Mono.just(market), StockMarket.class)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .returnResult(StockMarket.class)
//            .getResponseBody()
//            .blockFirst()
//            .getId();

    }
}

