-- company 테이블
CREATE TABLE company (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    owner_name  VARCHAR(255) NOT NULL,
    contact     VARCHAR(50)  NOT NULL,
    address     VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE company IS '업체 정보';
COMMENT ON COLUMN company.id IS '업체 ID (자동 증가)';
COMMENT ON COLUMN company.name IS '업체명';
COMMENT ON COLUMN company.owner_name IS '대표자명';
COMMENT ON COLUMN company.contact IS '업체 연락처';
COMMENT ON COLUMN company.address IS '업체 주소';
COMMENT ON COLUMN company.created_at IS '등록일';

-- users 테이블
CREATE TABLE public.users (
	id serial4 NOT NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NOT NULL,
	"name" varchar(255) NOT NULL,
	phone varchar(50) NULL,
	"role" varchar(10) NOT NULL,
	company_id int4 NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['TOURIST'::character varying, 'COMPANY'::character varying, 'ADMIN'::character varying])::text[]))),
	CONSTRAINT users_company_id_fkey FOREIGN KEY (company_id) REFERENCES public.company(id) ON DELETE SET NULL
);

COMMENT ON TABLE users IS '회원 정보';
COMMENT ON COLUMN users.id IS '회원 ID (자동 증가)';
COMMENT ON COLUMN users.email IS '회원 이메일 (로그인 ID)';
COMMENT ON COLUMN users.password IS '비밀번호';
COMMENT ON COLUMN users.name IS '회원 이름';
COMMENT ON COLUMN users.phone IS '연락처';
COMMENT ON COLUMN users.role IS '회원 유형 (TOURIST: 관광객, COMPANY: 업체)';
COMMENT ON COLUMN users.company_id IS '소속 업체 ID (업체 회원일 경우)';
COMMENT ON COLUMN users.created_at IS '가입일';

-- program 테이블
CREATE TABLE program (
    id          SERIAL PRIMARY KEY,
    company_id  INT NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    capacity    INT NOT NULL,
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

COMMENT ON TABLE program IS '프로그램 정보';
COMMENT ON COLUMN program.id IS '프로그램 ID (자동 증가)';
COMMENT ON COLUMN program.company_id IS '업체 ID (이 프로그램을 등록한 업체)';
COMMENT ON COLUMN program.title IS '프로그램명';
COMMENT ON COLUMN program.description IS '프로그램 설명';
COMMENT ON COLUMN program.price IS '가격';
COMMENT ON COLUMN program.capacity IS '최대 예약 가능 인원';
COMMENT ON COLUMN program.start_time IS '프로그램 시작 시간';
COMMENT ON COLUMN program.end_time IS '프로그램 종료 시간';
COMMENT ON COLUMN program.created_at IS '등록일';

-- reservation 테이블
CREATE TABLE reservation (
    id          SERIAL PRIMARY KEY,
    users_id     INT NOT NULL,
    program_id  INT NOT NULL,
    reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status      VARCHAR(10) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELED')) DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (users_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE
);

COMMENT ON TABLE reservation IS '예약 정보';
COMMENT ON COLUMN reservation.id IS '예약 ID (자동 증가)';
COMMENT ON COLUMN reservation.users_id IS '예약한 회원 ID';
COMMENT ON COLUMN reservation.program_id IS '예약한 프로그램 ID';
COMMENT ON COLUMN reservation.reserved_at IS '예약한 날짜 및 시간';
COMMENT ON COLUMN reservation.status IS '예약 상태 (PENDING: 대기, CONFIRMED: 확정, CANCELED: 취소)';
COMMENT ON COLUMN reservation.created_at IS '예약 생성일';


INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'admin', 'password123', 'admin', '010-1111-1111', 'ADMIN', NULL, '2025-02-09 01:34:05.325');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'tourist1@example.com', 'password123', '이덕현', '010-1111-1111', 'TOURIST', NULL, '2025-02-09 01:29:03.004');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'tourist2@example.com', 'password123', '이영희', '010-2222-2222', 'TOURIST', NULL, '2025-02-09 01:29:03.004');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'tourist3@example.com', 'password123', '박민준', '010-3333-3333', 'TOURIST', NULL, '2025-02-09 01:29:03.004');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'tourist4@example.com', 'password123', '최지우', '010-4444-4444', 'TOURIST', NULL, '2025-02-09 01:29:03.004');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'tourist5@example.com', 'password123', '한가을', '010-5555-5555', 'TOURIST', NULL, '2025-02-09 01:29:03.004');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'company4@example.com', 'password123', '업체4 대표', '010-9999-9999', 'COMPANY', NULL, '2025-02-09 01:29:45.895');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'company5@example.com', 'password123', '업체5 대표', '010-0000-0000', 'COMPANY', NULL, '2025-02-09 01:29:45.895');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'company1@example.com', 'password123', '업체1 대표', '010-6666-6666', 'COMPANY', 1, '2025-02-09 01:29:31.042');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'company2@example.com', 'password123', '업체2 대표', '010-7777-7777', 'COMPANY', 2, '2025-02-09 01:29:45.895');
INSERT INTO public.users
( email, "password", "name", phone, "role", company_id, created_at)
VALUES( 'company3@example.com', 'password123', '업체3 대표', '010-8888-8888', 'COMPANY', 3, '2025-02-09 01:29:45.895');

INSERT INTO public.company
(id, "name", owner_name, contact, address, created_at)
VALUES(1, '클라이머스', '이덕현', '010-1234-5678', 'Seoul, Gangnam-gu, Teheran-ro 123', '2025-02-09 10:00:00.000');
INSERT INTO public.company
(id, "name", owner_name, contact, address, created_at)
VALUES(2, '패들보드', '아쿠아맨', '010-9876-5432', 'Seoul, Mapo-gu, Hongdae 456', '2025-02-08 14:30:00.000');
INSERT INTO public.company
(id, "name", owner_name, contact, address, created_at)
VALUES(3, '뿡빠라뿡빵뿡', '뿡빠라뿡빠사장장', '010-1122-3344', 'Busan, Haeundae-gu, Beach Road 789', '2025-02-07 09:15:00.000');

INSERT INTO public."program"
(id, company_id, title, description, price, capacity, start_time, end_time, created_at)
VALUES(1, 1, '리드 오전 강습', '전문강사에게 강습을 받아서 성장하세요', 50000.00, 30, '2025-02-01 09:00:00.000', '2025-02-28 17:00:00.000', '2025-02-09 01:47:57.858');
INSERT INTO public."program"
(id, company_id, title, description, price, capacity, start_time, end_time, created_at)
VALUES(2, 1, '리드 관광객 체험', '한강에서 리드를 체험하세요', 30000.00, 3, '2025-03-01 09:00:00.000', '2025-03-31 17:00:00.000', '2025-02-09 01:47:57.858');

INSERT INTO public.reservation
(id, users_id, program_id, reserved_at, status, created_at)
VALUES(1, 26, 1, '2025-02-09 09:00:00.000', 'PENDING', '2025-02-09 09:00:00.000');
INSERT INTO public.reservation
(id, users_id, program_id, reserved_at, status, created_at)
VALUES(2, 27, 2, '2025-02-09 09:00:00.000', 'PENDING', '2025-02-09 09:00:00.000');
