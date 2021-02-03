package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StockMarketServiceTest implements StockIntegrationTestData{

    @TestConfiguration
    static class StockServiceTestConfiguration {

        @Bean
        public StockMarketService stockMarketService() {
            return new StockMarketServiceImpl();
        }
    }

    @Autowired
    StockMarketService stockMarketService;

    @MockBean
    TiingoClient tiingoClient;

    @MockBean
    StockRepository stockRepository;

    @MockBean
    StockMarketRepository stockMarketRepository;

    @MockBean
    StockPriceRepository stockPriceRepository;

    @MockBean
    StockService stockService;

    @Test
    public void createStockMarket() {
        // prepare
        when(stockMarketRepository.findByName(nasdaq)).thenReturn(Mono.empty());
        when(stockMarketRepository.create(null, newNasdaqStockMarket.getName(), "tiingo")).thenReturn(Mono.just(existingNasdaqStockMarket));
        when(stockMarketRepository.findByName(nasdaq)).thenReturn(Mono.just(existingNasdaqStockMarket));

        // Execute
        Mono<StockMarket> stockMarketResult = stockMarketService.createStockMarket(newNasdaqStockMarket);

        // verify
        StepVerifier
            .create(stockMarketResult).assertNext(stockMarket -> {
                Assert.assertEquals(stockMarket.getName(), newNasdaqStockMarket.getName());
                })
            .verifyComplete();
    }

    // move to repository test
    @Test
    public void getStockMarket() throws Exception {

        // prepare
        when(stockMarketRepository
            .findById(existingNasdaqStockMarket.getId()))
            .thenReturn(Mono.just(existingNasdaqStockMarket));

        // execute
        var result = stockMarketService.getStockMarket(existingNasdaqStockMarket.getId());

        // verify
        StepVerifier.create(result)
            .assertNext(stockMarket -> Assert.assertEquals(existingNasdaqStockMarket, stockMarket))
            .verifyComplete();
    }

    @Test
    public void getStockMarketByName() throws Exception {
        // prepare
        when(stockMarketRepository.findByName(nasdaq)).thenReturn(Mono.just(existingNasdaqStockMarket));

        // execute
        Mono<StockMarket> foundMarket = stockMarketService.createStockMarket(newNasdaqStockMarket)
            .flatMap(sm -> {
                return stockMarketRepository.findByName(sm.getName());
            });

        // verify
        StepVerifier.create(foundMarket)
            .assertNext(stockMarket -> Assert.assertEquals(nasdaq, stockMarket.getName()))
            .verifyComplete();
    }

    @Test
    public void importStock() {
        // Prepare
        when(stockMarketRepository.findAll()).thenReturn(Flux.just(existingNasdaqStockMarket));
        when(stockMarketRepository.getStocksByMarket(stockMarketUuid)).thenReturn(Flux.just(existingAmazonStock(), existingNetflixStock()));
        when(stockService.importStockPrices(existingAmazonStock(), localDateToday, localDateToday)).thenReturn(Flux.just(amazonStockPrice));
        when(stockService.importStockPrices(existingNetflixStock(), localDateToday, localDateToday)).thenReturn(Flux.just(netflixStockPrice));

        // Execute
        Flux<StockPrice> stockprizes = stockMarketService.importStockPrices(localDateToday, localDateToday);

        // verify
        Assert.assertTrue(stockprizes.any(stockPrize -> stockPrize.getStockId() == amazonStockUuid).block());
        Assert.assertTrue(stockprizes.any(stockPrize -> stockPrize.getStockId() == netflixStockUuid).block());
    }
}
