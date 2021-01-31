package nl.arnovanoort.stockreader.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.arnovanoort.stockreader.domain.StockPrice;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/*

example tingo response
  {
    "adjClose": 2213.4,
    "adjHigh": 2318.49,
    "adjLow": 2186.52,
    "adjOpen": 2295.12,
    "adjVolume": 20081176,
    "close": 2213.4,
    "date": "T2020-08-28T00:00:00+00:00",
    "divCash": 0.0,
    "high": 2318.49,
    "low": 2186.52,
    "open": 2295.12,
    "splitFactor": 1.0,
    "volume": 20081176
  }
]
*
 */
@EqualsAndHashCode
public class TingoStockPrice {

    @Getter @Setter
    private Float open;
    @Getter @Setter
    private Float close;
    @Getter @Setter
    private Float high;
    @Getter @Setter
    private Float low;
    @Getter @Setter
    private Long volume;
    @Getter @Setter
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime date;

    public TingoStockPrice() {} // needed for deserialisation
    public TingoStockPrice(Float open, Float close, Float high, Float low, Long volume, LocalDateTime date) {
        this.open   = open;
        this.close  = close;
        this.high   = high;
        this.low    = low;
        this.volume = volume;
        this.date   = date;
    }

    public StockPrice toStockPrice(UUID stockId) {
        StockPrice price = new StockPrice(
            this.open,
            this.close,
            this.high,
            this.low,
            this.volume,
            this.date.toLocalDate(),
            stockId
        );
        return price;
    }
}
