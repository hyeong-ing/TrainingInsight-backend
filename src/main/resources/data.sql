insert into employees (id, name, department, position, hire_type, email, joined_at)
values
  (1, 'Kim Minji', 'HR', 'Manager', '경력', 'minji.kim@example.com', '2021-03-15'),
  (2, 'Lee Junho', 'Engineering', 'Staff Engineer', '경력', 'junho.lee@example.com', '2020-07-01'),
  (3, 'Park Sora', 'Sales', 'Associate', '신입', 'sora.park@example.com', '2024-01-08'),
  (4, 'Choi Daniel', 'Engineering', 'Associate', '신입', 'daniel.choi@example.com', '2024-02-12');

insert into courses (id, title, category, description, is_required, target_department, target_hire_type)
values
  (1, '정보보안 필수 교육', 'Compliance', '전 직원 대상 정보보안 기본 교육입니다.', true, 'ALL', 'ALL'),
  (2, '신입 온보딩 과정', 'Onboarding', '신입 직원을 위한 조직 문화와 업무 프로세스 교육입니다.', true, 'ALL', '신입'),
  (3, '엔지니어링 코드 리뷰', 'Engineering', '엔지니어링 조직을 위한 코드 리뷰 실무 교육입니다.', true, 'Engineering', 'ALL'),
  (4, '고객 커뮤니케이션 스킬', 'Sales', '고객 응대와 커뮤니케이션 역량 향상 과정입니다.', false, 'Sales', 'ALL');

insert into training_records (id, employee_id, course_id, status, completed_at)
values
  (1, 1, 1, 'COMPLETED', '2024-03-01T10:00:00'),
  (2, 2, 1, 'IN_PROGRESS', null),
  (3, 3, 1, 'NOT_STARTED', null),
  (4, 3, 2, 'COMPLETED', '2024-03-04T14:30:00'),
  (5, 4, 2, 'IN_PROGRESS', null),
  (6, 2, 3, 'COMPLETED', '2024-03-05T09:30:00'),
  (7, 4, 3, 'NOT_STARTED', null);
