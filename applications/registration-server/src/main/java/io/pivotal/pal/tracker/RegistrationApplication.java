package io.pivotal.pal.tracker;

import io.pivotal.pal.tracker.accounts.RegistrationService;
import io.pivotal.pal.tracker.accounts.data.AccountDataGateway;
import io.pivotal.pal.tracker.projects.data.ProjectDataGateway;
import io.pivotal.pal.tracker.users.data.UserDataGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.TimeZone;

@SpringBootApplication
public class RegistrationApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(RegistrationApplication.class, args);
    }

    @Bean
    UserDataGateway userDataGateway(DataSource dataSource) {
        return new UserDataGateway(dataSource);
    }

    @Bean
    ProjectDataGateway projectDataGateway(DataSource dataSource) {
        return new ProjectDataGateway(dataSource);
    }

    @Bean
    AccountDataGateway accountDataGateway(DataSource dataSource) {
        return new AccountDataGateway(dataSource);
    }

    @Bean
    RegistrationService registrationService(UserDataGateway userDataGateway, AccountDataGateway accountDataGateway) {
        return new RegistrationService(userDataGateway, accountDataGateway);
    }
}
