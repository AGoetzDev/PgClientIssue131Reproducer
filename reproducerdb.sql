--
-- PostgreSQL database dump
--

-- Dumped from database version 10.0
-- Dumped by pg_dump version 10.0

-- Started on 2018-08-30 00:25:13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12924)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2800 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 197 (class 1259 OID 18364)
-- Name: reproducer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE reproducer (
    id bigint NOT NULL,
    integerfield integer NOT NULL
);


ALTER TABLE reproducer OWNER TO postgres;

--
-- TOC entry 196 (class 1259 OID 18362)
-- Name: reproducer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE reproducer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE reproducer_id_seq OWNER TO postgres;

--
-- TOC entry 2801 (class 0 OID 0)
-- Dependencies: 196
-- Name: reproducer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE reproducer_id_seq OWNED BY reproducer.id;


--
-- TOC entry 2670 (class 2604 OID 18367)
-- Name: reproducer id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reproducer ALTER COLUMN id SET DEFAULT nextval('reproducer_id_seq'::regclass);


--
-- TOC entry 2672 (class 2606 OID 18369)
-- Name: reproducer reproducer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY reproducer
    ADD CONSTRAINT reproducer_pkey PRIMARY KEY (id);


-- Completed on 2018-08-30 00:25:13

--
-- PostgreSQL database dump complete
--

