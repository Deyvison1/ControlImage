-- file.image definição

-- Drop table

-- DROP TABLE file.image;

CREATE TABLE IF NOT EXISTS file.image (
	active bool NOT NULL,
	created_date timestamp(6) NULL,
	last_modified_date timestamp(6) NULL,
	"size" int8 NULL,
	id uuid NOT NULL,
	content_type varchar(255) NULL,
	created_by varchar(255) NULL,
	filename varchar(255) NULL,
	last_modified_by varchar(255) NULL,
	url varchar(255) NULL,
	CONSTRAINT image_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE file.image OWNER TO postgres;
GRANT ALL ON TABLE file.image TO postgres;