create table usuarios(
    id bigint not null auto_increment,
    login varchar(100) not null,
    clave varchar(300) not null unique,
    primary key(id)
    );
insert into usuarios values(1, 'ana.souza@voll.med', '$2a$10$Y50UaMFOxteibQEYLrwuHeehHYfcoafCopUazP12.rqB41bsolF5.');