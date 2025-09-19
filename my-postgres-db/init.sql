-- ===========================================
-- Drop Tables (선택 사항: 기존 테이블 제거)
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

COMMENT ON TABLE public.companies IS '회사 정보';
COMMENT ON COLUMN public.companies.id IS '회사 고유 식별자';
COMMENT ON COLUMN public.companies.company_name IS '회사 이름';
COMMENT ON COLUMN public.companies.business_number IS '사업자등록 번호';
COMMENT ON COLUMN public.companies.ceo_name IS '대표자 이름';
COMMENT ON COLUMN public.companies.ceo_contact IS '대표자 연락처';
COMMENT ON COLUMN public.companies.office_contact IS '사무실 연락처';
COMMENT ON COLUMN public.companies.office_address IS '사무실 주소';
COMMENT ON COLUMN public.companies.is_verified IS '승인 여부';
COMMENT ON COLUMN public.companies.created_at IS '생성 타임스탬프';

-- ===========================================
-- Table: users (간단 회원/인증)
-- ===========================================
CREATE TABLE public.users (
    id serial4 PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE,
    password_hash text NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.users IS '앱 사용자 (업체 회원 포함)';
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
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    activity_date date NOT NULL DEFAULT CURRENT_DATE,
    start_time time NOT NULL DEFAULT '09:00:00',
    end_time time NOT NULL DEFAULT '10:00:00'
);

COMMENT ON TABLE public.activities IS '활동 정보';
COMMENT ON COLUMN public.activities.id IS '활동 고유 식별자';
COMMENT ON COLUMN public.activities.company_id IS '업체 ID';
COMMENT ON COLUMN public.activities.title IS '활동 제목';
COMMENT ON COLUMN public.activities.description IS '활동 설명';
COMMENT ON COLUMN public.activities.location IS '활동 장소';
COMMENT ON COLUMN public.activities.activity_type IS '활동 유형 (지상, 수상, 공중)';
COMMENT ON COLUMN public.activities.max_participants IS '최대 참여 인원';
COMMENT ON COLUMN public.activities.created_at IS '생성 타임스탬프';
COMMENT ON COLUMN public.activities.activity_date IS '활동 날짜';
COMMENT ON COLUMN public.activities.start_time IS '시작 시간';
COMMENT ON COLUMN public.activities.end_time IS '종료 시간';

-- ===========================================
-- Table: tourists (관광객 정보)
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

COMMENT ON TABLE public.tourists IS '관광객 정보';
COMMENT ON COLUMN public.tourists.id IS '관광객 고유 식별자';
COMMENT ON COLUMN public.tourists.name IS '관광객 이름';
COMMENT ON COLUMN public.tourists.passport_number IS '여권 번호';
COMMENT ON COLUMN public.tourists.nationality IS '국적';
COMMENT ON COLUMN public.tourists.gender IS '성별';
COMMENT ON COLUMN public.tourists.birthdate IS '생년월일';
COMMENT ON COLUMN public.tourists.email IS '이메일 주소';
COMMENT ON COLUMN public.tourists.created_at IS '생성 타임스탬프';
COMMENT ON INDEX public.idx_tourist_email IS '관광객 이메일 조회 인덱스';

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
COMMENT ON COLUMN public.reservations.id IS '예약 고유 식별자';
COMMENT ON COLUMN public.reservations.activity_id IS '예약 활동 ID';
COMMENT ON COLUMN public.reservations.tourist_id IS '관광객 ID';
COMMENT ON COLUMN public.reservations.status IS '예약 상태';
COMMENT ON COLUMN public.reservations.special_request IS '특별 요청 사항';
COMMENT ON COLUMN public.reservations.created_at IS '생성 타임스탬프';

-- ===========================================
-- Table: email_logs (이메일 발송 기록)
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

COMMENT ON TABLE public.email_logs IS '이메일 발송 기록';
COMMENT ON COLUMN public.email_logs.id IS '이메일 로그 고유 식별자';
COMMENT ON COLUMN public.email_logs.sent_at IS '발송 타임스탬프';

-- ===========================================
-- Table: email_logs (이메일 발송 기록)
-- ===========================================
CREATE TABLE public.company_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.company_users IS '사용자 계정';
CREATE INDEX IF NOT EXISTS idx_company_users_username ON company_users (username);

-- ===========================================
-- Insert: 샘플 데이터
-- ===========================================
-- 업체 정보
INSERT INTO public.companies (company_name, business_number, ceo_name, ceo_contact, office_contact, office_address, is_verified)
VALUES 
('한강어드벤처', '111-22-33333', '이대한', '010-1000-2000', '02-100-2000', '서울시 마포구 한강대로', true),
('리들카약컴퍼니', '222-33-44444', '박수상', '010-2222-3333', '02-222-3333', '서울시 광진구 뚝섬유원지', true),
('바람과더리버', '333-44-55555', '김철수', '010-3333-4444', '02-333-4444', '서울시 강서구 한강변', true);

-- 액티비티 정보
INSERT INTO public.activities (company_id, title, description, "location", activity_type, max_participants, activity_date, start_time, end_time)
VALUES 
(1, '야외 클라이밍 반', '서울 한강로에서 진행하는 야외 클라이밍 반 프로그램입니다.', '서울 마포구', '지상', 20, '2025-08-01', '09:00:00', '10:00:00'),
(1, '생활체육 체험', '서울 한강로에서 진행하는 생활체육 체험입니다.', '서울 마포구', '지상', 20, '2025-08-01', '11:00:00', '12:00:00'),
(2, '패들보드 체험', '서울 한강 수상에서 진행하는 패들보드 체험입니다.', '서울 광진구', '수상', 20, '2025-08-06', '11:00:00', '12:30:00'),
(2, '카약 투어', '서울 한강 성수대교에서 진행하는 카약 투어입니다.', '서울 광진구', '수상', 12, '2025-08-07', '13:00:00', '14:30:00'),
(3, '조깅 클럽', '서울 한강 시민공원에서 진행하는 조깅 클럽입니다.', '서울 강서구', '지상', 25, '2025-08-03', '07:30:00', '08:30:00'),
(3, '명상 워크숍', '서울 한강 실내 공간에서 진행하는 명상 워크숍입니다.', '서울 강서구', '지상', 30, '2025-08-08', '09:00:00', '10:30:00'),
(3, '자연 사진 산책', '서울 한강 성수대교에서 진행하는 자연 사진 산책 프로그램입니다.', '서울 광진구', '지상', 20, '2025-08-09', '06:30:00', '07:30:00'),
(3, '주말 명상 리트릿', '서울 한강 시민공원에서 진행하는 주말 명상 리트릿입니다.', '서울 강서구', '지상', 15, '2025-08-10', '08:00:00', '10:00:00');

-- 관광객 정보
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
(1, 1, 'confirmed', '초보자입니다'),
(2, 2, 'confirmed', '안전장비 요청'),
(3, 3, 'pending', '햇빛 가리개 필요'),
(4, 4, 'confirmed', '아침 일찍 시작 희망'),
(5, 5, 'canceled', '일정 변경으로 취소'),
(6, 6, 'confirmed', '부자 참여'),
(7, 7, 'pending', '매트 대여가 되나요?'),
(8, 8, 'confirmed', '가이드 요청'),
(5, 9, 'confirmed', '구명조끼 필수'),
(6, 10, 'pending', '근처 식사 장소 추천');

-- 이메일 로그
INSERT INTO public.email_logs (tourist_id, reservation_id, subject, body, status)
VALUES 
(1, 1, '한강 활동 예약 확인', '예약이 완료되었습니다. 즐거운 활동 되세요!', 'sent'),
(2, 2, '한강 활동 예약 확인', '예약이 확인되었습니다. 감사합니다.', 'sent'),
(3, 3, '한강 활동 예약 보류', '현재 예약이 보류 상태입니다.', 'sent'),
(4, 4, '한강 활동 예약 완료', '예약이 정상적으로 처리되었습니다.', 'sent'),
(5, 5, '한강 활동 예약 취소', '요청하신 예약이 취소되었습니다.', 'sent'),
(6, 6, '한강 활동 예약 확인', '패들보드 체험이 곧 시작됩니다.', 'sent'),
(7, 7, '조깅 클럽 예약 확인', '조깅 클럽에 등록되었습니다.', 'sent'),
(8, 8, '카약 투어 예약 확인', '카약 투어 일정 참여가 확정되었습니다.', 'sent'),
(9, 9, '클라이밍 반 예약 확인', '준비물을 꼭 챙겨주세요.', 'sent'),
(10, 10, '패들보드 체험 확인', '세부 일정은 한 번 더 확인 중입니다.', 'sent');

