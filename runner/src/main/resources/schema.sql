create table if not exists movers
(
    id                     varchar                  not null,
    position               geometry                 not null default 'point(0 0)',
    availability_status    boolean                  not null default false,
    availability_timestamp timestamp with time zone not null default timestamp '2000-01-01',
    primary key (id)
);

create spatial index if not exists mover_spatial_index on movers (position);
