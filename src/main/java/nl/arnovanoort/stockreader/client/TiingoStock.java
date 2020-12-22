package nl.arnovanoort.stockreader.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.arnovanoort.stockreader.domain.Stock;
import nl.arnovanoort.stockreader.domain.StockPrice;
import nl.arnovanoort.stockreader.exception.StockReaderException;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/*
    Mainly used to process import of tiingo stocks from csv
    example: AAPL,NASDAQ,Stock,USD,1980-12-12,2020-12-10
 */
@ToString
@EqualsAndHashCode
public class TiingoStock {

    @Getter @Setter
    private String ticker;
    @Getter @Setter
    private String stockMarket;
    @Getter @Setter
    private String assetType;
    @Getter @Setter
    private String currency;
    @Getter @Setter
    private Optional<LocalDate> dateListed;
    @Getter @Setter
    private Optional<LocalDate> dateUnListed;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TiingoStock(
        String ticker,
        String stockMarket,
        String assetType,
        String currency,
        Optional<LocalDate> dateListed,
        Optional<LocalDate> dateUnListed
    ) {
        this.ticker       = ticker;
        this.stockMarket  = stockMarket;
        this.assetType    = assetType;
        this.currency     = currency;
        this.dateListed   = dateListed;
        this.dateUnListed = dateUnListed;
    }

    public static TiingoStock fromCSV(String csv){
        String[] values = csv.split(",");
        return new TiingoStock(
            values[0],
            values[1],
            values[2],
            values[3],
            fetchIfExists(values, 4).map( date -> LocalDate.parse(date, formatter)),
            fetchIfExists(values, 5).map( date -> LocalDate.parse(date, formatter))
        );
    }

    private static <T> Optional<T> fetchIfExists(T[] list, int item){
        Optional<T> value;
        if(list.length > item) {
            value = Optional.ofNullable(list[item]);
        } else {
            value = Optional.empty();
        }
        return value;
    }

    public Stock toStock(UUID stockMarketId) {
        return new Stock(
            null,
            ticker,
            ticker,
            assetType,
            currency,
            dateListed,
            dateUnListed,
            stockMarketId
        );
    }
}
