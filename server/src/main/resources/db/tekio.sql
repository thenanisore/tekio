-- `tekio` should have all right to read/modify the database
alter default privileges
    in schema public
    grant select, insert, update, delete on tables to tekio;

drop type reading_type, review_type, order_type, grade_level, jlpt_level, item_priority cascade;
drop table users, kanji, kanji_readings, kanji_meanings, user_kanji, user_items, user_answers cascade;

create type reading_type as enum ('ON', 'KUN');
create type review_type as enum ('MEANING', 'READING', 'QUIZ', 'WRITING');
create type order_type as enum ('AOZORA', 'NEWS', 'TWITTER', 'WIKI');
create type grade_level as enum ('G1', 'G2', 'G3', 'G4', 'G5', 'G6', 'G8');
create type jlpt_level as enum ('N1', 'N2', 'N3', 'N4', 'N5');
create type item_priority as enum ('NEW', 'HIGH', 'MID', 'LOW');


create or replace function update_updated_at()
returns trigger as $$
    begin
        new.updated_at = now();
        return new;
    end;
$$ language 'plpgsql';

-- Users, including their personal information and settings
create table users (
    id serial primary key,
    email varchar not null unique,
    hash varchar not null,
    salt varchar not null,
    -- profile info
    username varchar not null,
    full_name varchar,
    bio varchar,
    birth_date date,
    picture varchar,
    -- settings
    mins_per_day smallint not null,
    pref_order order_type not null,
    adapt_schedule boolean not null,
    adapt_order boolean not null,
    batch_size smallint not null,
    auto_reveal boolean not null,
    max_review_size smallint not null,
    max_lesson_size smallint not null,
    -- misc
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- Kanji (about 2k of them)
create table kanji (
    id serial primary key,
    literal varchar not null unique,
    stroke_count smallint not null,
    aozora_freq double precision,
    news_freq double precision,
    twitter_freq double precision,
    wiki_freq double precision,
    aozora_rank smallint,
    news_rank smallint,
    twitter_rank smallint,
    wiki_rank smallint,
    grade grade_level not null,
    jlpt jlpt_level not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- Kanji readings, each row is a specific reading of one kanji
create table kanji_readings (
    id serial primary key,
    kanji_id integer not null,
    reading varchar not null,
    type reading_type not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    foreign key (kanji_id) references kanji (id)
);

-- Kanji meanings (a word or a collocation),
-- each row is a specific meaning of one kanji (for now in en-US only)
create table kanji_meanings (
    id serial primary key,
    kanji_id integer not null,
    meaning varchar not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    foreign key (kanji_id) references kanji (id)
);

-- The user-specific information about the progress regarding a kanji.
create table user_kanji (
    id serial primary key,
    user_id integer not null,
    kanji_id integer not null,
    unlocked boolean not null default false,
    unlocked_at timestamptz,
    lesson_time integer,
    rec float,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    foreign key (user_id) references users (id),
    foreign key (kanji_id) references kanji (id),
    unique (user_id, kanji_id)
);

-- The user-specific information on a pair (kanji, review_type)
-- The row is created once the user finished the first review.
create table user_items (
    id serial primary key,
    user_kanji_id integer not null,
    review_type review_type not null,
    interval_s int not null,
    next_review timestamptz not null,
    finished bool not null default false,
    frozen bool not null default false,
    priority item_priority not null default 'NEW',
    answers int not null default 0,
    wins int not null default 0,
    fails int not null default 0,
    average_time int not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    foreign key (user_kanji_id) references user_kanji (id),
    unique (user_kanji_id, review_type)
);

-- The information about one user answer.
-- The rows are gathered in batches after review sessions.
create table user_answers (
    id serial primary key,
    user_item_id integer not null,
    answered_at timestamptz not null,
    time_taken integer not null,
    is_correct boolean not null,
    session_id uuid not null,
    answer varchar,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    foreign key (user_item_id) references user_items (id)
);

-- Auto generate trigger creation queries for all public tables
select
        'create trigger trigger_' || tablename || '_updated_at'
        || ' before update on ' || tablename
        || ' for each row execute procedure update_updated_at();' as trigger_query
from (
         select p.tablename from pg_tables p where p.schemaname = 'public'
     ) tablist;

create trigger trigger_users_updated_at before update on users for each row execute procedure update_updated_at();
create trigger trigger_kanji_readings_updated_at before update on kanji_readings for each row execute procedure update_updated_at();
create trigger trigger_user_kanji_updated_at before update on user_kanji for each row execute procedure update_updated_at();
create trigger trigger_kanji_meanings_updated_at before update on kanji_meanings for each row execute procedure update_updated_at();
create trigger trigger_kanji_updated_at before update on kanji for each row execute procedure update_updated_at();
create trigger trigger_user_items_updated_at before update on user_items for each row execute procedure update_updated_at();
create trigger trigger_user_answers_updated_at before update on user_answers for each row execute procedure update_updated_at();

grant usage, select on all sequences in schema public to tekio;

------------------------------------------------
----------------- TEST DATA --------------------
------------------------------------------------

-- Test user keke with password 123
-- insert into users (email, hash, salt, username, created_at, updated_at)
-- values ('keke@gmail.com', '$s0$120802$exiDKXEVuoi3zk9GQLq9gA==$pubCePGVPuoSZNKyQ+/oJwL1iko3Q5eu26oNG7e++LE=', 'af4f2c56af747fda850c661d5de2e8de1a2db75be908ed4effa6ef99ed7ddd3f', 'keke', '2019-03-20 01:27:56.88', '2019-03-20 01:27:56.88');

-- Test user_kanji and user_items entries: for each user every kanji/item
-- insert into user_kanji (user_id, kanji_id, unlocked, rec)
-- select u.id user_id, k.id kanji_id, false, 1.0 / k.freq
-- from users u cross join kanji k;
--
-- insert into user_items (user_kanji_id, review_type, interval_s, next_review)
-- select uk.id as user_kanji_id, rw as review_type, 300, current_timestamp + (interval '300 second')
-- from user_kanji as uk cross join unnest(enum_range(NULL::review_type)) as rw;
