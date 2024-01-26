    create table message_entity (
        id integer not null auto_increment,
        sent datetime(6),
        email varchar(255),
        external_case_id varchar(255),
        family_id varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        message varchar(255),
        message_id varchar(255),
        user_id varchar(255),
        username varchar(255),
        direction enum ('INBOUND','OUTBOUND'),
        primary key (id)
    ) engine=InnoDB;
