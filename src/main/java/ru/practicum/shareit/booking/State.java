package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import java.time.Instant;

public enum State implements QueryParameters {
    ALL {
        @Override
        public Predicate getQueryParams() {
            // пустой предикат (where true = true)
            return Expressions.TRUE.isTrue();
        }
    },
    CURRENT {
        @Override
        public Predicate getQueryParams() {
            Instant now = Instant.now();
            return QBooking.booking.start.before(now)
                    .and(QBooking.booking.end.after(now))
                    .and(QBooking.booking.status.eq(Status.APPROVED));
        }
    },
    PAST {
        @Override
        public Predicate getQueryParams() {
            Instant now = Instant.now();
            return QBooking.booking.end.before(now);
        }
    },
    FUTURE {
        @Override
        public Predicate getQueryParams() {
            Instant now = Instant.now();
            return QBooking.booking.start.after(now);
        }
    },
    WAITING {
        @Override
        public Predicate getQueryParams() {
            return QBooking.booking.status.eq(Status.WAITING);
        }
    },
    REJECTED {
        @Override
        public Predicate getQueryParams() {
            return QBooking.booking.status.eq(Status.REJECTED);
        }
    }
}
