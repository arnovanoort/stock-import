package nl.arnovanoort.stockreader.client;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
public class TiingoStockTest {

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Test
  public void testParseCSV() throws IOException {
    String location = new ClassPathResource(
        "tickerInfo/supported_tickers.csv",
        this.getClass().getClassLoader())
        .getFile().getPath();
    Path path = Paths.get(location);

    Flux<String> csvWithHeader = Flux.using(
        () -> Files.lines(path),
        Flux::fromStream,
        Stream::close
    );

    // skip header line
    Flux<String> csvFlux = csvWithHeader.skip(1);

    Flux<TiingoStock> tingoStocks = csvFlux.map(line -> TiingoStock.fromCSV(line));
    var x = tingoStocks.blockFirst();
    StepVerifier.create(tingoStocks)
        .assertNext(stock -> {
          Assert.assertEquals("AAPL"      , stock.getTicker());
          Assert.assertEquals("NASDAQ"    , stock.getStockMarket());
          Assert.assertEquals("Stock"     , stock.getAssetType());
          Assert.assertEquals("USD"       , stock.getCurrency());
          Assert.assertEquals("1980-12-12", formatter.format(stock.getDateListed().get()));
          Assert.assertEquals("2020-12-10", formatter.format(stock.getDateUnListed().get()));
        })
        .assertNext(stock -> {
          Assert.assertEquals("NFLX"      , stock.getTicker());
          Assert.assertEquals("NASDAQ"    , stock.getStockMarket());
          Assert.assertEquals("Stock"     , stock.getAssetType());
          Assert.assertEquals("USD"       , stock.getCurrency());
          Assert.assertEquals("2002-05-23", formatter.format(stock.getDateListed().get()));
          Assert.assertTrue(  stock.getDateUnListed().isEmpty());
        })
        .assertNext(stock -> {
          Assert.assertEquals("TSLA"      , stock.getTicker());
          Assert.assertEquals("NASDAQ"    , stock.getStockMarket());
          Assert.assertEquals("Stock"     , stock.getAssetType());
          Assert.assertEquals("USD"       , stock.getCurrency());
          Assert.assertTrue(  stock.getDateListed().isEmpty());
          Assert.assertTrue(  stock.getDateUnListed().isEmpty());
        })
//        .assertNext(stock -> {
//          Assert.assertEquals("TSLA"      , stock.getTicker());
//          Assert.assertEquals("NASDAQ"    , stock.getStockMarket());
//          Assert.assertEquals("Stock"     , stock.getAssetType());
//          Assert.assertEquals("USD"       , stock.getCurrency());
//          Assert.assertTrue(  stock.getDateListed().isEmpty());
//          Assert.assertTrue(  stock.getDateUnListed().isEmpty());
//        })
        .verifyComplete();
  }
}
