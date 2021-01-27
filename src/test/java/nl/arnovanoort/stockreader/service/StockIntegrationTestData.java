package nl.arnovanoort.stockreader.service;

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
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface StockIntegrationTestData {

  LocalDate localDateToday = LocalDate.now();
  LocalDateTime localDateTimeToday = LocalDateTime.now();


  UUID amazonStockUuid = UUID.randomUUID();
  UUID netflixStockUuid = UUID.randomUUID();

  default Stock amazonStock(UUID stockUuid, UUID stockMarketUuid) {
    return new Stock(
        stockUuid,
        "Amazon",
        "AMZ",
        "Stock",
        "Dollar",
        Optional.of(localDateToday),
        Optional.of(localDateToday),
        stockMarketUuid);
  }

  StockMarket newNasdaqStockMarket = new StockMarket(null, "Nasdaq");

  StockMarket existingNasdaqStockMarket = new StockMarket(UUID.randomUUID(), "Nasdaq");

  default Stock amazonStock(){
    return amazonStock(null, UUID.randomUUID());
  }

  default Stock amazonStock(UUID stockUuid){
    return amazonStock(stockUuid, UUID.randomUUID());
  }

  public StockPrice amazonStockPrice = new StockPrice(
      1f,
      2f,
      3f,
      4f,
      100000l,
      LocalDateTime.of(2020, 11, 16, 1, 2),
      amazonStockUuid
  );

  StockPrice netflixPrize = new StockPrice(
      5f,
      6f,
      7f,
      8f,
      200000l,
      LocalDateTime.of(2020, 11, 15, 3, 4),
      netflixStockUuid
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
