
    create table execution_information (
        last_successful_execution datetime(6),
        family_id varchar(255) not null,
        primary key (family_id)
    ) engine=InnoDB;

    create table message (
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
        instance enum ('EXTERNAL','INTERNAL'),
        primary key (id)
    ) engine=InnoDB;

    create table message_attachment (
        attachment_id integer not null,
        message_id integer,
        extension varchar(255),
        mime_type varchar(255),
        name varchar(255),
        file longblob,
        primary key (attachment_id)
    ) engine=InnoDB;

    alter table if exists message_attachment 
       add constraint FK_message_attachment_message_id 
       foreign key (message_id) 
       references message (id);
