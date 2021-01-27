package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.controller.StockController;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.repository.PostgresqlTestContainer;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockIntegrationTest implements StockIntegrationTestData{

    StockController stockController;
    StockMarket stockMarket;

    @Autowired
    private StockMarketService stockMarketService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMarketRepository stockMarketRepository;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    Flyway flyway;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = PostgresqlTestContainer.getInstance();


    @BeforeEach
    public void setup(){
        flyway.clean();
        flyway.migrate();
    }


    public StockIntegrationTest(){
        stockController = new StockController();
        stockController.setStockService(stockService);
        stockMarket = new StockMarket(null, "Nasdaq");
        postgreSQLContainer.start();
    }

    @Test
    public void testCreateNewStock() throws Exception {
        StockMarket newStockMarket = createTestStockMarket();
        Stock amazonStock = amazonStock(null, newStockMarket.getId());

        Flux<Stock> result = createStock(amazonStock);

        StepVerifier.create(result)
            .assertNext(createdStock -> {
                Assert.assertEquals(amazonStock.getName(), createdStock.getName());
            })
            .verifyComplete();
    }

    @Test
    public void testGetStock() throws Exception {
        StockMarket newStockMarket = createTestStockMarket();
        Stock amazonStock = new Stock(
            null,
            "Amazon",
            "AMZ",
            "Stock",
            "EUR",
            Optional.of(localDateToday),
            Optional.of(localDateToday),
            newStockMarket.getId());

        Stock createdStock = createStock(amazonStock).blockFirst();

        Flux<Stock> result = webTestClient
            .get()
            .uri("/stocks/" + createdStock.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Stock.class)
            .getResponseBody()
            ;

        StepVerifier.create(result)
            .assertNext(foundStock -> {
                Assert.assertEquals(amazonStock.getName(), foundStock.getName());
            })
            .verifyComplete();
    }

    @Test
    public void testImportStock() throws InterruptedException, IOException {
        // prepare data
        Flux<String> csvFlux = getTestFile("tickerInfo/supported_tickers.csv");
        List<String> resultList = Arrays.asList(new String[] { "AAPL", "NFLX", "TSLA" });

        // Execute method under test
        Mono<List<String>> stockTickers = stockService.importStocks(csvFlux).map(stock -> stock.getTicker()).collectSortedList();

        // verify
        StepVerifier.create(stockTickers).expectNext(resultList).verifyComplete();
    }

    @Test
    public void testImportStockWithEmptyDate() throws InterruptedException, IOException {
        // prepare data
        Flux<String> csvFlux = getTestFile("tickerInfo/supported_tickers_empty_dates.csv");

        // Execute method under test
        Flux<Stock> stock = stockService.importStocks(csvFlux);

        StepVerifier.create(stock)
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .assertNext(s -> {Assertions.assertEquals(s.getName(), "SMPP");})
            .verifyComplete();
    }


//    @Test
//    /* later gebruiken voor multipart example */
//    public void importStocks2() throws Exception {
//        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
//        multipartBodyBuilder.part("file", new ClassPathResource("tickerInfo/supported_tickers.csv"))
//            .contentType(MediaType.MULTIPART_FORM_DATA);
//
//        Flux<StockMarket> result = webTestClient.post()
//            .uri("/stocks/import")
//            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .returnResult(StockMarket.class)
//            .getResponseBody();
//
//        StepVerifier.create(result)
//            .assertNext(s -> {Assertions.assertEquals(s.getName(), "AAPL");})
//            .assertNext(s -> {Assertions.assertEquals(s.getName(), "NFLX");})
//            .assertNext(s -> {Assertions.assertEquals(s.getName(), "TSLA");})
//            .verifyComplete();
//    }

    @NotNull
    private Flux<Stock> createStock(Stock stock) {
        return webTestClient
            .post()
            .uri("/stocks")
            .body(Mono.just(stock), Stock.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .returnResult(Stock.class)
            .getResponseBody();
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

}

