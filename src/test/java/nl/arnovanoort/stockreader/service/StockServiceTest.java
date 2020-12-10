package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPrizeRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
public class StockServiceTest {

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
    StockPrizeRepository stockPrizeRepository;

    @MockBean
    StockService stockService;

    @Test
    public void updateStock() {
        Date today = new Date();
        UUID amazonStockUuid = UUID.randomUUID();
        UUID netflixStockUuid = UUID.randomUUID();
        UUID marketUuid = UUID.randomUUID();
        StockMarket nasdaq = new StockMarket(marketUuid, "NASDAQ");
        Stock amazon = new Stock(UUID.randomUUID(), "AMAZON", "AMZN", nasdaq.getId());
        Stock netflix = new Stock(UUID.randomUUID(), "NETFLIX", "NFLX", nasdaq.getId());
        StockPrice amazonPrize = new StockPrice(1f, 2f, 3f, 4f, LocalDateTime.of(2020, 11, 16, 1, 2), amazonStockUuid);
        StockPrice netflixPrize = new StockPrice(5f, 6f, 7f, 8f, LocalDateTime.of(2020, 11, 15, 3, 4), netflixStockUuid);

        when(stockMarketRepository.findAll()).thenReturn(Flux.just(nasdaq));
        when(stockMarketRepository.getStocksByMarket(marketUuid)).thenReturn(Flux.just(amazon, netflix));
        when(stockService.updateStockPrize(amazon, today, today)).thenReturn(Flux.just(amazonPrize));
        when(stockService.updateStockPrize(netflix, today, today)).thenReturn(Flux.just(netflixPrize));

        Flux<StockPrice> stockprizes = stockMarketService.updateStockPrizes(today, today);

        Assert.assertTrue(stockprizes.any(stockPrize -> stockPrize.getStockId() == amazonStockUuid).block());
        Assert.assertTrue(stockprizes.any(stockPrize -> stockPrize.getStockId() == netflixStockUuid).block());
    }
}
