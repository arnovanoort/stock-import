package nl.arnovanoort.stockreader.repository;

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
import nl.arnovanoort.stockreader.service.StockIntegrationTestData;
import nl.arnovanoort.stockreader.service.StockMarketService;
import nl.arnovanoort.stockreader.service.StockService;
import nl.arnovanoort.stockreader.service.WebTestClientWrapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class StockMarketRepositoryTest implements StockIntegrationTestData {

    StockMarket market;
    static ObjectMapper objectMapper;

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

    public StockMarketRepositoryTest(){
        market                = new StockMarket(null, nasdaq);
        postgreSQLContainer.start();

    }
}

