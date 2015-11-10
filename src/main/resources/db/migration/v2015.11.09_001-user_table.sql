create table aa_user (
    id bigserial primary key,
    username varchar(255) not null,
    enabled boolean default false,
    created_date timestamptz not null,
    last_modified_date timestamptz not null
);

