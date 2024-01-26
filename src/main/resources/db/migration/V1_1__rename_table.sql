    rename table message_entity to message;
    
    alter table message modify column direction enum ('INBOUND','OUTBOUND');