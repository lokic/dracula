package com.github.lokic.dracula.eventbus.transaction;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.javaplus.property.Property1;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionEvent<E extends Event> {

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

    private E event;

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

        public static final Property1<Status, Integer> FROM_STATUS = new Property1<>(Status.class, Status::getStatus);
    }
}
