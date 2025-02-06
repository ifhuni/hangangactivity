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

COMMENT ON COLUMN public.company.id IS '회사 고유 ID (자동 증가)';
COMMENT ON COLUMN public.company."name" IS '회사명';
COMMENT ON COLUMN public.company.registration_number IS '사업자 등록번호';
COMMENT ON COLUMN public.company.established_date IS '설립일';
COMMENT ON COLUMN public.company.industry_type IS '산업 유형';
COMMENT ON COLUMN public.company.phone IS '전화번호';
COMMENT ON COLUMN public.company.email IS '이메일';
COMMENT ON COLUMN public.company.address IS '주소';
COMMENT ON COLUMN public.company.website IS '웹사이트';
COMMENT ON COLUMN public.company.ceo_name IS '대표자 이름';
COMMENT ON COLUMN public.company.employee_count IS '직원 수';
COMMENT ON COLUMN public.company.revenue IS '연 매출 (금액)';
COMMENT ON COLUMN public.company.status IS '회사 상태 (기본값: ACTIVE)';
COMMENT ON COLUMN public.company.created_at IS '생성일';
COMMENT ON COLUMN public.company.updated_at IS '수정일';

create trigger trigger_update_modified before
update
    on
    public.company for each row execute function update_modified_column();

CREATE TABLE public.users (
	id serial4 NOT NULL,
	username varchar(50) NOT NULL,
	"password" varchar(255) NOT NULL,
	email varchar(255) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_username_key UNIQUE (username)
);

COMMENT ON COLUMN public.users.id IS '사용자 고유 ID (자동 증가)';
COMMENT ON COLUMN public.users.username IS '사용자명';
COMMENT ON COLUMN public.users."password" IS '비밀번호';
COMMENT ON COLUMN public.users.email IS '이메일';

CREATE TABLE public.program (
    id bigserial NOT NULL,
    company_id bigint NOT NULL,
    "name" varchar(255) NOT NULL,
    description text NULL,
    start_date date NULL,
    end_date date NULL,
    status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
    created_at timestamp DEFAULT now() NULL,
    updated_at timestamp DEFAULT now() NULL,
    CONSTRAINT program_pkey PRIMARY KEY (id),
    CONSTRAINT program_company_id_fkey FOREIGN KEY (company_id) REFERENCES public.company (id) ON DELETE CASCADE
);

COMMENT ON COLUMN public.program.id IS '프로그램 고유 ID (자동 증가)';
COMMENT ON COLUMN public.program.company_id IS '관련된 회사 ID (외래 키)';
COMMENT ON COLUMN public.program."name" IS '프로그램명';
COMMENT ON COLUMN public.program.description IS '프로그램 설명';
COMMENT ON COLUMN public.program.start_date IS '시작 날짜';
COMMENT ON COLUMN public.program.end_date IS '종료 날짜';
COMMENT ON COLUMN public.program.status IS '프로그램 상태 (기본값: ACTIVE)';
COMMENT ON COLUMN public.program.created_at IS '생성일';
COMMENT ON COLUMN public.program.updated_at IS '수정일';
