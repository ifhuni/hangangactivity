-- public.company definition

-- Drop table

-- DROP TABLE public.company;

CREATE TABLE public.company (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	registration_number varchar(50) NULL,
	established_date date NULL,
	industry_type varchar(100) NULL,
	phone varchar(50) NULL,
	email varchar(255) NULL,
	address text NULL,
	website varchar(255) NULL,
	ceo_name varchar(100) NULL,
	employee_count int4 NULL,
	revenue numeric(15, 2) NULL,
	status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
	created_at timestamp DEFAULT now() NULL,
	updated_at timestamp DEFAULT now() NULL,
	CONSTRAINT company_email_key UNIQUE (email),
	CONSTRAINT company_pkey PRIMARY KEY (id),
	CONSTRAINT company_registration_number_key UNIQUE (registration_number)
);

-- Table Triggers

create trigger trigger_update_modified before
update
    on
    public.company for each row execute function update_modified_column();