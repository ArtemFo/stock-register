create table stock_audit
(
    code     varchar(8) NOT NULL,
    row      text       NOT NULL,
    oldvalue text       NOT NULL,
    newvalue text       NOT NULL,
    stamp    timestamp  NOT NULL
);

GO