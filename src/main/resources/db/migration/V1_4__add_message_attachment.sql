create table message_attachment
(
    attachment_id integer not null,
    message_id    integer,
    extension     varchar(255),
    mime_type     varchar(255),
    name          varchar(255),
    file          longblob,
    primary key (attachment_id)
) engine = InnoDB;

alter table if exists message_attachment
    add constraint FK_message_attachment_message_id
        foreign key (message_id)
            references message (id);
