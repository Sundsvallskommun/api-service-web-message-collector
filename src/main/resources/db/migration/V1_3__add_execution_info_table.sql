    create table execution_information (
        last_successful_execution datetime(6),
        family_id varchar(255) not null,
        primary key (family_id)
    ) engine=InnoDB;

