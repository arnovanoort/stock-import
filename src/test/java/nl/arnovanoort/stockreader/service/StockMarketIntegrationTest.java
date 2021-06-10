package nl.arnovanoort.stockreader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.arnovanoort.stockreader.controller.StockMarketController;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.exception.StockReaderException;
import nl.arnovanoort.stockreader.repository.PostgresqlTestContainer;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.flywaydb.core.Flyway;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockMarketIntegrationTest implements StockIntegrationTestData{

    StockMarketController stockMarketController;
    StockMarket market;
    static ObjectMapper objectMapper;

    @Autowired
    private StockMarketService stockMarketService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMarketRepository stockMarketRepository;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Autowired
    WebTestClientWrapper webTestClientWrapper;

    @Autowired
    Flyway flyway;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = PostgresqlTestContainer.getInstance();

    @BeforeEach
    public void setup(){
        flyway.clean();
        flyway.migrate();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
    }

    public StockMarketIntegrationTest(){
        market                = new StockMarket(null, nasdaq);
        stockMarketController = new StockMarketController();
        stockMarketController.setStockMarketService(stockMarketService);
        postgreSQLContainer.start();

    }

    @Test
    public void createNewStockMarket() throws Exception {
        FluxExchangeResult<StockMarket> result = webTestClientWrapper.getClient()
            .post()
            .uri("/stockmarkets")
            .body(Mono.just(market), StockMarket.class)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(StockMarket.class);

        StepVerifier.create(result.getResponseBody())
            .assertNext(stockMarket -> Assert.assertEquals(nasdaq, stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void getStockMarketByUuid() throws Exception {
        UUID createdUuid = webTestClientWrapper.getClient()
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

        FluxExchangeResult<StockMarket> result = webTestClientWrapper.getClient()
            .get()
            .uri("/stockmarkets/" + createdUuid.toString())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(StockMarket.class);

        StepVerifier.create(result.getResponseBody())
            .assertNext(stockMarket -> Assert.assertEquals(nasdaq, stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void getStocksByStockMarket() throws Exception {
        UUID createdUuid = webTestClientWrapper.getClient()
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

        FluxExchangeResult<StockMarket> result = webTestClientWrapper.getClient()
            .get()
            .uri("/stockmarkets/" + createdUuid.toString())
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(StockMarket.class);

        StepVerifier.create(result.getResponseBody())
            .assertNext(stockMarket -> Assert.assertEquals(nasdaq, stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void importStockPrices(){
        try{
            // prepare
            StockMarket newStockMarket  = webTestClientWrapper.createTestStockMarket();
            Stock amazonStock           = webTestClientWrapper.createStock(amazonStock(null, newStockMarket.getId())).block();

            MockWebServer mockBackEnd   = new MockWebServer();
            mockBackEnd.start(9999);
            mockBackEnd.enqueue(
                new MockResponse()
                    .setBody(objectMapper.writeValueAsString(newAmazonTiingoStockPrice))
                    .addHeader("Content-Type", "application/json"));

            // execute
            webTestClientWrapper.getClient()
            .post()
            .uri("/stockmarkets/" + newStockMarket.getId() + "/prices?from=2021-02-02&to=2021-02-04")
            .exchange()
            .expectStatus()
            .isCreated();

            mockBackEnd.shutdown();

            // validate
            Flux<StockPrice> result = stockPriceRepository.getByStockUuidAndDateWindow(amazonStock.getId(), localDateToday.minusDays(1), localDateToday.plusDays(1));

            // verify
            StepVerifier.create(result).assertNext(stockPrice -> {
                Assert.assertEquals(stockPrice.getHigh()    , amazonStockPrice.getHigh());
                Assert.assertEquals(stockPrice.getClose()   , amazonStockPrice.getClose());
                Assert.assertEquals(stockPrice.getLow()     , amazonStockPrice.getLow());
                Assert.assertEquals(stockPrice.getOpen()    , amazonStockPrice.getOpen());
                Assert.assertEquals(stockPrice.getVolume()  , amazonStockPrice.getVolume());
                Assert.assertEquals(stockPrice.getStockId() , amazonStock.getId());
            }).verifyComplete();
        } catch(Exception e){
            e.printStackTrace();
            throw new StockReaderException("Error trying to mock tiingo client", e);
        }
    }
}

