package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoClient;
import nl.arnovanoort.stockreader.client.TingoStockPrice;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.repository.StockMarketRepository;
import nl.arnovanoort.stockreader.repository.StockPriceRepository;
import nl.arnovanoort.stockreader.repository.StockRepository;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
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
    UuidGenerator uuidGenerator;

    @Test
    public void testCreateStock() throws Exception {
        // prepare
        var newAmazonStock = newAmazonStock();
        var existingAmazonStock = existingAmazonStock();
        when(stockRepository
            .save(newAmazonStock))
            .thenReturn(Mono.just(existingAmazonStock));

        // execute
        var result = stockService.createStock(newAmazonStock);

        // verify
        StepVerifier.create(result)
            .assertNext(stock -> Assert.assertEquals(stock, existingAmazonStock))
            .verifyComplete();
    }

    @Test
    public void testGetStock() throws Exception {
        // prepare
        var existingAmazonStock = existingAmazonStock();
        when(stockRepository
            .findById(existingAmazonStock.getId()))
            .thenReturn(Mono.just(existingAmazonStock));

        // execute
        var result = stockService.getStock(existingAmazonStock.getId());

        // verify
        StepVerifier.create(result)
            .assertNext(stock -> Assert.assertEquals(stock, existingAmazonStock))
            .verifyComplete();
    }

    @Test
    public void testGetStockByTicker() throws Exception {
        // prepare
        var amazonStock = existingAmazonStock();
        when(stockRepository
                .findStockByTicker(amazonStock.getTicker()))
                .thenReturn(Mono.just(amazonStock));

        // execute
        var result = stockService.findStockByTicker(amazonStock.getTicker());

        // verify
        StepVerifier.create(result)
                .assertNext(stock -> Assert.assertEquals(stock, amazonStock))
                .verifyComplete();
    }

    @Test
    public void testGetStockByStockMarket() throws Exception {
        // prepare
        var amazonStock = existingAmazonStock();
        when(stockRepository
                .getStocksByMarket(amazonStock.getStockMarketId()))
                .thenReturn(Flux.just(amazonStock));

        // execute
        var result = stockService.findStocksByStockMarket(amazonStock.getStockMarketId());

        // verify
        StepVerifier.create(result)
                .assertNext(stock -> Assert.assertEquals(stock, amazonStock))
                .verifyComplete();
    }

    @Test
    public void testImportExistingStock() throws IOException {
        // prepare
        var amazonStock = existingAmazonStock();
        when(stockMarketService.createStockMarket(newNasdaqStockMarket)).thenReturn(Mono.just(existingNasdaqStockMarket));
        when(stockRepository.findStockByTicker("AMZ")).thenReturn(Mono.just(amazonStock));

        var input = Flux.fromArray(new String[]{
            "ticker,exchange,assetType,priceCurrency,startDate,endDate",
            "AMZ,NASDAQ,Stock,USD,1980-12-12,2020-12-10"
        });
        // Execute
        Flux<Stock> stockTickers = stockService.importStocks(input);

        // verify
        StepVerifier
            .create(stockTickers)
            .expectNext(amazonStock)
            .verifyComplete();
    }

    @Test
    public void testImportNewStock() throws IOException {
        // prepare
        var amazonStock = existingAmazonStock();
        when(stockMarketService.createStockMarket(newNasdaqStockMarket)).thenReturn(Mono.just(existingNasdaqStockMarket));
        when(stockRepository.findStockByTicker("AMZ")).thenReturn(Mono.empty());
        when(stockRepository.create(
            amazonStock.getId(),
            "AMZ",
            "AMZ",
            "Stock",
            "USD",
            LocalDate.of(1980, 12, 12),
            LocalDate.of(2020, 12, 10),
            existingNasdaqStockMarket.getId())
        ).thenReturn(Mono.just(amazonStock));
        when(stockRepository.findStockByTicker("AMZ")).thenReturn(Mono.just(amazonStock));

        var input = Flux.fromArray(new String[]{
            "ticker,exchange,assetType,priceCurrency,startDate,endDate",
            "AMZ,NASDAQ,Stock,USD,1980-12-12,2020-12-10"
        });
        // Execute
        Flux<Stock> stockTickers = stockService.importStocks(input);

        // verify
        StepVerifier
            .create(stockTickers)
            .expectNext(amazonStock)
            .verifyComplete();
    }

    @Test
    public void importNewStockPrice(){
        // prepare
        Stock amazon = existingAmazonStock();
        when(tiingoClient.importStockPrices(amazon.getTicker(), localDateToday, localDateToday)).thenReturn(Flux.just(newAmazonTiingoStockPrice));
        when(stockPriceRepository.getByStockUuidAndDate(amazon.getId(), localDateToday)).thenReturn(Mono.empty());
        when(stockPriceRepository.save(amazonStockPrice)).thenReturn(Mono.just(amazonStockPrice));

        // Execute
        Flux<StockPrice> result = stockService.importStockPrices(amazon, localDateToday, localDateToday);

        // Verify
        StepVerifier.create(result)
            .assertNext(stockPrice -> Assertions.assertEquals(amazonStockPrice, stockPrice))
            .verifyComplete();

        verify(tiingoClient).importStockPrices(amazon.getTicker(), localDateToday, localDateToday);
        verify(stockPriceRepository).getByStockUuidAndDate(amazon.getId(), localDateToday);
    }

    @Test
    public void importDuplicateStockPrice(){
        // prepare
        Stock amazon = existingAmazonStock();
        when(tiingoClient.importStockPrices(amazon.getTicker(), localDateToday, localDateToday)).thenReturn(Flux.just(newAmazonTiingoStockPrice));
        when(stockPriceRepository.getByStockUuidAndDate(amazon.getId(), localDateToday)).thenReturn(Mono.just(amazonStockPrice));

        // Execute
        Flux<StockPrice> result = stockService.importStockPrices(amazon, localDateToday, localDateToday);

        // Verify
        StepVerifier.create(result).verifyComplete();

        verify(tiingoClient).importStockPrices(amazon.getTicker(), localDateToday, localDateToday);
        verify(stockPriceRepository).getByStockUuidAndDate(amazon.getId(), localDateToday);
    }
}
