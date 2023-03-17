create table webMessageCollector.poster_entity
(
    id         varchar(255) not null
        primary key,
    email      varchar(255) null,
    first_name varchar(255) null,
    last_name  varchar(255) null,
    user_id    varchar(255) null,
    username   varchar(255) null
);

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
    poster_entity_id  varchar(255) null,
    constraint FKp34nhd8nvav2jl3je63fg6hvp
        foreign key (poster_entity_id) references webMessageCollector.poster_entity (id)
);

insert into webMessageCollector.poster_entity
VALUES (1,'test@sundsvall.se','Test','Testorsson','testId','test');
insert into webMessageCollector.message_entity
VALUES (1,'223','123','Hello World','1',0,'2023-02-23 17:26:23.458389',1);