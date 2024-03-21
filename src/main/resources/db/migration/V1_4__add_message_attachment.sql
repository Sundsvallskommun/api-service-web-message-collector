create table message_attachment
(
    attachment_id integer not null,
    file_name     varchar(255),
    file          longblob,
    primary key (attachment_id)
) engine = InnoDB;
