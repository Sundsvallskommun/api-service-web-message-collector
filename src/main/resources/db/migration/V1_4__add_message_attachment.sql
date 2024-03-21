create table message_attachment
(
    attachment_id integer not null,
    extension     varchar(255),
    mime_type     varchar(255),
    name          varchar(255),
    file          longblob,
    primary key (attachment_id)
) engine = InnoDB;
