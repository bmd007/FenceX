package statefull.geofencing.faas.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import statefull.geofencing.faas.runner.streamprocessing.KStreamAndKTableDefinitions;

import java.sql.SQLException;

@SpringBootApplication(scanBasePackages = {"statefull.geofencing.faas"})
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private org.h2.tools.Server webServer;

    private org.h2.tools.Server server;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void setupH2Console() {
        try {
            this.webServer = org.h2.tools.Server.createWebServer("-webPort", "8084", "-webAllowOthers").start();
            this.server = org.h2.tools.Server.createTcpServer("-tcpPort", "9097", "-tcpAllowOthers").start();
        } catch (SQLException throwable) {
            LOGGER.error("", throwable);
        }
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.webServer.stop();
        this.server.stop();
    }
}
