create sequence hibernate_sequence start 1 increment 1;

GO

create table stock
(
    id         int8       not null,
    amount     int4       not null,
    code       varchar(8) not null,
    comment    varchar(255),
    date       date       not null,
    face_value int4       not null,
    status     varchar(255),
    primary key (id)
);

GO

alter table stock
    add constraint stock_code_unique unique (code);

GO