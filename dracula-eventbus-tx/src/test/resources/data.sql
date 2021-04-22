INSERT INTO dr_transactional_event (id, event_key, event_content, status, current_retry_times, max_retry_times,
                                    next_retry_time, init_backoff, backoff_factor, creator, editor)
VALUES (1, '1', '{}', 0, 0, 1, '2021-01-01 00:00:00', 10, 2, 'lokic', 'lokic');

INSERT INTO dr_transactional_event (id, event_key, event_content, status, current_retry_times, max_retry_times,
                                    next_retry_time, init_backoff, backoff_factor, creator, editor)
VALUES (2, '1', '{}', 0, 0, 1, '2021-01-01 00:00:00', 10, 2, 'lokic', 'lokic')