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
