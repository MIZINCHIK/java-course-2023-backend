create type external_service as enum ('github', 'stackoverflow');

create table if not exists users
(
    id bigint,

    primary key (id)
);

create table if not exists links
(
    id      bigint generated always as identity,
    url     text,
    service external_service,
    last_update timestamp with time zone not null,

    primary key (id),
    unique (url)
);

create table if not exists following_links
(
    user_id    bigint,
    link_id    bigint,
    created_at timestamp with time zone not null,

    foreign key (user_id) references users (id),
    foreign key (link_id) references links (id),
    primary key (user_id, link_id)
);
