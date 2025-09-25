-- ===========================================
-- Drop Tables (초기화 작업: 기존 테이블 삭제)
-- ===========================================
DROP TABLE IF EXISTS public.email_logs CASCADE;
DROP TABLE IF EXISTS public.reservations CASCADE;
DROP TABLE IF EXISTS public.tourists CASCADE;
DROP TABLE IF EXISTS public.activities CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.companies CASCADE;

-- ===========================================
-- Table: companies (업체 정보)
-- ===========================================
CREATE TABLE public.companies (
    id serial4 PRIMARY KEY,
    company_name varchar(100) NOT NULL,
    business_number varchar(20) NOT NULL UNIQUE,
    ceo_name varchar(50) NOT NULL,
    ceo_contact varchar(20) NOT NULL,
    office_contact varchar(20),
    office_address varchar(255),
    is_verified bool DEFAULT false,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.companies IS '업체 정보';
COMMENT ON COLUMN public.companies.id IS '업체 식별자';
COMMENT ON COLUMN public.companies.company_name IS '업체명';
COMMENT ON COLUMN public.companies.business_number IS '사업자등록번호';
COMMENT ON COLUMN public.companies.ceo_name IS '대표자 이름';
COMMENT ON COLUMN public.companies.ceo_contact IS '대표자 연락처';
COMMENT ON COLUMN public.companies.office_contact IS '사무실 연락처';
COMMENT ON COLUMN public.companies.office_address IS '사무실 주소';
COMMENT ON COLUMN public.companies.is_verified IS '승인 여부';
COMMENT ON COLUMN public.companies.created_at IS '생성 일시';

-- ===========================================
-- Table: users (사용자 계정)
-- ===========================================
CREATE TABLE public.users (
    id serial4 PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE,
    password_hash text NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.users IS '사용자 계정(업체 회원)';
COMMENT ON COLUMN public.users.username IS '로그인 아이디';

-- ===========================================
-- Table: activities (활동 정보)
-- ===========================================
CREATE TABLE public.activities (
    id serial4 PRIMARY KEY,
    company_id int4 NOT NULL REFERENCES public.companies(id) ON DELETE CASCADE,
    title varchar(150) NOT NULL,
    description text,
    "location" varchar(255) NOT NULL,
    activity_type varchar(10) NOT NULL,
    max_participants int4 NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    price int4,
    image_url varchar(255),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    activity_date date NOT NULL DEFAULT CURRENT_DATE,
    start_time time NOT NULL DEFAULT '09:00:00',
    end_time time NOT NULL DEFAULT '10:00:00'
);

COMMENT ON TABLE public.activities IS '활동 정보';
COMMENT ON COLUMN public.activities.id IS '활동 식별자';
COMMENT ON COLUMN public.activities.company_id IS '업체 ID';
COMMENT ON COLUMN public.activities.title IS '활동 제목';
COMMENT ON COLUMN public.activities.description IS '활동 설명';
COMMENT ON COLUMN public.activities.location IS '활동 장소';
COMMENT ON COLUMN public.activities.activity_type IS '활동 유형(육상/수상/공중)';
COMMENT ON COLUMN public.activities.max_participants IS '최대 수용 인원';
COMMENT ON COLUMN public.activities.created_at IS '생성 일시';
COMMENT ON COLUMN public.activities.activity_date IS '활동 일자';
COMMENT ON COLUMN public.activities.start_time IS '시작 시간';
COMMENT ON COLUMN public.activities.end_time IS '종료 시간';

-- ===========================================
-- Table: tourists (참가자 정보)
-- ===========================================
CREATE TABLE public.tourists (
    id serial4 PRIMARY KEY,
    "name" varchar(50) NOT NULL,
    passport_number varchar(30) NOT NULL UNIQUE,
    nationality varchar(50) NOT NULL,
    gender varchar(10),
    birthdate date NOT NULL,
    email varchar(100) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tourists_gender_check CHECK (gender IN ('M', 'F', 'Other'))
);

CREATE INDEX idx_tourist_email ON public.tourists USING btree (email);

COMMENT ON TABLE public.tourists IS '참가자 정보';
COMMENT ON COLUMN public.tourists.id IS '참가자 식별자';
COMMENT ON COLUMN public.tourists.name IS '참가자 이름';
COMMENT ON COLUMN public.tourists.passport_number IS '여권 번호';
COMMENT ON COLUMN public.tourists.nationality IS '국적';
COMMENT ON COLUMN public.tourists.gender IS '성별';
COMMENT ON COLUMN public.tourists.birthdate IS '생년월일';
COMMENT ON COLUMN public.tourists.email IS '이메일 주소';
COMMENT ON COLUMN public.tourists.created_at IS '생성 일시';
COMMENT ON INDEX public.idx_tourist_email IS '참가자 이메일 인덱스';

-- ===========================================
-- Table: reservations (예약 정보)
-- ===========================================
CREATE TABLE public.reservations (
    id serial4 PRIMARY KEY,
    activity_id int4 NOT NULL REFERENCES public.activities(id) ON DELETE CASCADE,
    tourist_id int4 NOT NULL REFERENCES public.tourists(id) ON DELETE CASCADE,
    status varchar(20) DEFAULT 'pending',
    special_request text,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT reservations_status_check CHECK (status IN ('pending', 'confirmed', 'canceled'))
);

COMMENT ON TABLE public.reservations IS '활동 예약 정보';
COMMENT ON COLUMN public.reservations.id IS '예약 식별자';
COMMENT ON COLUMN public.reservations.activity_id IS '예약 활동 ID';
COMMENT ON COLUMN public.reservations.tourist_id IS '참가자 ID';
COMMENT ON COLUMN public.reservations.status IS '예약 상태';
COMMENT ON COLUMN public.reservations.special_request IS '특별 요청 사항';
COMMENT ON COLUMN public.reservations.created_at IS '생성 일시';

-- ===========================================
-- Table: email_logs (이메일 발송 이력)
-- ===========================================
CREATE TABLE public.email_logs (
    id serial4 PRIMARY KEY,
    tourist_id int4 NOT NULL REFERENCES public.tourists(id) ON DELETE CASCADE,
    reservation_id int4 REFERENCES public.reservations(id) ON DELETE SET NULL,
    subject varchar(255) NOT NULL,
    body text NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'sent',
    error_message text,
    sent_at timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.email_logs IS '이메일 발송 이력';
COMMENT ON COLUMN public.email_logs.id IS '이메일 식별자';
COMMENT ON COLUMN public.email_logs.sent_at IS '발송 일시';

-- ===========================================
-- Table: company_users (업체 사용자 계정)
-- ===========================================
CREATE TABLE public.company_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    company_id INT REFERENCES public.companies(id),
    role VARCHAR(20) NOT NULL DEFAULT 'COMPANY',
    membership_status VARCHAR(20) NOT NULL DEFAULT 'UNASSIGNED'
);

COMMENT ON TABLE public.company_users IS '업체 사용자 계정';
CREATE INDEX IF NOT EXISTS idx_company_users_username ON company_users (username);

-- ===========================================
-- Insert: 기본 데이터
-- ===========================================
-- 업체 정보
INSERT INTO public.companies (company_name, business_number, ceo_name, ceo_contact, office_contact, office_address, is_verified)
VALUES 
('한강 어드벤처', '111-22-33333', '김한강', '010-1000-2000', '02-100-2000', '서울특별시 영등포구 여의도동 123-4', true),
('서울 SUP 클럽', '222-33-44444', '박서해', '010-2222-3333', '02-222-3333', '서울특별시 마포구 망원동 45-7', true),
('한강 수상센터', '333-44-55555', '이순파', '010-3333-4444', '02-333-4444', '서울특별시 광진구 자양동 89-2', true);

-- 활동 정보
INSERT INTO public.activities (company_id, title, description, "location", activity_type, max_participants, activity_date, start_time, end_time)
VALUES 
(1, '투어 SUP 기초 클래스', '한강에서 SUP 기본 자세와 안전 수칙을 배우는 입문 프로그램입니다.', '서울 영등포구 여의도한강공원', '수상', 20, '2025-08-01', '09:00:00', '10:00:00'),
(1, '노을 요가 세션', '해질녘 한강 뷰를 바라보며 진행하는 힐링 요가 시간입니다.', '서울 영등포구 여의도한강공원', '육상', 20, '2025-08-01', '18:00:00', '19:00:00'),
(2, '도심 카약 투어', '전문 강사와 함께하는 야간 한강 카약 투어 프로그램입니다.', '서울 마포구 망원한강공원', '수상', 16, '2025-08-06', '20:00:00', '21:30:00'),
(2, '실내 패들 보강반', '기초를 마친 회원을 위한 균형 감각 및 근력 강화 프로그램입니다.', '서울 마포구 서교동 연습장', '육상', 12, '2025-08-07', '13:00:00', '14:30:00'),
(3, '수상 안전 교육', '현직 구조대 출신 강사가 진행하는 수상 안전 이론 및 실습 과정입니다.', '서울 광진구 자양한강공원', '수상', 25, '2025-08-03', '07:30:00', '09:00:00'),
(3, '선셋 보트 파티', '음악과 다과가 마련된 선셋 크루즈 체험 프로그램입니다.', '서울 광진구 자양한강공원', '수상', 30, '2025-08-08', '19:00:00', '21:00:00'),
(3, '프라이빗 요트 라운지', '소규모 인원으로 즐기는 프라이빗 요트 라운지 패키지입니다.', '서울 광진구 자양한강공원', '수상', 10, '2025-08-09', '15:00:00', '17:00:00'),
(3, '아침 조깅 모임', '강변을 따라 달리며 체력을 기르는 무료 조깅 커뮤니티입니다.', '서울 광진구 자양한강공원', '육상', 40, '2025-08-10', '06:30:00', '07:30:00');

-- 참가자 정보
INSERT INTO public.tourists (name, passport_number, nationality, gender, birthdate, email)
VALUES 
('Satoshi Tanaka', 'JP1029384', 'Japan', 'M', '1985-06-15', 'satoshi@example.com'),
('Li Wei', 'CN3849201', 'China', 'F', '1992-03-22', 'liwei@example.cn'),
('John Smith', 'US8473629', 'USA', 'M', '1988-11-05', 'johnsmith@example.com'),
('Yuki Nakamura', 'JP8372619', 'Japan', 'F', '1995-01-30', 'yuki@example.jp'),
('Wang Fang', 'CN7261823', 'China', 'F', '1982-08-14', 'fangwang@example.cn'),
('Emily Davis', 'US1928374', 'USA', 'F', '1990-07-19', 'emily@example.com'),
('Takashi Ito', 'JP3748291', 'Japan', 'M', '1979-09-02', 'takashi@example.jp'),
('Chen Jie', 'CN9081723', 'China', 'M', '1987-04-09', 'jiechen@example.cn'),
('Michael Brown', 'US5647382', 'USA', 'M', '1983-10-22', 'michaelb@example.com'),
('Aiko Suzuki', 'JP3847562', 'Japan', 'F', '1996-12-10', 'aiko@example.jp');

-- 예약 정보
INSERT INTO public.reservations (activity_id, tourist_id, status, special_request)
VALUES 
(1, 1, 'confirmed', '채식 간식으로 부탁드립니다.'),
(2, 2, 'confirmed', '요가 매트 대여가 필요합니다.'),
(3, 3, 'pending', '동행인 추가 예약 가능 여부 문의'),
(4, 4, 'confirmed', '주차 공간 안내 부탁드립니다.'),
(5, 5, 'canceled', '일정 변경으로 취소 요청'),
(6, 6, 'confirmed', '기념일 이벤트를 준비하고 싶어요.'),
(7, 7, 'pending', '영어 진행 가능 여부 확인 바랍니다.'),
(8, 8, 'confirmed', '달리기 완주 인증서가 필요합니다.'),
(5, 9, 'confirmed', '장비 사전 대여 요청'),
(6, 10, 'pending', '단체 할인 적용 가능 여부 문의');

-- 이메일 발송 이력
INSERT INTO public.email_logs (tourist_id, reservation_id, subject, body, status)
VALUES 
(1, 1, '한강 활동 예약 확정 안내', '예약이 확정되었습니다. 집결 시간과 위치를 확인해 주세요.', 'sent'),
(2, 2, '요가 세션 예약 확정', '예약이 완료되었습니다. 편안한 복장으로 10분 전에 도착해 주세요.', 'sent'),
(3, 3, '카약 투어 대기 안내', '현재 대기 상태입니다. 좌석이 생기는 즉시 연락드리겠습니다.', 'sent'),
(4, 4, '보강반 예약 확정', '예약이 확정되었습니다. 준비물과 장소를 다시 확인해주세요.', 'sent'),
(5, 5, '수상 안전 교육 취소 안내', '요청하신 예약 취소가 완료되었습니다.', 'sent'),
(6, 6, '선셋 파티 예약 확정', '예약이 확정되었습니다. 탑승 15분 전에 체크인 해주세요.', 'sent'),
(7, 7, '요트 라운지 대기 안내', '대기 신청이 정상 접수되었습니다. 순차적으로 안내드리겠습니다.', 'sent'),
(8, 8, '조깅 모임 예약 확정', '예약이 확정되었습니다. 간단한 워밍업 후 함께 달려요!', 'sent'),
(9, 9, '수상 안전 교육 예약 확정', '장비 대여가 포함된 예약이 확정되었습니다.', 'sent'),
(10, 10, '선셋 파티 추가 문의 답변', '단체 할인은 현재 적용되지 않으며 다른 혜택을 검토 중입니다.', 'sent');

INSERT INTO public.company_users
(id, username, "name", password_hash, created_at, company_id, "role", membership_status)
VALUES
(1, 'admin', '관리자', '$2a$10$HLpSuVhpMcK.0l/aRz1QUuHnytQHKeFCvobb4FihR5kBADnmDqrwC', '2025-09-19 13:46:34.025', NULL, 'ADMIN', 'APPROVED');



