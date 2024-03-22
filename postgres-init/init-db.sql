create table public.person
(
    id           serial,
    firstname    varchar,
    lastname     varchar,
    street       varchar,
    city         varchar,
    postal_code  int
);

create unique index person_id_uindex
    on public.person (id);

alter table public.person
    add primary key (id);

alter table public.person
    add unique (id);

create table public.stay
(
    stay_number int,
    person_id  int,
    start_date date,
    end_date   date
);

create unique index stay_id_uindex
    on public.stay (stay_number);

alter table public.stay
    add primary key (stay_number);

alter table public.stay
    add unique (stay_number);

alter table public.stay
    add constraint stay_personid_foreign
        foreign key ("person_id") references public.person (id);

create table public.movement
(
    id          serial,
    person_id  int,
    stay_number    int,
    date      date,
    service    varchar,
    room      varchar,
    bed       varchar
);

create unique index movement_id_uindex
    on public.movement (id);

alter table public.movement
    add primary key (id);

alter table public.movement
    add unique (id);

alter table public.movement
    add constraint movement_personid_foreign
        foreign key (person_id) references public.person (id);

alter table public.movement
    add constraint movement_stayid_foreign
        foreign key (stay_number) references public.stay (stay_number);

