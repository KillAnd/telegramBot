-- liquibase formatted sql

-- changeset student:1
CREATE TABLE if not exists notification_task (
    id bigint generated by default as identity primary key,
    chat_id bigint not null,
    message text not null,
    notification_date_time timestamp not null
    )