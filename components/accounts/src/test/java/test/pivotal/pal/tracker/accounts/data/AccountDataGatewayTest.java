package test.pivotal.pal.tracker.accounts.data;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.accounts.data.AccountDataGateway;
import io.pivotal.pal.tracker.accounts.data.AccountRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static io.pivotal.pal.tracker.accounts.data.AccountRecord.accountRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountDataGatewayTest {
    private JdbcTemplate template;
    private AccountDataGateway gateway;

    @Before
    public void setup() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        template = new JdbcTemplate(dataSource);
        template.execute("DELETE FROM projects;");
        template.execute("DELETE FROM accounts;");
        template.execute("DELETE FROM users;");

        gateway = new AccountDataGateway(dataSource);
    }

    @Test
    public void testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')");


        AccountRecord created = gateway.create(12L, "anAccount");


        assertThat(created.id).isNotNull();
        assertThat(created.name).isEqualTo("anAccount");
        assertThat(created.ownerId).isEqualTo(12);

        Map<String, Object> persisted = template.queryForMap("SELECT * FROM accounts WHERE id = ?", created.id);
        assertThat(persisted.get("name")).isEqualTo("anAccount");
        assertThat(persisted.get("owner_id")).isEqualTo(12L);
    }

    @Test
    public void testFindBy() {
        template.execute("insert into users (id, name) values (12, 'Jack')");
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')");


        List<AccountRecord> result = gateway.findAllByOwnerId(12L);


        assertThat(result).containsExactly(accountRecordBuilder()
            .id(1L)
            .ownerId(12L)
            .name("anAccount")
            .build()
        );
    }
}
