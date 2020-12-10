package nl.arnovanoort.stockreader.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode
public class StockPrice {

    @Id
    @Getter @Setter
    private UUID            id;
    @Getter @Setter
    private Float           open;
    @Getter @Setter
    private Float           close;
    @Getter @Setter
    private Float           high;
    @Getter @Setter
    private Float           low;
    @Getter @Setter
    private UUID            stockId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime   date;

    public StockPrice() {} // needed for deserialisation
    public StockPrice(
        Float open,
        Float close,
        Float high,
        Float low,
        LocalDateTime date,
        UUID stockId)
    {
        this.open       = open;
        this.close      = close;
        this.high       = high;
        this.low        = low;
        this.date       = date;
        this.stockId    = stockId;
    }
}
