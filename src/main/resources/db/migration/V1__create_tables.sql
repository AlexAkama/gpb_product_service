CREATE TABLE product
(
    id           bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    change_date  timestamp without time zone,
    create_time  timestamp without time zone,
    status       character varying(10) COLLATE pg_catalog."default",
    description  character varying(1024) COLLATE pg_catalog."default",
    kilocalories integer,
    name         character varying(100) COLLATE pg_catalog."default",
    PRIMARY KEY (id)
);

CREATE TABLE product_list
(
    id          bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    change_date timestamp without time zone,
    create_time timestamp without time zone,
    status      character varying(10) COLLATE pg_catalog."default",
    name        character varying(255) COLLATE pg_catalog."default",
    PRIMARY KEY (id)
);

CREATE TABLE product_to_list
(
    product_id bigint NOT NULL,
    list_id    bigint NOT NULL,
    PRIMARY KEY (product_id, list_id),
    CONSTRAINT product_fk FOREIGN KEY (product_id) REFERENCES product_list (id),
    CONSTRAINT list_fk FOREIGN KEY (list_id) REFERENCES product (id)
)