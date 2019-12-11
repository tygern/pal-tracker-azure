package io.pivotal.pal.tracker;

import io.pivotal.pal.tracker.timesheets.ProjectClient;
import io.pivotal.pal.tracker.timesheets.data.TimeEntryDataGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.TimeZone;

@EnableDiscoveryClient
@SpringBootApplication
public class TimesheetsApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(TimesheetsApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    TimeEntryDataGateway timeEntryDataGateway(DataSource dataSource) {
        return new TimeEntryDataGateway(dataSource);
    }

    @Bean
    ProjectClient projectClient(
        RestOperations restOperations,
        @Value("${registration.server.endpoint}") String registrationEndpoint
    ) {
        return new ProjectClient(restOperations, registrationEndpoint);
    }
}
