package test.pivotal.pal.tracker.users.data;


import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.users.data.UserDataGateway;
import io.pivotal.pal.tracker.users.data.UserRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDataGatewayTest {
    private JdbcTemplate template;
    private UserDataGateway gateway;

    @Before
    public void setUp() throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        template = new JdbcTemplate(dataSource);
        template.execute("DELETE FROM projects;");
        template.execute("DELETE FROM accounts;");
        template.execute("DELETE FROM users;");

        gateway = new UserDataGateway(dataSource);
    }

    @Test
    public void testCreate() {
        UserRecord createdUser = gateway.create("aUser");


        assertThat(createdUser.id).isGreaterThan(0);
        assertThat(createdUser.name).isEqualTo("aUser");

        Map<String, Object> persistedFields = template.queryForMap("SELECT id, name FROM users WHERE id = ?", createdUser.id);
        assertThat(persistedFields.get("id")).isEqualTo(createdUser.id);
        assertThat(persistedFields.get("name")).isEqualTo(createdUser.name);
    }

    @Test
    public void testFind() {
        template.execute("INSERT INTO users(id, name) VALUES (42346, 'aName'), (42347, 'anotherName'), (42348, 'andAnotherName')");


        UserRecord record = gateway.find(42347L);


        assertThat(record).isEqualTo(new UserRecord(42347L, "anotherName"));
    }

    @Test
    public void testFind_WhenNotFound() {
        assertThat(gateway.find(42347L)).isNull();
    }
}
