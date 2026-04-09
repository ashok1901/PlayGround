package parkinglot.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import parkinglot.ParkingSpotType;
import parkinglot.TicketRecord;

public class TicketRecordDao {

    private static final String UPSERT =
            """
            INSERT INTO ticket_record (ticket_id, entry_time, parking_spot_id, vehicle_type, exit_time)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (ticket_id) DO UPDATE SET
                entry_time = EXCLUDED.entry_time,
                parking_spot_id = EXCLUDED.parking_spot_id,
                vehicle_type = EXCLUDED.vehicle_type,
                exit_time = EXCLUDED.exit_time
            """;

    private static final String SELECT_BY_ID =
            """
            SELECT ticket_id, entry_time, parking_spot_id, vehicle_type, exit_time
            FROM ticket_record
            WHERE ticket_id = ?
            """;

    private final JdbcTemplate jdbc;

    public TicketRecordDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public void persist(TicketRecord record) {
        jdbc.update(
                UPSERT,
                ps -> {
                    ps.setString(1, record.getTicketId());
                    ps.setObject(2, record.getEntryTime());
                    ps.setString(3, record.getParkingSpotId());
                    ps.setString(4, record.getVehicleType().name());
                    if (record.getExitTime() != null) {
                        ps.setObject(5, record.getExitTime());
                    } else {
                        ps.setNull(5, Types.TIMESTAMP_WITH_TIMEZONE);
                    }
                });
    }

    public Optional<TicketRecord> findByTicketId(String ticketId) {
        return jdbc.query(SELECT_BY_ID, new TicketRowMapper(), ticketId).stream().findFirst();
    }

    private static final class TicketRowMapper implements RowMapper<TicketRecord> {

        @Override
        public TicketRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            Instant entry = rs.getObject("entry_time", Instant.class);
            String spotId = rs.getString("parking_spot_id");
            ParkingSpotType vehicleType = ParkingSpotType.valueOf(rs.getString("vehicle_type"));
            String id = rs.getString("ticket_id");
            TicketRecord record = new TicketRecord(entry, spotId, vehicleType, id);
            Instant exit = rs.getObject("exit_time", Instant.class);
            if (exit != null) {
                record.setExitTime(exit);
            }
            return record;
        }
    }
}
