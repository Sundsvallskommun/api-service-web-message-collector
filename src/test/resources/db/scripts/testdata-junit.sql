INSERT INTO message (id, municipality_id, external_case_id, family_id, message, message_id, direction, sent, email,
                     first_name, last_name, user_id, username, instance, status, status_timestamp)
VALUES (1, 1984, '100', '200', 'MESSAGE_1', '1', 'INBOUND', '2023-02-23 17:26:23.458389',
            'EMAIL_1', 'FIRST_NAME_1', 'LAST_NAME_1', 'USER_ID_1', 'USER_NAME_1', 'INTERNAL', 'COMPLETE', '2024-01-01 15:00:00'),
       (2, 1985, '101', '200', 'MESSAGE_2', '2', 'INBOUND', '2023-02-23 17:26:23.458389',
            'EMAIL_2', 'FIRST_NAME_2', 'LAST_NAME_2', 'USER_ID_2', 'USER_NAME_2', 'INTERNAL', 'COMPLETE', '2024-01-01 15:00:00'),
       (3, 1984, '102', '201', 'MESSAGE_3', '3', 'INBOUND', '2023-02-23 17:26:23.458389',
            'EMAIL_3', 'FIRST_NAME_3', 'LAST_NAME_3', 'USER_ID_3', 'USER_NAME_3', 'INTERNAL', 'COMPLETE', '2024-01-01 15:00:00'),
       (4, 1984, '104', '200', 'MESSAGE_4', '4', 'INBOUND', '2023-02-23 17:26:23.458389',
            'EMAIL_4', 'FIRST_NAME_4', 'LAST_NAME_4', 'USER_ID_4', 'USER_NAME_4', 'EXTERNAL', 'COMPLETE', '2024-01-01 15:00:00'),
       (5, 1984, '105', '200', 'MESSAGE_5', '5', 'INBOUND', '2023-02-23 17:26:23.458389',
            'EMAIL_5', 'FIRST_NAME_5', 'LAST_NAME_5', 'USER_ID_5', 'USER_NAME_5', 'INTERNAL', 'PROCESSING', '2024-01-01 15:00:00');

INSERT INTO execution_information (municipality_id, family_id)
VALUES (1984, '1'),
       (1984, '2'),
       (2281, '3');
