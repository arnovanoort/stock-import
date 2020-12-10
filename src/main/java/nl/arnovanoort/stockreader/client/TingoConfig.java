package nl.arnovanoort.stockreader.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="tiingo")
public class TingoConfig {

    @Getter @Setter
    private String path;
    @Getter @Setter
    private String host;
    @Getter @Setter
    private String token;
    @Getter @Setter
    private String supportedTickersZip;

}