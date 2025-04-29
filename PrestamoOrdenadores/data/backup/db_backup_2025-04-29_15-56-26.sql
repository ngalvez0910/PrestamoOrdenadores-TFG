--
-- PostgreSQL database dump
--

-- Dumped from database version 12.22
-- Dumped by pg_dump version 17.4

-- Started on 2025-04-29 15:56:26

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: admin
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 207 (class 1259 OID 140437)
-- Name: dispositivos; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.dispositivos (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    numero_serie character varying(255) NOT NULL,
    componentes character varying(255) NOT NULL,
    estado_dispositivo character varying(255) NOT NULL,
    incidencia_id bigint,
    is_activo boolean DEFAULT true,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.dispositivos OWNER TO admin;

--
-- TOC entry 206 (class 1259 OID 140435)
-- Name: dispositivos_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.dispositivos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.dispositivos_id_seq OWNER TO admin;

--
-- TOC entry 3120 (class 0 OID 0)
-- Dependencies: 206
-- Name: dispositivos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.dispositivos_id_seq OWNED BY public.dispositivos.id;


--
-- TOC entry 205 (class 1259 OID 140417)
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
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.incidencias OWNER TO admin;

--
-- TOC entry 204 (class 1259 OID 140415)
-- Name: incidencias_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.incidencias_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.incidencias_id_seq OWNER TO admin;

--
-- TOC entry 3121 (class 0 OID 0)
-- Dependencies: 204
-- Name: incidencias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.incidencias_id_seq OWNED BY public.incidencias.id;


--
-- TOC entry 209 (class 1259 OID 140462)
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
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.prestamos OWNER TO admin;

--
-- TOC entry 208 (class 1259 OID 140460)
-- Name: prestamos_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.prestamos_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prestamos_id_seq OWNER TO admin;

--
-- TOC entry 3122 (class 0 OID 0)
-- Dependencies: 208
-- Name: prestamos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.prestamos_id_seq OWNED BY public.prestamos.id;


--
-- TOC entry 211 (class 1259 OID 140486)
-- Name: sanciones; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.sanciones (
    id bigint NOT NULL,
    guid character varying(255) NOT NULL,
    user_id bigint NOT NULL,
    tipo_sancion character varying(255) NOT NULL,
    fecha_sancion date NOT NULL,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.sanciones OWNER TO admin;

--
-- TOC entry 210 (class 1259 OID 140484)
-- Name: sanciones_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.sanciones_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sanciones_id_seq OWNER TO admin;

--
-- TOC entry 3123 (class 0 OID 0)
-- Dependencies: 210
-- Name: sanciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.sanciones_id_seq OWNED BY public.sanciones.id;


--
-- TOC entry 203 (class 1259 OID 140393)
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
    is_activo boolean DEFAULT false,
    created_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_login_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_password_reset_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.usuarios OWNER TO admin;

--
-- TOC entry 202 (class 1259 OID 140391)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO admin;

--
-- TOC entry 3124 (class 0 OID 0)
-- Dependencies: 202
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- TOC entry 2933 (class 2604 OID 140501)
-- Name: dispositivos id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos ALTER COLUMN id SET DEFAULT nextval('public.dispositivos_id_seq'::regclass);


--
-- TOC entry 2930 (class 2604 OID 140521)
-- Name: incidencias id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias ALTER COLUMN id SET DEFAULT nextval('public.incidencias_id_seq'::regclass);


--
-- TOC entry 2937 (class 2604 OID 140555)
-- Name: prestamos id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos ALTER COLUMN id SET DEFAULT nextval('public.prestamos_id_seq'::regclass);


--
-- TOC entry 2940 (class 2604 OID 140569)
-- Name: sanciones id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones ALTER COLUMN id SET DEFAULT nextval('public.sanciones_id_seq'::regclass);


--
-- TOC entry 2922 (class 2604 OID 140582)
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- TOC entry 3109 (class 0 OID 140437)
-- Dependencies: 207
-- Data for Name: dispositivos; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.dispositivos (id, guid, numero_serie, componentes, estado_dispositivo, incidencia_id, is_activo, created_date, updated_date) FROM stdin;
2	a1b2c3d4e5f	5CD456QWER	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
3	x9y8z7w6v5u	9EF789TYUI	ratón, cargador	NO_DISPONIBLE	1	f	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
5	l8k9j0h1g2f	7JK345ZXCV	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
6	t5s4r3q2p1o	2LM678POIU	ratón, cargador	NO_DISPONIBLE	2	f	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
8	v2w3x4y5z6a	4PQ234MNBV	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
10	n0m9l8k7j6i	0TU890WSXC	ratón, cargador	NO_DISPONIBLE	3	f	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
4	m3n4o5p6q7r	3GH012ASDF	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
1	ed472271676	1AB123WXYZ	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
7	d7e8f9g0h1i	8NO901LKJH	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
9	c6b5a4z3y2x	6RS567QAZX	ratón, cargador	PRESTADO	\N	t	2025-04-26 11:52:04.95035	2025-04-26 11:52:04.95035
\.


--
-- TOC entry 3107 (class 0 OID 140417)
-- Dependencies: 205
-- Data for Name: incidencias; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.incidencias (id, guid, asunto, descripcion, estado_incidencia, user_id, created_date, updated_date) FROM stdin;
1	INC000003	Cargador roto	El cargador esta despeluchado	PENDIENTE	1	2025-04-26 11:52:04.932679	2025-04-26 11:52:04.932679
2	INC000006	Tecla W rota	La tecla W esta levantada y no se puede volver a colocar	PENDIENTE	2	2025-04-26 11:52:04.932679	2025-04-26 11:52:04.932679
3	INC000010	Virus	El ordenador tiene un virus	PENDIENTE	2	2025-04-26 11:52:04.932679	2025-04-26 11:52:04.932679
\.


--
-- TOC entry 3111 (class 0 OID 140462)
-- Dependencies: 209
-- Data for Name: prestamos; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.prestamos (id, guid, user_id, dispositivo_id, estado_prestamo, fecha_prestamo, fecha_devolucion, created_date, updated_date) FROM stdin;
1	938012eccbd	1	2	EN_CURSO	2025-04-26	2025-05-17	2025-04-26 11:50:28.983847	2025-04-26 11:50:28.983847
2	oTfk7nHmljX	4	4	EN_CURSO	2025-04-26	2025-05-17	2025-04-26 11:53:44.587382	2025-04-26 11:53:44.587382
3	E17TBOt19x7	4	1	EN_CURSO	2025-04-26	2025-05-17	2025-04-26 13:16:28.99027	2025-04-26 13:16:28.99027
4	uiXny3e0LwE	4	7	EN_CURSO	2025-04-26	2025-05-17	2025-04-26 13:52:27.930597	2025-04-26 13:52:27.930597
5	K5weqzoHhaO	4	9	EN_CURSO	2025-04-26	2025-05-17	2025-04-26 14:07:31.180071	2025-04-26 14:07:31.180071
\.


--
-- TOC entry 3113 (class 0 OID 140486)
-- Dependencies: 211
-- Data for Name: sanciones; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.sanciones (id, guid, user_id, tipo_sancion, fecha_sancion, created_date, updated_date) FROM stdin;
1	SANC000001	1	ADVERTENCIA	2025-04-26	2025-04-26 11:50:29.006676	2025-04-26 11:50:29.006676
\.


--
-- TOC entry 3105 (class 0 OID 140393)
-- Dependencies: 203
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.usuarios (id, guid, email, campo_password, rol, numero_identificacion, nombre, apellidos, curso, tutor, avatar, is_activo, created_date, updated_date, last_login_date, last_password_reset_date) FROM stdin;
1	3854b5ba26c	juan@loantech.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ALUMNO	2015LT849	Juan	Pérez	1º Bachillerato	María Gómez	avatar1.png	t	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767
2	c1f40bb1900	maria@profesor.loantech.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	PROFESOR	2023LT044	María	Gómez	1º Bachillerato	\N	avatar2.png	t	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767
3	0d6d031ad0a	admin@admin.loantech.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ADMIN	2010LT295	Admin	User	\N	\N	avatar3.png	t	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767
4	f768ece2a79	ailatan0910@gmail.com	$2a$12$pwhykP.03H8de9whL58AzO2ZpuxZoS1O1KSGesjW..zndYFxu0wB2	ADMIN	2000LT214	Aila	Tan	\N	\N	avatar4.png	t	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767	2025-04-26 11:52:04.919767
\.


--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 206
-- Name: dispositivos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.dispositivos_id_seq', 10, true);


--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 204
-- Name: incidencias_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.incidencias_id_seq', 3, true);


--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 208
-- Name: prestamos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.prestamos_id_seq', 5, true);


--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 210
-- Name: sanciones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.sanciones_id_seq', 1, true);


--
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 202
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 4, true);


--
-- TOC entry 2956 (class 2606 OID 140520)
-- Name: dispositivos dispositivos_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_guid_key UNIQUE (guid);


--
-- TOC entry 2958 (class 2606 OID 140454)
-- Name: dispositivos dispositivos_incidencia_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_incidencia_id_key UNIQUE (incidencia_id);


--
-- TOC entry 2960 (class 2606 OID 140452)
-- Name: dispositivos dispositivos_numero_serie_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_numero_serie_key UNIQUE (numero_serie);


--
-- TOC entry 2962 (class 2606 OID 140503)
-- Name: dispositivos dispositivos_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT dispositivos_pkey PRIMARY KEY (id);


--
-- TOC entry 2952 (class 2606 OID 140554)
-- Name: incidencias incidencias_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT incidencias_guid_key UNIQUE (guid);


--
-- TOC entry 2954 (class 2606 OID 140523)
-- Name: incidencias incidencias_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT incidencias_pkey PRIMARY KEY (id);


--
-- TOC entry 2964 (class 2606 OID 140473)
-- Name: prestamos prestamos_dispositivo_id_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_dispositivo_id_key UNIQUE (dispositivo_id);


--
-- TOC entry 2966 (class 2606 OID 140565)
-- Name: prestamos prestamos_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_guid_key UNIQUE (guid);


--
-- TOC entry 2968 (class 2606 OID 140557)
-- Name: prestamos prestamos_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT prestamos_pkey PRIMARY KEY (id);


--
-- TOC entry 2970 (class 2606 OID 140578)
-- Name: sanciones sanciones_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT sanciones_guid_key UNIQUE (guid);


--
-- TOC entry 2972 (class 2606 OID 140571)
-- Name: sanciones sanciones_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT sanciones_pkey PRIMARY KEY (id);


--
-- TOC entry 2944 (class 2606 OID 140412)
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- TOC entry 2946 (class 2606 OID 140612)
-- Name: usuarios usuarios_guid_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_guid_key UNIQUE (guid);


--
-- TOC entry 2948 (class 2606 OID 140614)
-- Name: usuarios usuarios_numero_identificacion_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_numero_identificacion_key UNIQUE (numero_identificacion);


--
-- TOC entry 2950 (class 2606 OID 140584)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 2974 (class 2606 OID 140524)
-- Name: dispositivos fk_dispositivo_incidencia; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.dispositivos
    ADD CONSTRAINT fk_dispositivo_incidencia FOREIGN KEY (incidencia_id) REFERENCES public.incidencias(id);


--
-- TOC entry 2973 (class 2606 OID 140585)
-- Name: incidencias fk_incidencia_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.incidencias
    ADD CONSTRAINT fk_incidencia_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 2975 (class 2606 OID 140504)
-- Name: prestamos fk_prestamo_dispositivo; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT fk_prestamo_dispositivo FOREIGN KEY (dispositivo_id) REFERENCES public.dispositivos(id);


--
-- TOC entry 2976 (class 2606 OID 140590)
-- Name: prestamos fk_prestamo_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.prestamos
    ADD CONSTRAINT fk_prestamo_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 2977 (class 2606 OID 140595)
-- Name: sanciones fk_sancion_user; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sanciones
    ADD CONSTRAINT fk_sancion_user FOREIGN KEY (user_id) REFERENCES public.usuarios(id);


--
-- TOC entry 3119 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: admin
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-04-29 15:56:26

--
-- PostgreSQL database dump complete
--

