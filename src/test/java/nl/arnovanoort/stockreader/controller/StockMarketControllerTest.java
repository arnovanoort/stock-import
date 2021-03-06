package nl.arnovanoort.stockreader.controller;


import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.service.StockIntegrationTestData;
import nl.arnovanoort.stockreader.service.StockMarketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(StockMarketController.class)
public class StockMarketControllerTest implements StockIntegrationTestData {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  private StockMarketService stockMarketServiceMock;

  @Test
  public void testGetStockMarketByUuid(){

    when(stockMarketServiceMock.getStockMarket(existingNasdaqStockMarket.getId()))
        .thenReturn(Mono.just(existingNasdaqStockMarket));

    webTestClient
        .get()
        .uri("/stockmarkets/{uuid}", existingNasdaqStockMarket.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo(nasdaq)
        .jsonPath("$.client").isEqualTo("tiingo")
    ;
  }

  @Test
  public void testCreateStock(){
    when(stockMarketServiceMock.createStockMarket(newNasdaqStockMarket))
        .thenReturn(Mono.just(existingNasdaqStockMarket));

    webTestClient
        .post()
        .uri("/stockmarkets")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(newNasdaqStockMarket), StockMarket.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("name").isEqualTo(existingNasdaqStockMarket.getName()
        );
  }

  @Test
  public void importStockPrices(){
    when(stockMarketServiceMock.importStockPrices(existingNasdaqStockMarket.getId(), localDateToday, localDateToday))
        .thenReturn(Flux.just(amazonStockPrice,amazonStockPrice));

    webTestClient
        .post()
        .uri("/stockmarkets/{uuid}/prices", existingNasdaqStockMarket.getId())
        .contentType(MediaType.APPLICATION_JSON)
        //.body(Mono.just(newNasdaqStockMarket), StockMarket.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .isEmpty()
    ;
  }
}
