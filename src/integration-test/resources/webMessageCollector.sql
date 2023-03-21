create table webMessageCollector.message_entity
(
    id                varchar(255) not null
        primary key,
    external_case_id  varchar(255) null,
    family_id         varchar(255) null,
    message           varchar(255) null,
    message_id        varchar(255) null,
    posted_by_manager bit          not null,
    sent              datetime(6)  null,
    email             varchar(255) null,
    first_name        varchar(255) null,
    last_name         varchar(255) null,
    user_id           varchar(255) null,
    username          varchar(255) null
);

insert into webMessageCollector.message_entity
VALUES (1, '223', '123', 'Hello World', '1', 0, '2023-02-23 17:26:23.458389', 'test@sundsvall.se',
        'Test', 'Testorsson', 'testId', 'test');