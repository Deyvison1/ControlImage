-- file.image definition

CREATE TABLE IF NOT EXISTS file.image (
    id uuid NOT NULL,
    filename varchar(255),
    bucket varchar(255) NOT NULL,
    object_key varchar(512) NOT NULL,
    content_type varchar(255),
    size int8,
    active boolean NOT NULL DEFAULT true,
    creation_Date timestamp(6) NOT NULL,
    update_date timestamp(6),
    creation_user varchar(255) NOT NULL,
    update_user varchar(255),

    CONSTRAINT image_pkey PRIMARY KEY (id)
);

-- Permissions
ALTER TABLE file.image OWNER TO postgres;
GRANT ALL ON TABLE file.image TO postgres;