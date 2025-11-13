-- control_images.image definição

-- Drop table

-- DROP TABLE control_images.image;

CREATE TABLE IF NOT EXISTS control_images.image (
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

ALTER TABLE control_images.image OWNER TO postgres;
GRANT ALL ON TABLE control_images.image TO postgres;