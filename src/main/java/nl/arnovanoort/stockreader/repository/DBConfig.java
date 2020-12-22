package nl.arnovanoort.stockreader.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="db")
public class DBConfig {

    @Getter @Setter
    private String host;
    @Getter @Setter
    private String port;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String database;

    public Integer getPort() {
        return Integer.valueOf(port);
    }


}
