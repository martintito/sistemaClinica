ALTER TABLE empleado ADD actualiza_clave int(1) AFTER sueldo_base;
delimiter //
create trigger actualiza_usuarios
 before update on empleado
 for each row
 begin


IF new.actualiza_clave = 1  THEN
    set new.password = SHA2(new.password, 256);
    END IF;

end//
delimiter ;


create table perfil(
pk_id int(11) not null auto_increment,
nombre varchar(100),
descripcion varchar(100),
fec_registro datetime,
usu_registro int(5),
estado_existencia int(1) not null default 0,
primary key (pk_id)
)engine=innodb;
insert into perfil values(1,'administrador','',now(),0,1);
insert into perfil values(2,'asistente-ventas','',now(),0,1);
insert into perfil values(3,'vendedor','',now(),0,1);


create table urls_sistema(
pk_id int(11) not null auto_increment,
fk_perfil int(11),
nombre varchar(100),
descripcion varchar(100),
fec_registro datetime,
usu_registro int(5),
estado_existencia int(1) not null default 0,
primary key (pk_id),
foreign key (fk_perfil)references perfil(pk_id)
)engine=innodb;




create table empleado_perfil(
pk_id int(11) not null auto_increment,
fk_empleado int(11) not null,
fk_perfil int(11) not null,
fec_registro datetime,
usu_registro int(5),
estado_existencia int(1) not null default 0,
primary key (pk_id),
foreign key (fk_perfil)references perfil(pk_id),
foreign key (fk_empleado)references empleado(id_empleado)
)engine=innodb;

create table empleado_urls_sistema(
pk_id int(11) not null auto_increment,
fk_empleado int(11) not null,
fk_urls_sistema int(11) not null,
fec_registro datetime,
usu_registro int(5),
estado_existencia int(1) not null default 0,
primary key (pk_id),
foreign key (fk_urls_sistema)references urls_sistema(pk_id),
foreign key (fk_empleado)references empleado(id_empleado)
)engine=innodb;