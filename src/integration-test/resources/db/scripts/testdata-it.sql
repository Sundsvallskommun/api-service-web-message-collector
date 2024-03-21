INSERT INTO message (id, external_case_id, family_id, message, message_id, direction, sent, email,
                     first_name, last_name, user_id, username)
VALUES (1, '223', '123', 'Hello World', '1', 'INBOUND', '2023-02-23 17:26:23.458389',
        'test@sundsvall.se', 'Test', 'Testorsson', 'testId', 'test');

INSERT INTO message_attachment (attachment_id, file_name, file)
VALUES (1, 'test.txt', 'Hello World');
