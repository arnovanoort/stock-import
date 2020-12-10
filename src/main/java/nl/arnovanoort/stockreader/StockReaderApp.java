package nl.arnovanoort.stockreader;

import nl.arnovanoort.stockreader.client.TiingoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import javax.inject.Inject;

@SpringBootApplication
public class StockReaderApp {

//    @Inject
//    TiingoClient tiingoClient;

    public static void main(String[] args) {
        SpringApplication.run(StockReaderApp.class, args);
    }


}
