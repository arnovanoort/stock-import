package nl.arnovanoort.stockreader.repository;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcRepositories
class R2DBCConfiguration extends AbstractR2dbcConfiguration {

	@Autowired
	DBConfig dbConfig;

	@Bean
	public ConnectionFactory connectionFactory() {
		return new io.r2dbc.postgresql.PostgresqlConnectionFactory(
		        io.r2dbc.postgresql.PostgresqlConnectionConfiguration.builder()
								.host(dbConfig.getHost())
								.port(dbConfig.getPort())
								.username(dbConfig.getUsername())
								.password(dbConfig.getPassword())
								.database(dbConfig.getDatabaseName())
                .build()
        );
	}
}