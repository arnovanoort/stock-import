package nl.arnovanoort.stockreader.controller;


import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.service.StockIntegrationTestData;
import nl.arnovanoort.stockreader.service.StockService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
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
    Stock amazonStock = existingAmazonStock();

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
    Stock inputAmazonStock = newAmazonStock();
    Stock returnAmazonStock = existingAmazonStock();

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
        .thenReturn(Flux.just(newAmazonStock()));

    webTestClient
        .post()
        .uri("/stocks/importlocal")
        .contentType(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isCreated();


    verify(stockServiceMock).importStocksLocal();
  }

  @Test
  public void testGetStockPrices(){
    var from = LocalDate.of(2021, 04,30);
//    var to = LocalDate.of(2021, 05,31);
    var today = LocalDate.now();
    when(stockServiceMock.getStockPrices(existingAmazonStock().getId(),from, today))
            .thenReturn(Flux.just(amazonStockPrice));

    webTestClient
      .get()
      .uri("/stocks/{uuid}/prices?from=2021-04-30", existingAmazonStock().getId())
//              .uri("/stocks/{uuid}/prices?from=2021-04-30", existingAmazonStock().getId())
//            .uri("/stocks/{uuid}/prices", existingAmazonStock().getId())
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody()
      .jsonPath("$[0].['open']").isEqualTo(1f)
      .jsonPath("$[0].['close']").isEqualTo(2f)
      .jsonPath("$[0].['high']").isEqualTo(3f)
      .jsonPath("$[0].['low']").isEqualTo(4f)
      .jsonPath("$[0].['date']").isEqualTo(today.toString())
      .jsonPath("$[0].['stockId']").isEqualTo(existingAmazonStock().getId().toString())
    ;

//    webTestClient
//            .post()
//            .uri("/stocks/{uuid}/prices?from=2021-04-30&to=2021-05-31", existingAmazonStock().getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            //.body(Mono.just(newNasdaqStockMarket), StockMarket.class)
//            .exchange()
//            .expectStatus()
//            .isCreated()
//            .expectBody()
//            .isEmpty()
//    ;
  }

}
