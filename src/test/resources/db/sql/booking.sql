insert into users (id, email, name)
values (1111, 'bexeiitatabek@yandex.kz', 'bxiit');

insert into item (id, name, description, is_available, owner_id, request_id)
values (1111, 'Acer', 'Лучше мак', true, 1111, null);

insert into booking (id, start_date, end_date, item_id, user_id, status)
values (1111, now(), dateadd('HOUR', 3, now()), 1111, 1111, 'WAITING');