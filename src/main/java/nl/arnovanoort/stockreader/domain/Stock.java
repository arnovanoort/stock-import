package nl.arnovanoort.stockreader.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Stock {

    @Id
    @Getter @Setter
    private UUID id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String ticker;
    @Getter @Setter
    private UUID stockMarketId;

    public Stock(UUID id, String name, String ticker, UUID stockMarketId) {
        this.id = id;
        this.name = name;
        this.ticker = ticker;
        this.stockMarketId = stockMarketId;
    }

}
