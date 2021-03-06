package nl.arnovanoort.stockreader.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@ToString
@EqualsAndHashCode
public class StockMarket {

    @Id
    @Getter @Setter
    private UUID id;

    @Getter @Setter
    String name;

    @Getter @Setter
    String client;

    public StockMarket(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.client = "tiingo";
    }
}

