package nl.arnovanoort.stockreader.controller;


import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.service.StockIntegrationTestData;
import nl.arnovanoort.stockreader.service.StockMarketService;
import nl.arnovanoort.stockreader.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(StockMarketController.class)
public class StockMarketControllerTest implements StockIntegrationTestData {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  private StockMarketService stockMarketServiceMock;

//  DateTimeFormatter listedDateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        .jsonPath("$.name").isEqualTo("Nasdaq")
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

    Date date = new Date(2021,1, 25);

    when(stockMarketServiceMock.updateStockPrices(existingNasdaqStockMarket.getId(), date, date))
        .thenReturn(Flux.just(amazonStockPrice,amazonStockPrice));

    webTestClient
        .post()
        .uri("/stockmarkets/{uuid}/prices?from=2021-01-25&to=2021-01-25", existingNasdaqStockMarket.getId())
        .contentType(MediaType.APPLICATION_JSON)
        //.body(Mono.just(newNasdaqStockMarket), StockMarket.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .isEmpty()
    ;
  }

}
