package com.github.lokic.dracula.eventbus.transaction.mysql;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.transaction.EventTypeSerializer;
import com.github.lokic.dracula.eventbus.transaction.JdbcTemplateExtension;
import com.github.lokic.dracula.eventbus.transaction.TransactionEvent;
import com.github.lokic.dracula.eventbus.transaction.TransactionEventRepository;
import com.github.lokic.dracula.eventbus.transaction.serializer.FastJsonEventTypeSerializer;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionEventMysqlRepository implements TransactionEventRepository {

    private final JdbcTemplateExtension jdbcTemplate;

    private final EventTypeSerializer eventTypeSerializer;


    public TransactionEventMysqlRepository(JdbcTemplateExtension jdbcTemplate) {
        this(jdbcTemplate, new FastJsonEventTypeSerializer());
    }

    public TransactionEventMysqlRepository(JdbcTemplateExtension jdbcTemplate, EventTypeSerializer eventTypeSerializer) {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate is null");
        Objects.requireNonNull(eventTypeSerializer, "eventTypeSerializer is null");
        this.jdbcTemplate = jdbcTemplate;
        this.eventTypeSerializer = eventTypeSerializer;
    }

    @Override
    public void save(List<TransactionEvent<? extends Event>> events) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.batchUpdate("" +
                        "INSERT INTO dr_transaction_event " +
                        " (event_key, event_content, status, current_retry_times, max_retry_times, " +
                        "next_retry_time, init_backoff, backoff_factor, creator, editor) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, events.get(i).getEventKey());
                        ps.setString(2, eventTypeSerializer.serialize(events.get(i).getEvent()));
                        ps.setInt(3, events.get(i).getStatus().getStatus());
                        ps.setInt(4, events.get(i).getCurrentRetryTimes());
                        ps.setInt(5, events.get(i).getMaxRetryTimes());
                        ps.setTimestamp(6, Timestamp.valueOf(events.get(i).getNextRetryTime()));
                        ps.setObject(7, events.get(i).getInitBackoff());
                        ps.setObject(8, events.get(i).getBackoffFactor());
                        ps.setString(9, events.get(i).getCreator());
                        ps.setString(10, events.get(i).getEditor());
                    }

                    @Override
                    public int getBatchSize() {
                        return events.size();
                    }
                },
                generatedKeyHolder
        );

        List<Map<String, Object>> objectMap = generatedKeyHolder.getKeyList();
        for (int i = 0; i < events.size(); i++) {
            TransactionEvent<? extends Event> event = events.get(i);
            event.setId(Long.valueOf(objectMap.get(i).get("ID").toString()));
        }
    }

    @Override
    public void updateStatus(TransactionEvent<? extends Event> txEvent) {
        jdbcTemplate.update("" +
                        "UPDATE dr_transaction_event " +
                        "SET status = ? , current_retry_times = ? , max_retry_times = ? , next_retry_time = ?, " +
                        "init_backoff = ?, backoff_factor = ? , editor = ? " +
                        "WHERE id = ? ",
                ps -> {
                    ps.setInt(1, txEvent.getStatus().getStatus());
                    ps.setInt(2, txEvent.getCurrentRetryTimes());
                    ps.setInt(3, txEvent.getMaxRetryTimes());
                    ps.setTimestamp(4, Timestamp.valueOf(txEvent.getNextRetryTime()));
                    ps.setObject(5, txEvent.getInitBackoff());
                    ps.setObject(6, txEvent.getBackoffFactor());
                    ps.setObject(7, txEvent.getEditor());
                    ps.setLong(8, txEvent.getId());
                });
    }

    @Override
    public void updateSuccessByEventIds(String editor, LocalDateTime endTime, List<Long> ids) {
        String inSql = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        Object[] params = Stream.concat(Stream.of(Timestamp.valueOf(endTime), editor), ids.stream()).toArray();
        jdbcTemplate.update(
                String.format("UPDATE dr_transaction_event SET status = 1, next_retry_time = ?, editor= ?  WHERE id in ( %s ) ", inSql),
                params);
    }

    @Override
    public List<TransactionEvent<? extends Event>> queryEvents(LocalDateTime min, LocalDateTime max, int limit) {
        return jdbcTemplate.query("" +
                        "SELECT id, event_key, event_content, status, " +
                        "current_retry_times, max_retry_times, next_retry_time, init_backoff, backoff_factor, " +
                        "creator, editor, created_time, updated_time " +
                        "FROM dr_transaction_event " +
                        "WHERE ( next_retry_time BETWEEN ? AND ? ) AND status = 0 " +
                        "LIMIT ?",
                ps -> {
                    ps.setTimestamp(1, Timestamp.valueOf(min));
                    ps.setTimestamp(2, Timestamp.valueOf(max));
                    ps.setInt(3, limit);
                },
                (rs, i) -> {
                    TransactionEvent<Event> txEvent = new TransactionEvent<>();
                    txEvent.setId(rs.getLong("id"));
                    txEvent.setEventKey(rs.getString("event_key"));
                    txEvent.setEvent(eventTypeSerializer.deserialize(rs.getString("event_content"), Event.class));
                    txEvent.setStatus(TransactionEvent.Status.FROM_STATUS.requireOf(rs.getObject("status", Integer.class)));

                    txEvent.setCurrentRetryTimes(rs.getObject("current_retry_times", Integer.class));
                    txEvent.setMaxRetryTimes(rs.getObject("max_retry_times", Integer.class));
                    txEvent.setNextRetryTime(rs.getTimestamp("next_retry_time").toLocalDateTime());
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
