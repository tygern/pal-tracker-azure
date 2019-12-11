package test.pivotal.pal.tracker.projects;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.projects.data.ProjectDataGateway;
import io.pivotal.pal.tracker.projects.data.ProjectFields;
import io.pivotal.pal.tracker.projects.data.ProjectRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static io.pivotal.pal.tracker.projects.data.ProjectFields.projectFieldsBuilder;
import static io.pivotal.pal.tracker.projects.data.ProjectRecord.projectRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectDataGatewayTest {
    private JdbcTemplate template;
    private ProjectDataGateway gateway;

    @Before
    public void setUp() throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        template = new JdbcTemplate(dataSource);
        template.execute("DELETE FROM projects;");
        template.execute("DELETE FROM accounts;");
        template.execute("DELETE FROM users;");

        gateway = new ProjectDataGateway(dataSource);
    }

    @Test
    public void testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')");
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')");

        ProjectFields fields = projectFieldsBuilder().accountId(1).name("aProject").build();
        ProjectRecord created = gateway.create(fields);


        assertThat(created.id).isNotNull();
        assertThat(created.name).isEqualTo("aProject");
        assertThat(created.accountId).isEqualTo(1L);

        Map<String, Object> persisted = template.queryForMap("SELECT * FROM projects WHERE id = ?", created.id);

        assertThat(persisted.get("name")).isEqualTo("aProject");
        assertThat(persisted.get("account_id")).isEqualTo(1L);
    }

    @Test
    public void testFindAllByAccountId() {
        template.execute("insert into users (id, name) values (12, 'Jack')");
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')");
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')");


        List<ProjectRecord> result = gateway.findAllByAccountId(1L);


        assertThat(result).containsExactlyInAnyOrder(
            projectRecordBuilder().id(22L).accountId(1L).name("aProject").active(true).build()
        );
    }

    @Test
    public void testFind() {
        template.execute("insert into users (id, name) values (12, 'Jack')");
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')");
        template.execute("insert into projects (id, account_id, name, active) values (22, 1, 'aProject', true)");


        ProjectRecord foundRecord = gateway.find(22L);


        assertThat(foundRecord).isEqualTo(
            projectRecordBuilder().id(22L).accountId(1L).name("aProject").active(true).build()
        );
    }
}
