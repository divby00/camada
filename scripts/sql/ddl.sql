create schema camada collate utf8mb4_0900_ai_ci;

create table banking_data
(
    id bigint auto_increment
        primary key,
    iban varchar(255) null,
    name varchar(255) null,
    surnames varchar(255) null
)
    engine=MyISAM;

create table camada_user
(
    id int auto_increment
        primary key,
    password varchar(45) not null,
    name varchar(45) not null,
    first_name varchar(45) not null,
    last_name varchar(45) not null,
    email varchar(200) not null,
    activation_date date not null,
    last_connection datetime null,
    is_admin tinyint(1) default 0 not null,
    is_volunteer tinyint(1) default 0 not null,
    is_virtual_sponsor tinyint(1) default 0 not null,
    is_presential_sponsor tinyint(1) default 0 not null,
    is_partner tinyint(1) default 0 not null,
    is_active tinyint(1) default 1 null,
    tmp_password varchar(255) null,
    tmp_password_expiration datetime null,
    constraint camada_user_name_uindex
        unique (name)
);

create table collaborator
(
    id int auto_increment
        primary key,
    name varchar(45) not null,
    surname1 varchar(45) not null,
    surname2 varchar(45) not null,
    dni varchar(13) not null,
    address varchar(100) null,
    post_code int null,
    location varchar(100) null,
    province varchar(45) null,
    phone varchar(9) not null,
    email varchar(100) null,
    entry_date date not null,
    leaving_date date null,
    observations varchar(500) null,
    birth_date date null
);

create table custom_query
(
    id bigint auto_increment
        primary key,
    description varchar(255) null,
    query varchar(255) null,
    zorder int null
)
    engine=MyISAM;

create table email_template
(
    id bigint auto_increment
        primary key,
    content longtext null,
    creation_date datetime null,
    is_public bit null,
    name varchar(255) null,
    subject varchar(255) null,
    user_name varchar(255) null
)
    engine=MyISAM;

create table partner
(
    id bigint auto_increment
        primary key,
    amount double null,
    payment_frequency varchar(30) null,
    banking_data_id bigint null,
    personal_data_id bigint null,
    camada_id bigint null,
    fk_subscription bigint null
)
    engine=MyISAM;

create index FK1vr1enayddxu65tk5sl8b2a83
    on partner (fk_subscription);

create index FK3ugqglq874psnswt7t95rjk6q
    on partner (banking_data_id);

create index FK8myfpii8q7y13va6ln7d2fepq
    on partner (personal_data_id);

create table personal_data
(
    id bigint auto_increment
        primary key,
    address varchar(255) null,
    birth_date datetime null,
    dni varchar(255) null,
    email varchar(255) null,
    location varchar(255) null,
    name varchar(255) null,
    phone1 varchar(255) null,
    phone2 varchar(255) null,
    post_code bigint null,
    province varchar(255) null,
    surnames varchar(255) null
)
    engine=MyISAM;

create table subscription
(
    id bigint auto_increment
        primary key,
    role varchar(30) null,
    subscribed_from datetime null,
    subscribed_to datetime null,
    fk_partner bigint null
)
    engine=MyISAM;

create index FKox6nwirwu6w2qvdu03b88s5ci
    on subscription (fk_partner);

