package nl.arnovanoort.stockreader.controller;


import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.service.StockIntegrationTestData;
import nl.arnovanoort.stockreader.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(StockController.class)
public class StockControllerTest implements StockIntegrationTestData {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  private StockService stockServiceMock;

  @MockBean
  private MultipartReader multipartReader;

  DateTimeFormatter listedDateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Test
  public void testGetStockByUuid(){
    Stock amazonStock = amazonStock(UUID.randomUUID(), UUID.randomUUID());

    when(stockServiceMock.getStock(amazonStock.getId()))
        .thenReturn(Mono.just(amazonStock));

    webTestClient
        .get()
        .uri("/stocks/{uuid}", amazonStock.getId())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Amazon")
        .jsonPath("$.ticker").isEqualTo("AMZ")
        .jsonPath("$.assetType").isEqualTo("Stock")
        .jsonPath("$.currency").isEqualTo("Dollar")
        .jsonPath("$.dateListedNullable").isEqualTo(localDateToday.format(listedDateformatter))
        .jsonPath("$.dateUnListedNullable").isEqualTo(localDateToday.format(listedDateformatter))
        .jsonPath("$.stockMarketId").isEqualTo(amazonStock.getStockMarketId().toString())
    ;
  }

  @Test
  public void testCreateStock(){
    Stock inputAmazonStock = amazonStock();
    Stock returnAmazonStock = amazonStock(UUID.randomUUID(), UUID.randomUUID());

    when(stockServiceMock.createStock(inputAmazonStock))
        .thenReturn(Mono.just(returnAmazonStock));

    webTestClient
        .post()
        .uri("/stocks")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(inputAmazonStock), Stock.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  public void testImportLocal(){

    when(stockServiceMock.importStocksLocal())
        .thenReturn(Flux.just(amazonStock()));

    webTestClient
        .post()
        .uri("/stocks/importlocal")
        .contentType(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isCreated();


    verify(stockServiceMock).importStocksLocal();
  }

}
