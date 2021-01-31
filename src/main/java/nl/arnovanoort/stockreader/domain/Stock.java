package nl.arnovanoort.stockreader.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {

    @Id
    @Getter @Setter
    private UUID id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String ticker;
    @Getter @Setter
    String assetType;
    @Getter @Setter
    String currency;
    @Getter @Setter
    @Column("date_listed")
    LocalDate dateListedNullable;
    @Getter @Setter
    @Column("date_unlisted")
    LocalDate dateUnListedNullable;
    @Getter @Setter
    private UUID stockMarketId;

    public Stock(UUID id,
                 String name,
                 String ticker,
                 String assetType,
                 String currency,
                 LocalDate dateListedNullable,
                 LocalDate dateUnListedNullable,
                 UUID stockMarketId) {
        this.id                     = id;
        this.name                   = name;
        this.ticker                 = ticker;
        this.assetType              = assetType;
        this.currency               = currency;
        this.dateListedNullable     = dateListedNullable;
        this.dateUnListedNullable   = dateUnListedNullable;
        this.stockMarketId          = stockMarketId;
    }
}
