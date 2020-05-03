package statefull.geofencing.faas.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@SpringBootApplication(scanBasePackages = {"statefull.geofencing.faas"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private org.h2.tools.Server webServer;

    private org.h2.tools.Server server;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void setupH2Console() {
        try {
            this.webServer = org.h2.tools.Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
            this.server = org.h2.tools.Server.createTcpServer("-tcpPort", "9099", "-tcpAllowOthers").start();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.webServer.stop();
        this.server.stop();
    }
}
