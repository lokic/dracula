package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.javaext.Property;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionalEvent {

    private Long id;

    private LocalDateTime cratedTime;

    private LocalDateTime updatedTime;

    // retry

    private Integer currentRetryTimes;

    private Integer maxRetryTimes;

    private LocalDateTime nextRetryTime;

    private Long initBackoff;

    private Integer backoffFactor;

    // message

    private String eventKey;

    private Event event;


    private String creator;

    private String editor;

    private Status status;

    public enum Status {


        /**
         * 待处理
         */
        PENDING(0),

        /**
         * 成功
         */
        SUCCESS(1),


        /**
         * 处理失败
         */
        FAIL(2),

        ;

        private final Integer status;

        Status(Integer status) {
            this.status = status;
        }

        public Integer getStatus() {
            return status;
        }

        public static final Property<Status, Integer> FROM_STATUS = new Property<>(Status.class, Status::getStatus);
    }
}
