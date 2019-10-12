create table partner
(
    id int not null primary key,
    name varchar(45) null,
    surname1 varchar(45) null,
    surname2 varchar(45) null
);

create table role
(
    id int not null primary key,
    name varchar(45) null
);

create table camada_user
(
    id int not null primary key,
    password varchar(45) not null,
    name varchar(45) not null,
    role_id int null,
    constraint user_role__fk foreign key (role_id) references role (id)
);

