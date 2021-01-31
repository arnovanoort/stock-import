package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.client.TingoStockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@RunWith(SpringRunner.class)
public class StockServiceTest implements StockIntegrationTestData{

    @TestConfiguration
    static class StockServiceTestConfiguration {

        @Bean
        public StockService stockService() {
            return new StockServiceImpl();
        }
    }

    @Autowired
    StockService stockService;

    @MockBean
    TiingoClient tiingoClient;

    @MockBean
    StockRepository stockRepository;

    @MockBean
    StockMarketRepository stockMarketRepository;

    @MockBean
    StockPriceRepository stockPriceRepository;

    @Test
    public void importStockPrice(){
        Stock amazon = new Stock(UUID.randomUUID(), "AMAZON", "AMZN", "Stock", "EUR", localDateToday, localDateToday, existingNasdaqStockMarket.getId());
        Stock netflix = new Stock(UUID.randomUUID(), "NETFLIX", "NFLX", "Stock", "EUR", localDateToday, localDateToday, existingNasdaqStockMarket.getId());
//        TiingoStock netflixTiingoStock = new TiingoStock(netflix.getTicker(), "NASDAQ", netflix.getAssetType(), netflix.getCurrency(), netflix.getDateListed(), netflix.getDateUnListed());
        TingoStockPrice netflixTiingoStockPrice = new TingoStockPrice(netflixStockPrice.getOpen(), netflixStockPrice.getClose(), netflixStockPrice.getHigh(), netflixStockPrice.getLow(), netflixStockPrice.getVolume(), localDateTimeToday);

        when(stockPriceRepository.get(amazon.getId(), localDateToday)).thenReturn(Mono.just(amazonStockPrice));
        when(tiingoClient.importStockPrices(amazon.getTicker(), localDateToday, localDateToday)).thenReturn(Flux.just(netflixTiingoStockPrice));
        Flux<StockPrice> result = stockService.importStockPrices(amazon, localDateToday, localDateToday );

        StepVerifier.create(result)
            .assertNext(stockPrice -> Assertions.assertEquals(stockPrice.getId(), amazonStockPrice.getId()))
            .verifyComplete();
    }
}
