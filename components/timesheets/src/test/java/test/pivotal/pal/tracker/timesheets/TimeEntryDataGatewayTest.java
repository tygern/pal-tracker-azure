package test.pivotal.pal.tracker.timesheets;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.timesheets.data.TimeEntryDataGateway;
import io.pivotal.pal.tracker.timesheets.data.TimeEntryFields;
import io.pivotal.pal.tracker.timesheets.data.TimeEntryRecord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static io.pivotal.pal.tracker.timesheets.data.TimeEntryFields.timeEntryFieldsBuilder;
import static io.pivotal.pal.tracker.timesheets.data.TimeEntryRecord.timeEntryRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeEntryDataGatewayTest {
    private JdbcTemplate template;
    private TimeEntryDataGateway gateway;

    @Before
    public void setUp() throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        template = new JdbcTemplate(dataSource);
        template.execute("DELETE FROM time_entries;");

        gateway = new TimeEntryDataGateway(dataSource);
    }

    @Test
    public void testCreate() {
        TimeEntryFields fields = timeEntryFieldsBuilder()
            .projectId(22L)
            .userId(12L)
            .date(LocalDate.parse("2016-02-28"))
            .hours(8)
            .build();
        TimeEntryRecord created = gateway.create(fields);


        assertThat(created.id).isNotNull();
        assertThat(created.projectId).isEqualTo(22L);
        assertThat(created.userId).isEqualTo(12L);
        assertThat(created.date).isEqualTo(LocalDate.parse("2016-02-28"));
        assertThat(created.hours).isEqualTo(8);

        Map<String, Object> persisted = template.queryForMap("SELECT * FROM time_entries WHERE id = ?", created.id);

        assertThat(persisted.get("project_id")).isEqualTo(22L);
        assertThat(persisted.get("user_id")).isEqualTo(12L);
        assertThat(persisted.get("date")).isEqualTo(Timestamp.valueOf("2016-02-28 00:00:00"));
        assertThat(persisted.get("hours")).isEqualTo(8);
    }

    @Test
    public void testFindAllByUserId() {
        template.execute("insert into time_entries (id, project_id, user_id, date, hours) values (2346, 22, 12, '2016-01-13', 8)");


        List<TimeEntryRecord> result = gateway.findAllByUserId(12L);


        assertThat(result).containsExactlyInAnyOrder(
            timeEntryRecordBuilder()
                .id(2346L)
                .projectId(22L)
                .userId(12L)
                .date(LocalDate.parse("2016-01-13"))
                .hours(8)
                .build()
        );
    }
}
