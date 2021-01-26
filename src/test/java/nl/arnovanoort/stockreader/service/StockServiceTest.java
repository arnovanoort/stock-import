package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.client.TiingoStock;
import nl.arnovanoort.stockreader.client.TingoStockPrice;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPrizeRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.junit.Assert;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
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
    StockPrizeRepository stockPrizeRepository;

    @Test
    public void importStockPrice(){
        Date today = new Date();
        LocalDate localDateToday = LocalDate.now();
        LocalDateTime localDateTimeToday = LocalDateTime.now();
//        UUID amazonStockUuid = UUID.randomUUID();
//        UUID netflixStockUuid = UUID.randomUUID();
//        UUID marketUuid = UUID.randomUUID();
//        StockMarket nasdaq = new StockMarket(marketUuid, "NASDAQ");
        Stock amazon = new Stock(UUID.randomUUID(), "AMAZON", "AMZN", "Stock", "EUR", Optional.of(localDateToday), Optional.of(localDateToday), existingNasdaqStockMarket.getId());
        Stock netflix = new Stock(UUID.randomUUID(), "NETFLIX", "NFLX", "Stock", "EUR", Optional.of(localDateToday), Optional.of(localDateToday), existingNasdaqStockMarket.getId());
//        StockPrice amazonPrize = new StockPrice(1f, 2f, 3f, 4f, 100000l, LocalDateTime.of(2020, 11, 16, 1, 2), amazonStockUuid);
//        StockPrice netflixPrize = new StockPrice(5f, 6f, 7f, 8f, 200000l, LocalDateTime.of(2020, 11, 15, 3, 4), netflixStockUuid);
        TiingoStock netflixTiingoStock = new TiingoStock(netflix.getTicker(), "NASDAQ", netflix.getAssetType(), netflix.getCurrency(), netflix.getDateListed(), netflix.getDateUnListed());
        TingoStockPrice netflixTiingoStockPrice = new TingoStockPrice(netflixPrize.getOpen(), netflixPrize.getClose(), netflixPrize.getHigh(), netflixPrize.getLow(), netflixPrize.getVolume(), localDateTimeToday);

        when(stockPrizeRepository.get(amazon.getId(), today)).thenReturn(Mono.just(amazonStockPrice));
        when(tiingoClient.getStockPrize(amazon.getTicker(), today, today)).thenReturn(Flux.just(netflixTiingoStockPrice));
        Flux<StockPrice> result = stockService.updateStockPrize(amazon, today, today );

        StepVerifier.create(result)
            .assertNext(stockPrice -> Assertions.assertEquals(stockPrice.getId(), amazonStockPrice.getId()))
            .verifyComplete();
    }
}
