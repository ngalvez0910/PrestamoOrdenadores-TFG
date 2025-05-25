--
-- PostgreSQL database dump
--

-- Dumped from database version 12.22
-- Dumped by pg_dump version 15.13 (Debian 15.13-0+deb12u1)

-- Started on 2025-05-25 15:32:48 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.sanciones DROP CONSTRAINT IF EXISTS fk_sancion_user;
ALTER TABLE IF EXISTS ONLY public.sanciones DROP CONSTRAINT IF EXISTS fk_sancion_prestamo;
ALTER TABLE IF EXISTS ONLY public.prestamos DROP CONSTRAINT IF EXISTS fk_prestamo_user;
ALTER TABLE IF EXISTS ONLY public.prestamos DROP CONSTRAINT IF EXISTS fk_prestamo_dispositivo;
ALTER TABLE IF EXISTS ONLY public.incidencias DROP CONSTRAINT IF EXISTS fk_incidencia_user;
ALTER TABLE IF EXISTS ONLY public.dispositivos DROP CONSTRAINT IF EXISTS fk_dispositivo_incidencia;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS usuarios_pkey;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS usuarios_numero_identificacion_key;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS usuarios_guid_key;
ALTER TABLE IF EXISTS ONLY public.usuarios DROP CONSTRAINT IF EXISTS usuarios_email_key;
ALTER TABLE IF EXISTS ONLY public.sanciones DROP CONSTRAINT IF EXISTS sanciones_pkey;
ALTER TABLE IF EXISTS ONLY public.sanciones DROP CONSTRAINT IF EXISTS sanciones_guid_key;
ALTER TABLE IF EXISTS ONLY public.prestamos DROP CONSTRAINT IF EXISTS prestamos_pkey;
ALTER TABLE IF EXISTS ONLY public.prestamos DROP CONSTRAINT IF EXISTS prestamos_guid_key;
ALTER TABLE IF EXISTS ONLY public.prestamos DROP CONSTRAINT IF EXISTS prestamos_dispositivo_id_key;
ALTER TABLE IF EXISTS ONLY public.incidencias DROP CONSTRAINT IF EXISTS incidencias_pkey;
ALTER TABLE IF EXISTS ONLY public.incidencias DROP CONSTRAINT IF EXISTS incidencias_guid_key;
ALTER TABLE IF EXISTS ONLY public.dispositivos DROP CONSTRAINT IF EXISTS dispositivos_pkey;
ALTER TABLE IF EXISTS ONLY public.dispositivos DROP CONSTRAINT IF EXISTS dispositivos_numero_serie_key;
ALTER TABLE IF EXISTS ONLY public.dispositivos DROP CONSTRAINT IF EXISTS dispositivos_incidencia_id_key;
ALTER TABLE IF EXISTS ONLY public.dispositivos DROP CONSTRAINT IF EXISTS dispositivos_guid_key;
ALTER TABLE IF EXISTS public.usuarios ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.sanciones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.prestamos ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.incidencias ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.dispositivos ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.usuarios_id_seq;
DROP TABLE IF EXISTS public.usuarios;
DROP SEQUENCE IF EXISTS public.sanciones_id_seq;
DROP TABLE IF EXISTS public.sanciones;
DROP SEQUENCE IF EXISTS public.prestamos_id_seq;
DROP TABLE IF EXISTS public.prestamos;
DROP SEQUENCE IF EXISTS public.incidencias_id_seq;
DROP TABLE IF EXISTS public.incidencias;
DROP SEQUENCE IF EXISTS public.dispositivos_id_seq;
DROP TABLE IF EXISTS public.dispositivos;
-- *not* dropping schema, since initdb creates it
--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: admin
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 207 (class 1259 OID 16434)
-- Name: dispositivos; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.dispositivos (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    numero_serie character varying(255) NOT NULL,
    componentes character varying(255) NOT NULL,
    estado_dispositivo character varying(255) NOT NULL,
    incidencia_id bigint,
    is_deleted boolean DEFAULT false,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.dispositivos OWNER TO admin;

--
-- TOC entry 206 (class 1259 OID 16432)
-- Name: dispositivos_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.dispositivos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dispositivos_id_seq OWNER TO admin;

--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 206
-- Name: dispositivos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.dispositivos_id_seq OWNED BY public.dispositivos.id;


--
-- TOC entry 205 (class 1259 OID 16413)
-- Name: incidencias; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.incidencias (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    asunto character varying(255) NOT NULL,
    descripcion character varying(255) NOT NULL,
    estado_incidencia character varying(255) NOT NULL,
    user_id bigint,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean DEFAULT false
);


ALTER TABLE public.incidencias OWNER TO admin;

--
-- TOC entry 204 (class 1259 OID 16411)
-- Name: incidencias_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.incidencias_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.incidencias_id_seq OWNER TO admin;

--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 204
-- Name: incidencias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.incidencias_id_seq OWNED BY public.incidencias.id;


--
-- TOC entry 209 (class 1259 OID 16459)
-- Name: prestamos; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.prestamos (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    user_id bigint NOT NULL,
    dispositivo_id bigint NOT NULL,
    estado_prestamo character varying(255) NOT NULL,
    fecha_prestamo date NOT NULL,
    fecha_devolucion date NOT NULL,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean DEFAULT false
);


ALTER TABLE public.prestamos OWNER TO admin;

--
-- TOC entry 208 (class 1259 OID 16457)
-- Name: prestamos_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.prestamos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.prestamos_id_seq OWNER TO admin;

--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 208
-- Name: prestamos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.prestamos_id_seq OWNED BY public.prestamos.id;


--
-- TOC entry 211 (class 1259 OID 16484)
-- Name: sanciones; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.sanciones (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    user_id bigint NOT NULL,
    prestamo_id bigint NOT NULL,
    tipo_sancion character varying(255) NOT NULL,
    fecha_sancion date NOT NULL,
    fecha_fin date,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean DEFAULT false
);


ALTER TABLE public.sanciones OWNER TO admin;

--
-- TOC entry 210 (class 1259 OID 16482)
-- Name: sanciones_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.sanciones_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sanciones_id_seq OWNER TO admin;

--
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 210
-- Name: sanciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.sanciones_id_seq OWNED BY public.sanciones.id;


--
-- TOC entry 203 (class 1259 OID 16387)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    campo_password character varying(255) NOT NULL,
    rol character varying(255) NOT NULL,
    numero_identificacion character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    apellidos character varying(255) NOT NULL,
    curso character varying(255) DEFAULT NULL::character varying,
    tutor character varying(255) DEFAULT NULL::character varying,
    avatar character varying(255) NOT NULL,
    is_activo boolean DEFAULT true,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_login_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean DEFAULT false,
    is_olvidado boolean DEFAULT false
);


ALTER TABLE public.usuarios OWNER TO admin;

--
-- TOC entry 202 (class 1259 OID 16385)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuarios_id_seq OWNER TO admin;

--
-- TOC entry 3130 (class 0 OID 0)
-- Dependencies: 202
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- TOC entry 2936 (class 2604 OID 16505)
-- Name: dispositivos id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos ALTER COLUMN id SET DEFAULT nextval('public.dispositivos_id_seq'::regclass);


--
-- TOC entry 2932 (class 2604 OID 16525)
-- Name: incidencias id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias ALTER COLUMN id SET DEFAULT nextval('public.incidencias_id_seq'::regclass);


--
-- TOC entry 2940 (class 2604 OID 16559)
-- Name: prestamos id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos ALTER COLUMN id SET DEFAULT nextval('public.prestamos_id_seq'::regclass);


--
-- TOC entry 2944 (class 2604 OID 16578)
-- Name: sanciones id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones ALTER COLUMN id SET DEFAULT nextval('public.sanciones_id_seq'::regclass);


--
-- TOC entry 2922 (class 2604 OID 16591)
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- TOC entry 3115 (class 0 OID 16434)
-- Dependencies: 207
-- Data for Name: dispositivos; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.dispositivos (id, guid, numero_serie, componentes, estado_dispositivo, incidencia_id, is_deleted, created_date, updated_date) FROM stdin;
1	ed472271676	1AB123WXYZ	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
2	a1b2c3d4e5f	5CD456QWER	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
3	x9y8z7w6v5u	9EF789TYUI	ratón, cargador	NO_DISPONIBLE	1	t	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
4	m3n4o5p6q7r	3GH012ASDF	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
5	l8k9j0h1g2f	7JK345ZXCV	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
6	t5s4r3q2p1o	2LM678POIU	ratón, cargador	NO_DISPONIBLE	2	t	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
7	d7e8f9g0h1i	8NO901LKJH	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
8	v2w3x4y5z6a	4PQ234MNBV	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
9	c6b5a4z3y2x	6RS567QAZX	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
10	n0m9l8k7j6i	0TU890WSXC	ratón, cargador	NO_DISPONIBLE	3	t	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
11	q8p7o6n5m4l	5VW123ERTY	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
12	b1a2z3y4x5w	1XY234CDEF	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
13	k6l5m4n3o2p	6ZT345GHIJ	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
14	f9e8d7c6b5a	9UA678BCDE	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
15	r2s1t0u9v8w	2VB789CDEF	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
16	j5i4h3g2f1e	5WC012EFGH	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
17	h8g7f6e5d4c	8XD345FGHI	ratón, cargador	PRESTADO	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
18	g1f2e3d4c5b	1YE678HIJK	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
19	e4d5c6b7a8z	4ZF901IJKL	ratón, cargador	DISPONIBLE	\N	f	2025-05-25 15:32:36.698989	2025-05-25 15:32:36.698989
\.


--
-- TOC entry 3113 (class 0 OID 16413)
-- Dependencies: 205
-- Data for Name: incidencias; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.incidencias (id, guid, asunto, descripcion, estado_incidencia, user_id, created_date, updated_date, is_deleted) FROM stdin;
1	INC000003	Cargador roto	El cargador esta despeluchado	PENDIENTE	1	2025-05-25 15:32:36.674454	2025-05-25 15:32:36.674454	f
2	INC000006	Tecla W rota	La tecla W esta levantada y no se puede volver a colocar	PENDIENTE	2	2025-05-25 15:32:36.674454	2025-05-25 15:32:36.674454	f
3	INC000010	Virus	El ordenador tiene un virus	PENDIENTE	2	2025-05-25 15:32:36.674454	2025-05-25 15:32:36.674454	f
\.


--
-- TOC entry 3117 (class 0 OID 16459)
-- Dependencies: 209
-- Data for Name: prestamos; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.prestamos (id, guid, user_id, dispositivo_id, estado_prestamo, fecha_prestamo, fecha_devolucion, created_date, updated_date, is_deleted) FROM stdin;
1	938012eccbd	1	2	VENCIDO	2025-04-01	2025-04-15	2025-04-01 10:00:00	2025-04-16 09:00:00	f
2	456789abcde	1	4	VENCIDO	2025-04-10	2025-04-24	2025-04-10 10:00:00	2025-04-25 09:00:00	f
3	123456789ab	2	5	VENCIDO	2024-12-01	2024-12-15	2024-12-01 10:00:00	2024-12-16 09:00:00	f
4	987654321cd	2	7	VENCIDO	2024-12-10	2024-12-24	2024-12-10 10:00:00	2024-12-25 09:00:00	f
5	abcdefg123	2	8	VENCIDO	2025-02-15	2025-03-01	2025-02-15 10:00:00	2025-03-02 09:00:00	f
6	hijklmn456	2	9	VENCIDO	2025-02-20	2025-03-06	2025-02-20 10:00:00	2025-03-07 09:00:00	f
7	opqrsuv789	3	11	VENCIDO	2025-04-20	2025-05-04	2025-04-20 10:00:00	2025-05-05 09:00:00	f
8	zxcvbnm012	4	14	DEVUELTO	2025-04-01	2025-04-15	2025-04-01 10:00:00	2025-04-14 09:00:00	f
9	asdfghj345	5	17	EN_CURSO	2025-05-10	2025-05-24	2025-05-10 10:00:00	2025-05-10 09:00:00	f
\.


--
-- TOC entry 3119 (class 0 OID 16484)
-- Dependencies: 211
-- Data for Name: sanciones; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.sanciones (id, guid, user_id, prestamo_id, tipo_sancion, fecha_sancion, fecha_fin, created_date, updated_date, is_deleted) FROM stdin;
1	SANC000001	2	3	ADVERTENCIA	2024-12-20	\N	2024-12-20 10:00:00	2024-12-20 10:00:00	f
2	SANC000002	2	4	ADVERTENCIA	2024-12-29	\N	2024-12-29 11:00:00	2024-12-29 11:00:00	f
3	SANC000003	2	4	BLOQUEO_TEMPORAL	2024-12-29	2025-02-28	2024-12-29 12:00:00	2024-12-29 12:00:00	f
4	SANC000004	2	5	ADVERTENCIA	2025-03-06	\N	2025-03-06 10:00:00	2025-03-06 10:00:00	f
5	SANC000005	2	6	ADVERTENCIA	2025-03-11	\N	2025-03-11 11:00:00	2025-03-11 11:00:00	f
6	SANC000006	2	6	BLOQUEO_TEMPORAL	2025-03-11	2025-05-11	2025-03-11 12:00:00	2025-03-11 12:00:00	f
\.


--
-- TOC entry 3111 (class 0 OID 16387)
-- Dependencies: 203
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.usuarios (id, guid, email, campo_password, rol, numero_identificacion, nombre, apellidos, curso, tutor, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date, is_deleted, is_olvidado) FROM stdin;
1	3854b5ba26c	juan.loantech@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2015LT849	Juan	Pérez	1º Bachillerato	María Gómez	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
2	c1f40bb1900	maria.loantech.profesor@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	PROFESOR	2023LT044	María	Gómez	1º Bachillerato	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
3	a1b2c3d4e5f	ana.lopez.loantech@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2016LT101	Ana	López	2º Bachillerato	Laura Vidal	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
4	f5e4d3c2b1a	david.garcia.loantech@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2017LT202	David	García	1º ESO	Pedro Ramos	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
5	abcdef12345	sofia.rodriguez.loantech@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2018LT303	Sofía	Rodríguez	3º ESO	Laura Vidal	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
6	9z8y7x6w5v4	martin.sanz.loantech@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2015LT404	Martín	Sanz	1º Bachillerato	María Gómez	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
7	fedcba98765	laura.vidal.loantech.profesor@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	PROFESOR	2020LTP01	Laura	Vidal	2º Bachillerato	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
8	54321abcde	pedro.ramos.loantech.profesor@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	PROFESOR	2019LTP02	Pedro	Ramos	1º ESO, 3º ESO	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
9	1a2b3c4d5e6	elena.diaz.loantech.profesor@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	PROFESOR	2021LTP03	Elena	Díaz	FP Grado Medio	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
10	0d6d031ad0a	admin.loantech.admin@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ADMIN	2010LT295	Admin	User	\N	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
11	f768ece2a79	ailatan0910@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ADMIN	2000LT214	Aila	Tan	\N	\N	/assets/avatars/avatarAzul.png	t	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	2025-05-25 15:32:36.658001	f	f
\.


--
-- TOC entry 3131 (class 0 OID 0)
-- Dependencies: 206
-- Name: dispositivos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.dispositivos_id_seq', 19, true);


--
-- TOC entry 3132 (class 0 OID 0)
-- Dependencies: 204
-- Name: incidencias_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.incidencias_id_seq', 3, true);


--
-- TOC entry 3133 (class 0 OID 0)
-- Dependencies: 208
-- Name: prestamos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.prestamos_id_seq', 9, true);


--
-- TOC entry 3134 (class 0 OID 0)
-- Dependencies: 210
-- Name: sanciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.sanciones_id_seq', 6, true);


--
-- TOC entry 3135 (class 0 OID 0)
-- Dependencies: 202
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 11, true);


--
-- TOC entry 2961 (class 2606 OID 16524)
-- Name: dispositivos dispositivos_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_guid_key UNIQUE (guid);


--
-- TOC entry 2963 (class 2606 OID 16451)
-- Name: dispositivos dispositivos_incidencia_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_incidencia_id_key UNIQUE (incidencia_id);


--
-- TOC entry 2965 (class 2606 OID 16449)
-- Name: dispositivos dispositivos_numero_serie_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_numero_serie_key UNIQUE (numero_serie);


--
-- TOC entry 2967 (class 2606 OID 16507)
-- Name: dispositivos dispositivos_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_pkey PRIMARY KEY (id);


--
-- TOC entry 2957 (class 2606 OID 16558)
-- Name: incidencias incidencias_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT incidencias_guid_key UNIQUE (guid);


--
-- TOC entry 2959 (class 2606 OID 16527)
-- Name: incidencias incidencias_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT incidencias_pkey PRIMARY KEY (id);


--
-- TOC entry 2969 (class 2606 OID 16471)
-- Name: prestamos prestamos_dispositivo_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_dispositivo_id_key UNIQUE (dispositivo_id);


--
-- TOC entry 2971 (class 2606 OID 16574)
-- Name: prestamos prestamos_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_guid_key UNIQUE (guid);


--
-- TOC entry 2973 (class 2606 OID 16561)
-- Name: prestamos prestamos_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_pkey PRIMARY KEY (id);


--
-- TOC entry 2975 (class 2606 OID 16587)
-- Name: sanciones sanciones_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT sanciones_guid_key UNIQUE (guid);


--
-- TOC entry 2977 (class 2606 OID 16580)
-- Name: sanciones sanciones_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT sanciones_pkey PRIMARY KEY (id);


--
-- TOC entry 2949 (class 2606 OID 16408)
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- TOC entry 2951 (class 2606 OID 16621)
-- Name: usuarios usuarios_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_guid_key UNIQUE (guid);


--
-- TOC entry 2953 (class 2606 OID 16623)
-- Name: usuarios usuarios_numero_identificacion_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_numero_identificacion_key UNIQUE (numero_identificacion);


--
-- TOC entry 2955 (class 2606 OID 16593)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 2979 (class 2606 OID 16528)
-- Name: dispositivos fk_dispositivo_incidencia; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT fk_dispositivo_incidencia FOREIGN KEY (incidencia_id) REFERENCES public.incidencias(id);


--
-- TOC entry 2978 (class 2606 OID 16594)
-- Name: incidencias fk_incidencia_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT fk_incidencia_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 2980 (class 2606 OID 16508)
-- Name: prestamos fk_prestamo_dispositivo; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT fk_prestamo_dispositivo FOREIGN KEY (dispositivo_id) REFERENCES public.dispositivos(id);


--
-- TOC entry 2981 (class 2606 OID 16599)
-- Name: prestamos fk_prestamo_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT fk_prestamo_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 2982 (class 2606 OID 16562)
-- Name: sanciones fk_sancion_prestamo; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT fk_sancion_prestamo FOREIGN KEY (prestamo_id) REFERENCES public.prestamos(id);


--
-- TOC entry 2983 (class 2606 OID 16604)
-- Name: sanciones fk_sancion_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT fk_sancion_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: admin
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-05-25 15:32:48 UTC

--
-- PostgreSQL database dump complete
--

