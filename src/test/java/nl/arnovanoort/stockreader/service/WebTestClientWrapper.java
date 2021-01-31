package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebTestClientWrapper implements StockIntegrationTestData{

  @Autowired
  WebTestClient webTestClient;

  @NotNull
  public Mono<Stock> createStock(Stock stock) {
    return webTestClient
        .post()
        .uri("/stocks")
        .body(Mono.just(stock), Stock.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .returnResult(Stock.class)
        .getResponseBody()
        .next();
  }

  public StockMarket createTestStockMarket() {
    return webTestClient
        .post()
        .uri("/stockmarkets")
        .body(Mono.just(newNasdaqStockMarket), StockMarket.class)
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(StockMarket.class)
        .getResponseBody()
        .blockFirst();
  }

  public org.springframework.test.web.reactive.server.WebTestClient getClient(){
    return webTestClient;
  }
}
