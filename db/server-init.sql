CREATE SCHEMA product;
CREATE TABLE product.products (
	id text NOT NULL,
	designation text NOT NULL,
	stock int4 NOT NULL,
    picture_url text NOT NULL,
    blueprint_url text,
    weight float8 NOT NULL,
    volume float8 NOT NULL,
    obsolete boolean NOT NULL,
	CONSTRAINT pk_product PRIMARY KEY (id)
);
