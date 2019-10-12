delete from role;
insert into camada.role(name, code) values ('ROLE_ADMIN', 1);
insert into camada.role(name, code) values ('ROLE_VIRTUAL_SPONSOR', 2);
insert into camada.role(name, code) values ('ROLE_PRESENCIAL_SPONSOR', 4);
insert into camada.role(name, code) values ('ROLE_PARTNER', 8);
insert into camada.role(name, code) values ('ROLE_VOLUNTEER', 16);
commit;

delete from camada_user;
commit;

insert into camada.camada_user (name, password, permissions, first_name, last_name, email, activation_date)
    value ('admin', sha1('admin'), (select id from camada.role where name like 'ROLE_ADMIN'), 'Laura', 'Palmer', 'email1@domain.com', now());
insert into camada.camada_user (name, password, permissions, first_name, last_name, email, activation_date)
    value ('partner', sha1('partner'), (select id from camada.role where name like 'ROLE_PARTNER'), 'Mario', 'bros', 'email2@domain.com', now());

commit;

select * from camada_user;
select * from camada.role;