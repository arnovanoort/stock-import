package nl.arnovanoort.stockreader.repository;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.time.Duration;

@Configuration
@EnableR2dbcRepositories
class R2DBCConfiguration extends AbstractR2dbcConfiguration {

	@Autowired
	DBConfig dbConfig;

	@Bean
	public ConnectionFactory connectionFactory() {

		ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(dbConfig.getUrl())
				.mutate()
				.option(ConnectionFactoryOptions.USER, dbConfig.getUsername())
				.option(ConnectionFactoryOptions.PASSWORD, dbConfig.getPassword())
				.option(ConnectionFactoryOptions.DATABASE, dbConfig.getDatabase())
				.build();

		ConnectionFactory connectionFactory = ConnectionFactories.get(options);

		ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
				.maxIdleTime(Duration.ofMinutes(30))
				.initialSize(2)
				.maxSize(10)
				.maxCreateConnectionTime(Duration.ofSeconds(1))
				.build();

		return new ConnectionPool(configuration);
	}
}