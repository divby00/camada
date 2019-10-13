delete from camada_user;
insert into camada.camada_user (name, password, is_active, first_name, last_name, email, activation_date, is_admin, is_virtual_sponsor, is_presential_sponsor, is_volunteer, is_partner)
    value ('admin', sha1('admin'), true, 'Laura', 'Palmer', 'email1@domain.com', now(), true, false, false, false, false);
insert into camada.camada_user (name, password, is_active, first_name, last_name, email, activation_date, is_admin, is_virtual_sponsor, is_presential_sponsor, is_volunteer, is_partner)
    value ('partner', sha1('partner'), true, 'Mario', 'bros', 'email2@domain.com', now(), false, false, false, false, true);
commit;
