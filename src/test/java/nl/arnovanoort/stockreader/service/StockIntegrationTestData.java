package nl.arnovanoort.stockreader.service;

import nl.arnovanoort.stockreader.client.TiingoStock;
import nl.arnovanoort.stockreader.client.TingoStockPrice;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockMarket;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public interface StockIntegrationTestData {

  public static String nasdaq           = "NASDAQ";
  LocalDate localDateToday              = LocalDate.now();
  LocalDateTime localDateTimeToday      = LocalDateTime.now();

  UUID stockMarketUuid                  = UUID.randomUUID();
  UUID amazonStockUuid                  = UUID.randomUUID();
  UUID netflixStockUuid                 = UUID.randomUUID();

  StockMarket newNasdaqStockMarket      = new StockMarket(null, nasdaq);
  StockMarket existingNasdaqStockMarket = new StockMarket(stockMarketUuid, nasdaq);

  default Stock newAmazonStock(){
    return amazonStock(null, stockMarketUuid);
  }
  default Stock existingAmazonStock(UUID stockUuid){ return amazonStock(stockUuid, stockMarketUuid); }
  default Stock existingAmazonStock(){ return amazonStock(amazonStockUuid, stockMarketUuid); }
  default Stock amazonStock(UUID stockUuid, UUID stockMarketUuid) {
    return new Stock(
        stockUuid,
        "Amazon",
        "AMZ",
        "Stock",
        "Dollar",
        localDateToday,
        localDateToday,
        stockMarketUuid);
  }

  default Stock newNetflixStock(){
    return netflixStock(null, stockMarketUuid);
  }
  default Stock existingNetflixStock(UUID stockUuid){ return netflixStock(stockUuid, stockMarketUuid); }
  default Stock existingNetflixStock(){ return netflixStock(netflixStockUuid, stockMarketUuid); }
  default Stock netflixStock(UUID stockUuid, UUID stockMarketUuid) {
    return new Stock(
        stockUuid,
        "NETFLIX",
        "NFLX",
        "Stock",
        "EUR",
        localDateToday,
        localDateToday,
        stockMarketUuid
    );
  }

  public StockPrice amazonStockPrice = new StockPrice(
      1f,
      2f,
      3f,
      4f,
      100000l,
      localDateToday,
      amazonStockUuid
  );

  default StockPrice newAmazonStockPrice(UUID stockUuid){
    return new StockPrice(
        1f,
        2f,
        3f,
        4f,
        100000l,
        localDateToday,
        stockUuid
    ) ;
  }

  TingoStockPrice newAmazonTiingoStockPrice = new TingoStockPrice(
      1f,
      2f,
      3f,
      4f,
      100000l,
      localDateTimeToday
  );


  StockPrice netflixStockPrice = new StockPrice(
      5f,
      6f,
      7f,
      8f,
      200000l,
      localDateToday,
      netflixStockUuid
  );

  TingoStockPrice netflixTiingoStockPrice = new TingoStockPrice(
      netflixStockPrice.getOpen(),
      netflixStockPrice.getClose(),
      netflixStockPrice.getHigh(),
      netflixStockPrice.getLow(),
      netflixStockPrice.getVolume(),
      localDateTimeToday
  );


  default Flux<String> getTestFile(String fileLocation) throws IOException {
    String location = new ClassPathResource(
        fileLocation,
        this.getClass().getClassLoader())
        .getFile().getPath();
    Path path = Paths.get(location);

    return Flux.using(
        () -> Files.lines(path),
        Flux::fromStream,
        Stream::close
    );
  }
}
