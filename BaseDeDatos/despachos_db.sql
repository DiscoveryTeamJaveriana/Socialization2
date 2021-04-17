-- Database: despachos_db
-- DROP DATABASE despachos_db;
CREATE DATABASE despachos_db
    WITH 
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- SCHEMA: despachos
-- DROP SCHEMA despachos ;

CREATE SCHEMA despachos AUTHORIZATION discoveryteam;

-- Table: despachos.usuario
--DROP TABLE despachos.usuario;
CREATE TABLE despachos.usuario
(
    apellidos character varying(50) COLLATE pg_catalog."default",
    correo character varying(70) COLLATE pg_catalog."default",
    direccion character varying(100) COLLATE pg_catalog."default",
    id bigint NOT NULL,
    rol character varying(20) COLLATE pg_catalog."default",
    nombreUsuario character varying(20) COLLATE pg_catalog."default",
    nombres character varying(50) COLLATE pg_catalog."default",
    telefono character varying(13) COLLATE pg_catalog."default",
    contrasena character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT usuario_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE despachos.usuario
    OWNER to discoveryteam;

-- Table: despachos.despacho
--DROP TABLE despachos.despacho;
CREATE TABLE despachos.despacho
(
    id bigint NOT NULL,
    "cantidadCajas" bigint,
    "pesoTotal" numeric,
    estado character varying(20) COLLATE pg_catalog."default",
    "idUsuarioDestino" bigint,
    CONSTRAINT despacho_pkey PRIMARY KEY (id),
    CONSTRAINT "idUsuarioDestino" FOREIGN KEY ("idUsuarioDestino")
        REFERENCES despachos.usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE despachos.despacho
    OWNER to discoveryteam;
	
-- Table: despachos.oferta
--DROP TABLE despachos.oferta;
CREATE TABLE despachos.oferta
(
    id bigint NOT NULL,
    "idDespacho" bigint,
    "idUsuarioTransporte" bigint,
    oferta numeric,
    CONSTRAINT oferta_pkey PRIMARY KEY (id),
    CONSTRAINT "idDespacho" FOREIGN KEY ("idDespacho")
        REFERENCES despachos.despacho (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "idUsuarioTransporte" FOREIGN KEY ("idUsuarioTransporte")
        REFERENCES despachos.usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE despachos.oferta
    OWNER to discoveryteam;
