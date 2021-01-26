package nl.arnovanoort.stockreader.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ToString
@EqualsAndHashCode
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
                 Optional<LocalDate> dateListedNullable,
                 Optional<LocalDate> dateUnListedNullable,
                 UUID stockMarketId) {
        this.id                     = id;
        this.name                   = name;
        this.ticker                 = ticker;
        this.assetType              = assetType;
        this.currency               = currency;
        this.dateListedNullable     = fixDate(dateListedNullable);
        this.dateUnListedNullable   = fixDate(dateUnListedNullable);
        this.stockMarketId          = stockMarketId;
    }

    private LocalDate fixDate(Optional<LocalDate> date) {
        if(date == null){
            return null;
        } else {
            return date.orElse(null);
        }
    }

    public Optional<LocalDate> getDateListed(){
        return Optional.ofNullable(dateListedNullable);
    }

    public void setDateListed(Optional<LocalDate> dateListed){
        this.dateListedNullable = dateListed.orElse(null);
    }

    public Optional<LocalDate> getDateUnListed(){
        return Optional.ofNullable(dateUnListedNullable);
    }

    public void setDateUnListed(Optional<LocalDate> dateUnListed){
        this.dateUnListedNullable = dateUnListed.orElse(null);
    }

}
