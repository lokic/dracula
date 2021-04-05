package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.eventbus.transaction.EventTypeHandler;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEvent;
import com.github.lokic.dracula.eventbus.transaction.TransactionalEventRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TransactionalEventMysqlRepository implements TransactionalEventRepository {

    private final JdbcTemplate jdbcTemplate;


    private final EventTypeHandler eventTypeHandler;


    public TransactionalEventMysqlRepository(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate,  new FastJsonEventTypeHandler());
    }

    public TransactionalEventMysqlRepository(JdbcTemplate jdbcTemplate, EventTypeHandler eventTypeHandler) {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate is null");
        Objects.requireNonNull(eventTypeHandler, "eventTypeHandler is null");
        this.jdbcTemplate = jdbcTemplate;
        this.eventTypeHandler = eventTypeHandler;
    }

    @Override
    public void save(List<TransactionalEvent> events) {
        jdbcTemplate.batchUpdate("" +
                        "INSERT dr_transactional_event " +
                        " (event_key, event_content, status, current_retry_times, max_retry_times, next_retry_time, init_backoff, backoff_factor, creator, editor) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, events.get(i).getEventKey());
                        ps.setString(2, eventTypeHandler.serialize(events.get(i).getEvent()));
                        ps.setInt(3, events.get(i).getStatus().getStatus());
                        ps.setInt(3, events.get(i).getCurrentRetryTimes());
                        ps.setInt(4, events.get(i).getMaxRetryTimes());
                        ps.setTimestamp(5, Timestamp.valueOf(events.get(i).getNextRetryTime()));
                        ps.setObject(6, events.get(i).getInitBackoff());
                        ps.setObject(7, events.get(i).getBackoffFactor());
                        ps.setString(8, events.get(i).getCreator());
                        ps.setString(9, events.get(i).getEditor());
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                }
        );
    }

    @Override
    public void updateStatus(TransactionalEvent txEvent) {
        jdbcTemplate.update("" +
                        "UPDATE dr_transactional_event " +
                        "SET status = ? , current_retry_times = ? , max_retry_times = ? , next_retry_time = ?, " +
                        "init_backoff = ?, backoff_factor = ? " +
                        "WHERE id = ? ",
                ps -> {
                    ps.setInt(1, txEvent.getStatus().getStatus());
                    ps.setInt(2, txEvent.getCurrentRetryTimes());
                    ps.setInt(3, txEvent.getMaxRetryTimes());
                    ps.setTimestamp(4, Timestamp.valueOf(txEvent.getNextRetryTime()));
                    ps.setObject(5, txEvent.getInitBackoff());
                    ps.setObject(6, txEvent.getBackoffFactor());

                    ps.setLong(7, txEvent.getId());
                });
    }

    @Override
    public List<TransactionalEvent> queryEvents(LocalDateTime min, LocalDateTime max, int limit) {
        return jdbcTemplate.query("" +
                        "SELECT id, event_key, event_content, status, " +
                        "current_retry_times, max_retry_times, next_retry_time, init_backoff, backoff_factor, " +
                        "creator, editor, created_time, updated_time " +
                        "FROM dr_transactional_event " +
                        "WHERE ( next_retry_time BETWEEN ? AND ? ) AND status = 0 ",
                ps -> {
                    ps.setTimestamp(1, Timestamp.valueOf(min));
                    ps.setTimestamp(2, Timestamp.valueOf(max));
                },
                (rs, i) -> {
                    TransactionalEvent txEvent = new TransactionalEvent();
                    txEvent.setId(rs.getLong("id"));
                    txEvent.setEventKey(rs.getString("event_key"));
                    txEvent.setEvent(eventTypeHandler.deserialize(rs.getString("event_content")));
                    txEvent.setStatus(TransactionalEvent.Status.FROM_STATUS.requireOf(rs.getObject("status", Integer.class)));

                    txEvent.setCurrentRetryTimes(rs.getObject("current_retry_times", Integer.class));
                    txEvent.setMaxRetryTimes(rs.getObject("max_retry_times", Integer.class));
                    txEvent.setNextRetryTime(rs.getTimestamp("current_retry_times").toLocalDateTime());
                    txEvent.setInitBackoff(rs.getObject("init_backoff", Long.class));
                    txEvent.setBackoffFactor(rs.getObject("backoff_factor", Integer.class));

                    txEvent.setCreator(rs.getString("creator"));
                    txEvent.setEditor(rs.getString("editor"));
                    txEvent.setCratedTime(rs.getTimestamp("created_time").toLocalDateTime());
                    txEvent.setUpdatedTime(rs.getTimestamp("updated_time").toLocalDateTime());
                    return txEvent;
                }
        );
    }

}
