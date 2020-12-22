package nl.arnovanoort.stockreader.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TiingoClientTest {

    public static MockWebServer mockBackEnd;
    TiingoClient tiingoClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
    }

    @Test
    public void test_get_stock() throws JsonProcessingException {
        TingoConfig tingoConfig = new TingoConfig();
        tingoConfig.setHost("http://" + mockBackEnd.getHostName() + ":" + mockBackEnd.getPort());
        tingoConfig.setPath("/stock");

        tiingoClient = new TiingoClient(tingoConfig);

        Date today                  = new Date();
        UUID stockUuid              = UUID.randomUUID();
        ObjectMapper objectMapper   = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        TingoStockPrice resultPrice = new TingoStockPrice(
            1f,
            2f,
            3f,
            4f,
            100000l,
            LocalDateTime.of(2020, 8, 28, 00, 0, 0, 0)
        );
        String json = objectMapper.writeValueAsString(resultPrice);

        mockBackEnd.enqueue(
            new MockResponse()
                .setBody(objectMapper.writeValueAsString(resultPrice))
                .addHeader("Content-Type", "application/json"));

        Flux<TingoStockPrice> stockPriceMono = tiingoClient.getStockPrize("TSLA", today, today );

        StepVerifier.create(stockPriceMono)
            .expectNext(resultPrice)
            .verifyComplete();
    }
}
