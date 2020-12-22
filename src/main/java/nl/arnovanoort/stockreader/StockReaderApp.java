package nl.arnovanoort.stockreader;

import nl.arnovanoort.stockreader.client.TiingoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import javax.inject.Inject;

@SpringBootApplication
public class StockReaderApp {

    public static void main(String[] args) {
        SpringApplication.run(StockReaderApp.class, args);
    }

}


/*
onNext(StockPrice(id=5f50c23e-4f46-43ae-9b25-76121bd4e85c, open=10.56, close=10.55, high=10.7499, low=10.5, volume=11352, stockId=376ccd52-c3f9-4d52-a0b2-f87bb808769d, date=2020-12-14T00:00))
2020-12-23 00:09:52.834  WARN 139301 --- [tor-tcp-epoll-1] i.r.p.client.ReactorNettyClient          : Error: SEVERITY_LOCALIZED=ERROR, SEVERITY_NON_LOCALIZED=ERROR, CODE=22003, MESSAGE=numeric field overflow, DETAIL=A field with precision 10, scale 6 must round to an absolute value less than 10^4., FILE=numeric.c, LINE=6612, ROUTINE=apply_typmod
2020-12-23 00:09:52.843 ERROR 139301 --- [tor-tcp-epoll-1] reactor.Flux.SwitchIfEmpty.9667          : onError(org.springframework.r2dbc.BadSqlGrammarException: executeMany; bad SQL grammar [INSERT INTO stock_price (open, close, high, low, volume, stock_id, date) VALUES ($1, $2, $3, $4, $5, $6, $7)]; nested exception is io.r2dbc.postgresql.ExceptionFactory$PostgresqlBadGrammarException: [22003] numeric field overflow)
2020-12-23 00:09:52.848 ERROR 139301 --- [tor-tcp-epoll-1] reactor.Flux.SwitchIfEmpty.9667
 */