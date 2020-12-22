package nl.arnovanoort.stockreader.repository;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
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
		ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(
				io.r2dbc.postgresql.PostgresqlConnectionConfiguration.builder()
						.host(dbConfig.getHost())
						.port(dbConfig.getPort())
						.username(dbConfig.getUsername())
						.password(dbConfig.getPassword())
						.database(dbConfig.getDatabase())
						.build()
		);
		ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
				.maxIdleTime(Duration.ofMinutes(30))
				.initialSize(2)
				.maxSize(10)
				.maxCreateConnectionTime(Duration.ofSeconds(1))
				.build();

		return new ConnectionPool(configuration);
	}
}