package statefull.geofencing.faas.location.update.processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return new SimpleDriverDataSource(new org.h2.Driver(), "jdbc:h2:mem:movers;DB_CLOSE_DELAY=-1");
    }

}
