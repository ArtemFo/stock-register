create or replace function process_stock_audit() returns trigger as
$stock_audit$
begin
    if tg_op = 'UPDATE' then
        if old.amount != new.amount then
            insert into stock_audit select old.code, 'amount', old.amount, new.amount, now();
        end if;
        if old.face_value != new.face_value then
            insert into stock_audit select old.code, 'face_value', old.face_value, new.face_value, now();
        end if;
        if old.status != new.status then
            insert into stock_audit select old.code, 'status', old.status, new.status, now();
        end if;
        if old.comment != new.comment then
            insert into stock_audit select old.code, 'comment', old.comment, new.comment, now();
        end if;
    end if;
    return null;
end;
$stock_audit$ language plpgsql;

create trigger stock_audit
    after update
    on stock
    for each row
execute procedure process_stock_audit();