INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (1, DATEADD('YEAR', -2, NOW()), DATEADD('YEAR', -1, NOW()), 1, 2, 'APPROVED');

INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (2, DATEADD('DAY', -7, NOW()), DATEADD('DAY', -5, NOW()), 1, 3, 'APPROVED');

INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (3, DATEADD('DAY', 5, NOW()), DATEADD('DAY', 7, NOW()), 1, 4, 'WAITING');

INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (4, DATEADD('YEAR', 1, NOW()), DATEADD('YEAR', 2, NOW()), 1, 5, 'WAITING');

INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (5, DATEADD('MINUTE', 30, NOW()), DATEADD('MINUTE', 60, NOW()), 2, 5, 'WAITING');

INSERT INTO BOOKING (ID, START_DATE, END_DATE, ITEM_ID, USER_ID, STATUS)
VALUES (6, DATEADD('MINUTE', -30, NOW()), DATEADD('MINUTE', -60, NOW()), 2, 5, 'WAITING');