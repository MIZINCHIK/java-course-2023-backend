create type external_service as enum ('GITHUB', 'STACKOVERFLOW');

create table if not exists users
(
    id bigint,

    primary key (id)
);

create table if not exists links
(
    id          bigint generated always as identity,
    url         text                     not null,
    service     external_service         not null,
    last_update timestamp with time zone not null,

    primary key (id),
    unique (url)
);

create table if not exists following_links
(
    user_id    bigint not null,
    link_id    bigint not null,

    foreign key (user_id) references users (id) on delete cascade,
    foreign key (link_id) references links (id) on delete cascade,
    primary key (link_id, user_id)
-- нагуглил, что от порядка колонок в мультиколоночном PK зависит скорость поиска по этим колонкам
-- решил не создавать отдельный индекс для link_id, а оптимизировать тут
-- правда я не уверен, что триггер ниже удовлетворительно эффективен,
-- возможно, больше смысла было бы использовать периодические вызовы функций чистки таблицы

-- по триггеру также у меня возник вопрос с его concurrency safety
-- судя по тексту ниже, всё ок должно быть (с каскадами аналогично)
-- The execution of an AFTER trigger can be deferred to the end of the transaction,
-- rather than the end of the statement, if it was defined as a constraint trigger.
-- In all cases, a trigger is executed as part of the same transaction as the statement that triggered it,
-- so if either the statement or the trigger causes an error, the effects of both will be rolled back.
);

create or replace function delete_zombies()
    returns trigger
    language plpgsql as
'
begin
    if not exists (select 1 from following_links where link_id = old.link_id) then
        delete from links where id = old.link_id;
    end if;
    return old;
end;
';

create trigger delete_zombie
    after delete
    on following_links
    for each row
execute function delete_zombies();
