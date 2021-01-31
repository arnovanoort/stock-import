package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
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

        // Prepare
        when(stockMarketRepository.save(newNasdaqStockMarket)).thenReturn(Mono.just(existingNasdaqStockMarket));

        // Execute
        Mono<StockMarket> stockMarketResult = stockMarketService.createStockMarket(newNasdaqStockMarket);

        // verify
        StepVerifier
            .create(stockMarketResult).assertNext(stockMarket -> {
                Assert.assertEquals(stockMarket.getName(), newNasdaqStockMarket.getName());
                })
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
