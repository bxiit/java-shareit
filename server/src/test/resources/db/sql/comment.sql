INSERT INTO comment (id, text, item_id, author_id, created_at)
VALUES (1, 'Super', 1, 2, DATEADD('DAY', -360, NOW()));

INSERT INTO comment (id, text, item_id, author_id, created_at)
VALUES (2, 'Super again', 1, 2, DATEADD('DAY', -360, NOW()));

INSERT INTO comment (id, text, item_id, author_id, created_at)
VALUES (3, 'Agree with super', 1, 3, DATEADD('DAY', -5, NOW()));
