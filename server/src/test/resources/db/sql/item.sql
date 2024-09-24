INSERT INTO item (id, name, description, is_available, owner_id, request_id)
VALUES (1, 'Item of first user', 'Description of item of first user', true, 1, NULL),
       (2, 'Item of second user', 'Description of item of second user', true, 2, NULL),
       (3, 'Item response for 1', 'Response item for request with id 1', true, 2, 1),
       (4, 'Item response for 1', 'Second response item for request with id 1', true, 2, 1),
       (5, 'Item response for 2', 'Response item for request with id 2', true, 2, 2);