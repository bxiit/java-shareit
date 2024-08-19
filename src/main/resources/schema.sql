-- -- DROP
--
-- DROP TABLE IF EXISTS public.users CASCADE;
-- DROP TABLE IF EXISTS public.item CASCADE;
-- DROP TABLE IF EXISTS public.comment CASCADE;
-- DROP TABLE IF EXISTS public.booking CASCADE;
-- DROP TABLE IF EXISTS public.request CASCADE;
-- DROP TYPE IF EXISTS public.booking_status CASCADE;
--
-- -- CREATE
CREATE TYPE public.booking_status AS ENUM (
    'WAITING',
    'APPROVED',
    'REJECTED',
    'CANCELED'
    );

CREATE TABLE IF NOT EXISTS public.users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255),
    name  VARCHAR(255),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE public.request
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255),
    requestor_id BIGINT,
    CONSTRAINT fk_users_request_id FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS public.item
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255),
    description  VARCHAR(200),
    is_available BOOLEAN,
    owner_id     BIGINT,
    request_id   BIGINT,
    CONSTRAINT fk_items_user_id FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_items_request_id FOREIGN KEY (request_id) REFERENCES request (id)
);

CREATE TABLE IF NOT EXISTS public.booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    user_id    BIGINT,
    status     VARCHAR(15),
    CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES item (id)
);

CREATE TABLE IF NOT EXISTS public.comment
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text       VARCHAR(255),
    item_id    BIGINT,
    author_id  BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES item (id),
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (id)
);