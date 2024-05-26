-- Drop the database if it existed
DROP DATABASE IF EXISTS bk_sims;

-- Create the database
CREATE DATABASE bk_sims;

-- Enable the 'uuid-ossp' extension for generating UUIDs (if not already enabled)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the 'users' table
CREATE TABLE activity_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create schema for the database

CREATE TABLE organization (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    avatar_file_url VARCHAR(255),
    gender VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    phone VARCHAR(255) UNIQUE NOT NULL
);

-- owner_id and organization_id will be changed to foreign key later
CREATE TABLE activity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) UNIQUE NOT NULL,
    banner_file_name VARCHAR(255) UNIQUE NOT NULL,
    banner_file_url VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_participants INT NOT NULL,
    can_participants_invite BOOLEAN NOT NULL,
    points INT NOT NULL,
    regulations_file_name VARCHAR(255) UNIQUE,
    regulations_file_url VARCHAR(255) UNIQUE,
    registration_start_date DATE, 
    registration_end_date DATE,
    activity_type VARCHAR(255),
    owner_id UUID NOT NULL,
    organization_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at DATE NOT NULL,
    CONSTRAINT fk_activity_organization FOREIGN KEY (organization_id) REFERENCES organization (id),
    CONSTRAINT fk_activity_owner FOREIGN KEY (owner_id) REFERENCES "user" (id)
);

CREATE TABLE activity_participation(
    activity_id UUID NOT NULL,
    user_id UUID NOT NULL,
    points_approved INT NOT NULL,
    evidence_url VARCHAR(255),
    CONSTRAINT fk_participation_activity FOREIGN KEY (activity_id) REFERENCES activity (id),
    CONSTRAINT fk_participation_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    PRIMARY KEY (activity_id, user_id)
);

CREATE TABLE activity_invitation(
    activity_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    invitation_link VARCHAR(255) UNIQUE NOT NULL,
    expired BOOLEAN NOT NULL,
    CONSTRAINT fk_invitation_activity FOREIGN KEY (activity_id) REFERENCES activity (id),
    CONSTRAINT fk_invitation_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    PRIMARY KEY (activity_id, user_id)
);

CREATE TABLE program (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE department (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE student (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    program_id UUID NOT NULL,
    department_id UUID NOT NULL,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_student_program FOREIGN KEY (program_id) REFERENCES program(id),
    CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE lecturer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    department_id UUID NOT NULL,
    CONSTRAINT fk_lecturer_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_lecturer_department FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE admin (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE aao (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    CONSTRAINT fk_aao_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE token (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(255) UNIQUE NOT NULL,
    expired BOOLEAN NOT NULL,
    revoked BOOLEAN NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE course (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_code VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    credits INT NOT NULL,
    introduction VARCHAR(255) NOT NULL,
    syllabus_file_name VARCHAR(255),
    exercise INT NOT NULL,
    midterm_exam INT NOT NULL,
    assignment INT NOT NULL,
    final_exam INT NOT NULL
);

CREATE TABLE prerequisite (
    pre_course_id UUID NOT NULL,
    course_id UUID NOT NULL,
    CONSTRAINT fk_pre_course_id FOREIGN KEY (pre_course_id) REFERENCES course (id),
    CONSTRAINT fk_participation_user FOREIGN KEY (course_id) REFERENCES course (id)
);

CREATE TABLE semester (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE registration_period (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL,
    semester_id UUID NOT NULL,
    CONSTRAINT fk_registration_period_semester FOREIGN KEY (semester_id) REFERENCES semester (id)
);

CREATE TABLE proposed_course (
    course_id UUID NOT NULL,
    registration_period_id UUID NOT NULL,
    CONSTRAINT fk_proposed_course_course FOREIGN KEY (course_id) REFERENCES course (id),
    CONSTRAINT fk_proposed_course_registration_period FOREIGN KEY (registration_period_id) REFERENCES registration_period (id),
    PRIMARY KEY (course_id, registration_period_id)
);

CREATE TABLE proposed_class (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    campus VARCHAR(255) NOT NULL,
    room VARCHAR(255) NOT NULL,
    weeks VARCHAR(255) NOT NULL,
    days VARCHAR(255) NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    current_enrollment INT NOT NULL,
    lecturer_id UUID NOT NULL,
    course_id UUID NOT NULL,
    registration_period_id UUID NOT NULL,
    CONSTRAINT unique_name_semester_course_proposed UNIQUE (name, type, registration_period_id, course_id),
    CONSTRAINT fk_proposed_class_proposed_course FOREIGN KEY (course_id, registration_period_id) REFERENCES proposed_course(course_id, registration_period_id),
    CONSTRAINT fk_proposed_class_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturer (id)
);

CREATE TABLE course_class (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    campus VARCHAR(255) NOT NULL,
    room VARCHAR(255) NOT NULL,
    weeks VARCHAR(255) NOT NULL,
    days VARCHAR(255) NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    current_enrollment INT NOT NULL,
    semester_id UUID NOT NULL,
    course_id UUID NOT NULL,
    lecturer_id UUID NOT NULL,
    CONSTRAINT unique_name_type_semester_course UNIQUE (name, type, semester_id, course_id),
    CONSTRAINT fk_course_class_semester FOREIGN KEY (semester_id) REFERENCES semester (id),
    CONSTRAINT fk_course_class_course FOREIGN KEY (course_id) REFERENCES course (id),
    CONSTRAINT fk_course_class_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturer (id)
);

CREATE TABLE registered_class (
    student_id UUID NOT NULL,
    proposed_class_id UUID NOT NULL,
    CONSTRAINT fk_registered_class_student FOREIGN KEY (student_id) REFERENCES student (id),
    CONSTRAINT fk_registered_class_proposed_class FOREIGN KEY (proposed_class_id) REFERENCES proposed_class (id),
    PRIMARY KEY (student_id, proposed_class_id)
);

-- CREATE TABLE assigned_class (
--     student_id UUID NOT NULL,
--     proposed_class_id UUID NOT NULL,
--     CONSTRAINT fk_assigned_class_student FOREIGN KEY (student_id) REFERENCES student (id),
--     CONSTRAINT fk_assigned_class_proposed_class FOREIGN KEY (proposed_class_id) REFERENCES proposed_class (id),
--     PRIMARY KEY (student_id, proposed_class_id)
-- );

CREATE TABLE assigned_class (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_code VARCHAR(255) UNIQUE NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    class_name VARCHAR(255) NOT NULL,
    campus VARCHAR(255) NOT NULL,
    room VARCHAR(255) NOT NULL,
    weeks VARCHAR(255) NOT NULL,
    days VARCHAR(255) NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    capacity INT NOT NULL,
    lecturer_name VARCHAR(255) NOT NULL,
    semester_name VARCHAR(255) NOT NULL
);


-- Insert initial values for tables

-- Insert initial data for activity types
INSERT INTO activity_type (id, name) VALUES 
(uuid_generate_v4(), 'Student Awards and Recognition Titles'),
(uuid_generate_v4(), 'Robocon Contest'),
(uuid_generate_v4(), 'International Cooperation and Exchange Activities'),
(uuid_generate_v4(), 'Visiting Factories, Enterprises, Projects'),
(uuid_generate_v4(), 'Academic Competitions (Green Environment, Chemistry Whirlwind,...)'),
(uuid_generate_v4(), 'School Civilization'),
(uuid_generate_v4(), 'Participating in Classes, Programs for Soft Skills Training'),
(uuid_generate_v4(), 'Visiting Historical Sites: Museums, Cemeteries, Battlefields...'),
(uuid_generate_v4(), 'Voluntary Blood Donation'),
(uuid_generate_v4(), 'Social Activities (Cleaning Warehouses, Laboratories, Environment....)'),
(uuid_generate_v4(), 'Social Activities (Volunteering)'),
(uuid_generate_v4(), 'Camping Programs, Festivals'),
(uuid_generate_v4(), 'Participation in Scientific Research Awards (Vifotech, Eureka...), Olympics at Various Levels'),
(uuid_generate_v4(), 'Program ''Lighting Up Vietnamese Youth Dreams'''),
(uuid_generate_v4(), 'Green Summer Volunteer Campaign'),
(uuid_generate_v4(), 'Exam Season Support Program'),
(uuid_generate_v4(), 'Competitions on Understanding Political Subjects'),
(uuid_generate_v4(), 'Student Titles (Class President, Youth Union Secretary, Committee Member,....)'),
(uuid_generate_v4(), 'Mid-Autumn Festival Night'),
(uuid_generate_v4(), 'External Units'),
(uuid_generate_v4(), 'Competitions and Cultural Activities'),
(uuid_generate_v4(), 'Job Fair Day'),
(uuid_generate_v4(), 'Career Orientation Day'),
(uuid_generate_v4(), 'Admission Consulting Program'),
(uuid_generate_v4(), 'Physical Education and Sports Competitions');

-- Insert initial data for department
INSERT INTO organization (id, name) VALUES 
(uuid_generate_v4(), 'Automotive Engineering'),
(uuid_generate_v4(), 'Chemical Engineering'),
(uuid_generate_v4(), 'Computer Engineering'),
(uuid_generate_v4(), 'Construction Materials Engineering'),
(uuid_generate_v4(), 'Aerospace Engineering'),
(uuid_generate_v4(), 'Architecture'),
(uuid_generate_v4(), 'Computer Science'),
(uuid_generate_v4(), 'Electrical Electronics Engineering'),
(uuid_generate_v4(), 'Mechatronics Engineering'),
(uuid_generate_v4(), 'Biotechnology'),
(uuid_generate_v4(), 'Transportation Engineering'),
(uuid_generate_v4(), 'Environmental Engineering'),
(uuid_generate_v4(), 'Business Administration'),
(uuid_generate_v4(), 'Logistics & Supply Chain Management'),
(uuid_generate_v4(), 'Physics Engineering'),
(uuid_generate_v4(), 'Civil Engineering'),
(uuid_generate_v4(), 'Food Technology'),
(uuid_generate_v4(), 'Environmental and Technology Management'),
(uuid_generate_v4(), 'Machanical Engineering'),
(uuid_generate_v4(), 'Mechatronic Engineering (Minor: Robot Engineering)'),
(uuid_generate_v4(), 'Petroleum Engineering'),
(uuid_generate_v4(), 'External Units'),
(uuid_generate_v4(), 'Transnational - Exchange Activities'),
(uuid_generate_v4(), 'Other School-level Units'),
(uuid_generate_v4(), 'School Union'),
(uuid_generate_v4(), 'OISP'),
(uuid_generate_v4(), 'National University'),
(uuid_generate_v4(), 'Student Political Affairs Office'),
(uuid_generate_v4(), 'School-level (General organization -- major campaigns)'),
(uuid_generate_v4(), 'Student Support and Employment Center'),
(uuid_generate_v4(), 'Technical Training Maintenance Center'),
(uuid_generate_v4(), 'School Student Union');

INSERT INTO program (id, name) VALUES 
(uuid_generate_v4(), 'General Program'),
(uuid_generate_v4(), 'High Quality Program'),
(uuid_generate_v4(), 'Talented Engineer Program'),
(uuid_generate_v4(), 'PFIEV'),
(uuid_generate_v4(), 'Japan-Oriented Program');

INSERT INTO department (id, name) VALUES 
(uuid_generate_v4(), 'Automotive Engineering'),
(uuid_generate_v4(), 'Chemical Engineering'),
(uuid_generate_v4(), 'Computer Engineering'),
(uuid_generate_v4(), 'Construction Materials Engineering'),
(uuid_generate_v4(), 'Aerospace Engineering'),
(uuid_generate_v4(), 'Architecture'),
(uuid_generate_v4(), 'Computer Science'),
(uuid_generate_v4(), 'Electrical Electronics Engineering'),
(uuid_generate_v4(), 'Mechatronics Engineering'),
(uuid_generate_v4(), 'Biotechnology'),
(uuid_generate_v4(), 'Transportation Engineering'),
(uuid_generate_v4(), 'Environmental Engineering'),
(uuid_generate_v4(), 'Business Administration'),
(uuid_generate_v4(), 'Logistics & Supply Chain Management'),
(uuid_generate_v4(), 'Physics Engineering'),
(uuid_generate_v4(), 'Civil Engineering'),
(uuid_generate_v4(), 'Food Technology'),
(uuid_generate_v4(), 'Environmental and Technology Management'),
(uuid_generate_v4(), 'Machanical Engineering'),
(uuid_generate_v4(), 'Mechatronic Engineering (Minor: Robot Engineering)'),
(uuid_generate_v4(), 'Petroleum Engineering');


INSERT INTO "user" (id, code, first_name, last_name, avatar_file_url, gender, dob, email, password, role, phone) VALUES
(uuid_generate_v4(), '1', 'System', 'Admin', null, 'Male', '1999-05-15', 'admin@hcmut.edu.vn', '$2a$10$RsvaI18Q3GN81PvY7u0ng.47TmoRAjRMVNly7cYfC7Nio15lvane2', 'ADMIN', '1234567890'),
(uuid_generate_v4(), '3', 'System', 'Student', null, 'Male', '1999-05-15', 'student@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567892'),
(uuid_generate_v4(), '4', 'Student', 'One', null, 'Female', '1999-05-15', 'student1@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567893'),
(uuid_generate_v4(), '5', 'Student', 'Two', null, 'Male', '1999-05-15', 'student2@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567894'),
(uuid_generate_v4(), '6', 'Student', 'Three', null, 'Female', '1999-05-15', 'student3@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567895'),
(uuid_generate_v4(), '7', 'Student', 'Four', null, 'Male', '1999-05-15', 'student4@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567896'),
(uuid_generate_v4(), '8', 'Student', 'Five', null, 'Female', '1999-05-15', 'student5@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567897'),
(uuid_generate_v4(), '2052082', 'Hai', 'Bui Trung', 'https://lh3.googleusercontent.com/a/ACg8ocLsEoxLMNHD1WPlrdcw6Dc1i7fzoYoF2uwgqmT2uBJUi5m7wg=s576-c-no', 'Male', '1999-05-15', 'hai.buiyoueyex2@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '0906744859'),
(uuid_generate_v4(), '2052590', 'Luong', 'La Vi', 'https://lh3.googleusercontent.com/cm/AGPWSu-87obYpixZDDL46ayBsYuUV7btUbiNU6761JMW61hjmBeB20Splw7MZT_3C3Jn54qCJg=s128-p-k-rw-no', 'Male', '1999-05-15', 'luong.la218@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '0386744859'),
(uuid_generate_v4(), '2', 'System', 'Lecturer', null, 'Female', '1999-05-15', 'lecturer@hcmut.edu.vn', '$2a$10$ZhbIAdxTXc2FSr74lB86mewOTb8yQ0hr7lDqXw5WwGBfkHevkEFMS', 'LECTURER', '1234567891'),
(uuid_generate_v4(), '12101', 'Van A', 'Nguyen', 'https://cdn-icons-png.flaticon.com/512/701/701996.png', 'Male', '1973-05-15', 'nguyenvana@hcmut.edu.vn', '$2a$10$0z1GlXgCK7EtVSOfi2IIeuqJgakC.j0/wBtZqB5fMIH0SS4PncFT.', 'LECTURER', '0923758294'),
(uuid_generate_v4(), '12102', 'Thi B', 'Tran', 'https://cdn-icons-png.flaticon.com/512/2945/2945469.png', 'Female', '1980-02-20', 'tranthib@hcmut.edu.vn', '$2a$10$LwSnCntWQ4JPl2ts2rvFpe.myZ3cSMlmucIIwSWUYCDDaAp5LoErG', 'LECTURER', '0912847563'),
(uuid_generate_v4(), '12103', 'Van C', 'Le', 'https://cdn-icons-png.flaticon.com/512/701/701996.png', 'Male', '1985-08-10', 'levanc@hcmut.edu.vn', '$2a$10$d0bzytqNb9o7QT1IcpaxP.1BEUGL.X7iokP7x.BhBED68kqPBPKDy', 'LECTURER', '0937458290'),
(uuid_generate_v4(), '12104', 'Thi D', 'Pham', 'https://cdn-icons-png.flaticon.com/512/2945/2945469.png', 'Female', '1990-11-25', 'phamthid@hcmut.edu.vn', '$2a$10$RTCb6eixieLVWnxvceKEue0o2lAVufU.gioN/u5vq1A1rReN4aKDm', 'LECTURER', '0945123786'),
(uuid_generate_v4(), '12105', 'Van E', 'Hoang', 'https://cdn-icons-png.flaticon.com/512/701/701996.png', 'Male', '1975-07-30', 'hoangvane@hcmut.edu.vn', '$2a$10$3DyGkrrnyjG9ePNhQyle8usugonfhJoZYnlby9i2rQhdhrR8inS.e', 'LECTURER', '0973456128'),
(uuid_generate_v4(), '12106', 'Thi F', 'Ngo', 'https://cdn-icons-png.flaticon.com/512/2945/2945469.png', 'Female', '1983-03-18', 'ngothif@hcmut.edu.vn', '$2a$10$N7OD66hpLZGKc5/9yQPLtOIjZ1JLkjjDvzCDaCRpdieeVopvoXCH2', 'LECTURER', '0962814579'),
(uuid_generate_v4(), '12107', 'Van G', 'Vu', 'https://cdn-icons-png.flaticon.com/512/701/701996.png', 'Male', '1978-12-22', 'vuvang@hcmut.edu.vn', '$2a$10$4jJ3uhgt8VMpK1WU9.iy5e2sRirOocfNoCMvjP.YaJnrAE9CEFPV2', 'LECTURER', '0983745612'),
(uuid_generate_v4(), '12108', 'Thi H', 'Dang', 'https://cdn-icons-png.flaticon.com/512/2945/2945469.png', 'Female', '1987-06-05', 'dangthih@hcmut.edu.vn', '$2a$10$KMrc41J5imsaQ.rncmrDAOtt06Bz.HPHjHSs9VhNo3Ru8J0IK0ini', 'LECTURER', '0957234861'),
(uuid_generate_v4(), '12109', 'Van I', 'Bui', 'https://cdn-icons-png.flaticon.com/512/701/701996.png', 'Male', '1982-09-14', 'buivani@hcmut.edu.vn', '$2a$10$K9uYxOCu9Z6zWjxPNfvXAum.R7jkM.PyeTx5kk/eVATHjNmDzNy9O', 'LECTURER', '0908374612'),
(uuid_generate_v4(), '12110', 'Thi J', 'Do', 'https://cdn-icons-png.flaticon.com/512/2945/2945469.png', 'Female', '1992-01-03', 'dothij@hcmut.edu.vn', '$2a$10$r/A6YwuIFOKtbL.p7LKbO.ABSm/A0AQCVrLQK0YBvSOJsVO1VBNDy', 'LECTURER', '0928143756');


INSERT INTO admin (id, user_id) VALUES
(uuid_generate_v4(), (SELECT id FROM "user" WHERE email = 'admin@hcmut.edu.vn'));

INSERT INTO lecturer (id, user_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn'), 
    (SELECT id FROM department WHERE name = 'Computer Science')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'High Quality Program'),
    (SELECT id FROM department WHERE name = 'Computer Science')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student1@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'Talented Engineer Program'),
    (SELECT id FROM department WHERE name = 'Aerospace Engineering')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student2@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'Japan-Oriented Program'),
    (SELECT id FROM department WHERE name = 'Construction Materials Engineering')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student3@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'PFIEV'),
    (SELECT id FROM department WHERE name = 'Transportation Engineering')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student4@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'High Quality Program'),
    (SELECT id FROM department WHERE name = 'Food Technology')
);

INSERT INTO student (id, user_id, program_id, department_id) VALUES
(
    uuid_generate_v4(), 
    (SELECT id FROM "user" WHERE email = 'student5@hcmut.edu.vn'), 
    (SELECT id FROM program WHERE name = 'General Program'),
    (SELECT id FROM department WHERE name = 'Civil Engineering')
);

-- Inserting courses into the course table
-- Test syllabus https://bk-sims-storage.s3.ap-northeast-2.amazonaws.com/syllabus/mock_syllabus.pdf
-- Mon Dai Cuong
INSERT INTO course (id, course_code, name, credits, introduction, syllabus_file_name, exercise, midterm_exam, assignment, final_exam)
VALUES 
    (uuid_generate_v4(), 'MT1003', 'Calculus 1', 3, 'Calculus course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'MT1005', 'Calculus 2', 4, 'Advanced Calculus course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'MT1007', 'Linear Algebra', 3, 'Linear Algebra course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'PH1003', 'Physics 1', 4, 'Introduction to Physics course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'PH1007', 'Physics 2', 3, 'Advanced Physics course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CH1003', 'General Chemistry', 3, 'Introduction to Chemistry course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'MT2013', 'Probability and Statistics', 3, 'Probability and Statistics course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1031', 'Marxist and Leninist Philosophy', 3, 'Philosophy course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1033', 'Marxist and Leninist Political Economy', 2, 'Political Economy course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1035', 'Scientific Socialism', 3, 'Socialism course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1039', 'History of Vietnamese Communist Party', 2, 'History of Vietnamese communist party course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1037', 'Ho Chi Minh Ideaology', 3, 'Ho Chi Minh Ideaology', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'SP1007', 'Introduction to Vietnamese Law', 2, 'General Law course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'IM1013', 'General Economics', 3, 'Introduction to Economics course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'IM1023', 'Production Management for Engineers', 2, 'Production Management course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'IM1025', 'Project Management for Engineers', 3, 'Project Management course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'IM1027', 'Engineering Economics', 3, 'Engineering Economics course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'IM3001', 'Business Administration for Engineers', 3, 'Business Administration course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CO1023', 'Digital Systems', 3, 'Digital Systems course', NULL, 20, 25, 25, 30),
    (uuid_generate_v4(), 'CO1005', 'Introduction to Computing', 3, 'Introduction to Computing course', NULL, 15, 20, 25, 40);

-- Mon co so
INSERT INTO course (id, course_code, name, credits, introduction, syllabus_file_name, exercise, midterm_exam, assignment, final_exam)
VALUES
    (uuid_generate_v4(), 'CO1007', 'Computer Organization and Assembly Language', 3, 'Computer Organization course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO1027', 'Programming Fundamentals', 3, 'Programming Fundamentals course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CO3093', 'Computer Networking', 3, 'Computer Networking course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO3001', 'Software Engineering', 3, 'Software Engineering course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CO2017', 'Operating Systems', 3, 'Operating Systems course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO2003', 'Data Structures & Algorithms', 4, 'Data Structures & Algorithms course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CO2011', 'Mathematical Modeling', 3, 'Mathematical Modeling course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO2007', 'Computer Architecture', 4, 'Computer Architecture course', NULL, 10, 15, 25, 50);

-- Mon tu chon
INSERT INTO course (id, course_code, name, credits, introduction, syllabus_file_name, exercise, midterm_exam, assignment, final_exam)
VALUES 
    (uuid_generate_v4(), 'CO3011', 'Software Project Management', 3, 'Software Project Management course', NULL, 15, 25, 30, 30),
    (uuid_generate_v4(), 'CO3015', 'Software Testing', 3, 'Software Testing course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO3017', 'Software Architecture', 3, 'Software Architecture course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO3021', 'Database Management Systems', 3, 'Database Management Systems course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO3023', 'Distributed and Object-Oriented Databases', 3, 'Distributed and Object-Oriented Databases course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO3027', 'E-commerce', 3, 'E-commerce course', NULL, 12, 20, 25, 45),
    (uuid_generate_v4(), 'CO3029', 'Data Mining', 3, 'Data Mining course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO3031', 'Algorithm Analysis and Design', 3, 'Algorithm Analysis and Design course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3033', 'Information Systems Security', 3, 'Information Systems Security course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO3037', 'Internet of Things Applications Development', 3, 'Internet of Things Applications Development course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO3041', 'Intelligent Systems', 3, 'Intelligent Systems course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO3043', 'Mobile Application Development', 3, 'Mobile Application Development course', NULL, 10, 20, 25, 45),
    (uuid_generate_v4(), 'CO3045', 'Game Programming', 3, 'Game Programming course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO3049', 'Web Programming', 3, 'Web Programming course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3057', 'Digital Image Processing and Computer Vision', 3, 'Digital Image Processing and Computer Vision course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO3059', 'Computer Graphics', 3, 'Computer Graphics course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3061', 'Introduction to Artificial Intelligence', 3, 'Introduction to Artificial Intelligence course', NULL, 15, 25, 30, 30),
    (uuid_generate_v4(), 'CO3065', 'Advanced Software Engineering', 3, 'Advanced Software Engineering course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3067', 'Parallel Computing', 3, 'Parallel Computing course', NULL, 15, 20, 25, 40),
    (uuid_generate_v4(), 'CO3069', 'Cryptography and Network Security', 3, 'Cryptography and Network Security course', NULL, 10, 20, 30, 40),
    (uuid_generate_v4(), 'CO3071', 'Distributed Systems', 3, 'Distributed Systems course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO3085', 'Natural Language Processing', 3, 'Natural Language Processing course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3089', 'Advanced Topics in Computer Science', 3, 'Advanced Topics in Computer Science course', NULL, 15, 25, 30, 30),
    (uuid_generate_v4(), 'CO3115', 'System Analysis and Design', 3, 'System Analysis and Design course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO3117', 'Machine Learning', 3, 'Machine Learning course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO4031', 'Big Data and Decision Support Systems', 3, 'Big Data and Decision Support Systems course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO4033', 'Big Data Analytics and Business Intelligence', 3, 'Big Data Analytics and Business Intelligence course', NULL, 15, 20, 30, 35),
    (uuid_generate_v4(), 'CO4035', 'Enterprise Resource Planning Systems', 3, 'Enterprise Resource Planning Systems course', NULL, 20, 20, 30, 30),
    (uuid_generate_v4(), 'CO4037', 'Management Information Systems', 3, 'Management Information Systems course', NULL, 15, 20, 30, 35);


-- Insert prerequisite table
-- Triet -> KTCT -> CNXHKH -> LSD -> Tu tuong HCM
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES
    ((SELECT id FROM course WHERE course_code = 'SP1031'), (SELECT id FROM course WHERE course_code = 'SP1033')),
    ((SELECT id FROM course WHERE course_code = 'SP1033'), (SELECT id FROM course WHERE course_code = 'SP1035')),
    ((SELECT id FROM course WHERE course_code = 'SP1035'), (SELECT id FROM course WHERE course_code = 'SP1039')),
    ((SELECT id FROM course WHERE course_code = 'SP1039'), (SELECT id FROM course WHERE course_code = 'SP1037'));

-- Calculus 1 -> Calculus 2 
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES 
    ((SELECT id FROM course WHERE course_code = 'MT1003'), (SELECT id FROM course WHERE course_code = 'MT1005'));

-- Calculus 1 + Linear Algerbra -> Probability and Statistics
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES
    ((SELECT id FROM course WHERE course_code = 'MT1003'), (SELECT id FROM course WHERE course_code = 'MT2013')),
    ((SELECT id FROM course WHERE course_code = 'MT1007'), (SELECT id FROM course WHERE course_code = 'MT2013'));

-- CO1007 (Computer Organization and Assembly Language) CO1027 Programming Fundamentals -> DSA
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES
    ((SELECT id FROM course WHERE course_code = 'CO1007'), (SELECT id FROM course WHERE course_code = 'CO2003')),
    ((SELECT id FROM course WHERE course_code = 'CO1027'), (SELECT id FROM course WHERE course_code = 'CO2003'));


-- CO1007 (Computer Organization and Assembly Language) -> Math Modeling (CO2011)
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES
    ((SELECT id FROM course WHERE course_code = 'CO1007'), (SELECT id FROM course WHERE course_code = 'CO2011'));

-- CO1005 (Introduction to computing) CO1023 (Digital systems) -> Computer Architecture
INSERT INTO prerequisite (pre_course_id, course_id)
VALUES
    ((SELECT id FROM course WHERE course_code = 'CO1005'), (SELECT id FROM course WHERE course_code = 'CO2007')),
    ((SELECT id FROM course WHERE course_code = 'CO1023'), (SELECT id FROM course WHERE course_code = 'CO2007')),
    ((SELECT id FROM course WHERE course_code = 'SP1039'), (SELECT id FROM course WHERE course_code = 'SP1037'));

-- Inserting semesters
INSERT INTO semester (id, name) VALUES
(uuid_generate_v4(), 'HK201'),
(uuid_generate_v4(), 'HK202'),
(uuid_generate_v4(), 'HK211'),
(uuid_generate_v4(), 'HK212'),
(uuid_generate_v4(), 'HK221'),
(uuid_generate_v4(), 'HK222'),
(uuid_generate_v4(), 'HK231'),
(uuid_generate_v4(), 'HK232'),
(uuid_generate_v4(), 'HK241');

-- Inserting registration periods
INSERT INTO registration_period (id, start_date, end_date, start_time, end_time, semester_id) VALUES
(uuid_generate_v4(), '2024-05-05', '2021-06-15', '10:00', '15:00', (SELECT id FROM semester WHERE name = 'HK241'));


-- Mon co so
INSERT INTO proposed_course (course_id, registration_period_id) VALUES
(
    (SELECT id FROM course WHERE course_code = 'CO1007'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO3093'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO3001'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO2017'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO2003'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO2011'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    (SELECT id FROM course WHERE course_code = 'CO2007'),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
);

-- Inserting proposed classes
INSERT INTO proposed_class (id, name, campus, room, weeks, days, start_time, end_time, type, capacity, current_enrollment, course_id, lecturer_id, registration_period_id) VALUES
(
    uuid_generate_v4(),
    'CC01',
    'LTK',
    'B1-308',
    '1,2,3,4,5,6,7,8,9,10',
    '2',
    '7:00',
    '9:00',
    'Theory',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    uuid_generate_v4(),
    'CC02',
    'LTK',
    'B1-308',
    '1,2,3,4,5,6,7,8,9,10',
    '3',
    '9:00',
    '11:00',
    'Theory',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    uuid_generate_v4(),
    'CC03',
    'LTK',
    'B1-308',
    '1,2,3,4,5,6,7,8,9,10',
    '4',
    '14:00',
    '16:00',
    'Theory',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    uuid_generate_v4(),
    'CC01',
    'LTK',
    'C6-105',
    '1,2,3,4,5,6,7,8,9,10',
    '5',
    '13:00',
    '18:00',
    'Lab',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    uuid_generate_v4(),
    'CC02',
    'LTK',
    'C6-105',
    '1,2,3,4,5,6,7,8,9,10',
    '6',
    '13:00',
    '18:00',
    'Lab',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
),
(
    uuid_generate_v4(),
    'CC03',
    'LTK',
    'C6-105',
    '1,2,3,4,5,6,7,8,9,10',
    '7',
    '13:00',
    '18:00',
    'Lab',
    100,
    0,
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn')),
    (SELECT id FROM registration_period WHERE semester_id = (SELECT id FROM semester WHERE name = 'HK241'))
);

-- Inserting course classes
INSERT INTO course_class (id, name, campus, room, weeks, days, start_time, end_time, type, capacity, current_enrollment, semester_id, course_id, lecturer_id) VALUES
(
    uuid_generate_v4(),
    'CC01',
    'LTK',
    'B1-308',
    '1,2,3,4,5,6,7,8,9,10',
    '2',
    '7:00',
    '9:00',
    'Theory',
    100,
    48,
    (SELECT id FROM semester WHERE name = 'HK201'),
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn'))
),
(
    uuid_generate_v4(),
    'CC02',
    'LTK',
    'B1-308',
    '1,2,3,4,5,6,7,8,9,10',
    '3',
    '9:00',
    '11:00',
    'Theory',
    100,
    72,
    (SELECT id FROM semester WHERE name = 'HK201'),
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn'))
),
(
    uuid_generate_v4(),
    'CC01',
    'LTK',
    'C6-105',
    '1,2,3,4,5,6,7,8,9,10',
    '5',
    '13:00',
    '18:00',
    'Lab',
    100,
    60,
    (SELECT id FROM semester WHERE name = 'HK201'),
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn'))
),
(
    uuid_generate_v4(),
    'CC02',
    'LTK',
    'C6-105',
    '1,2,3,4,5,6,7,8,9,10',
    '6',
    '13:00',
    '18:00',
    'Lab',
    100,
    60,
    (SELECT id FROM semester WHERE name = 'HK201'),
    (SELECT id FROM course WHERE course_code = 'CO1027'),
    (SELECT id FROM lecturer WHERE user_id = (SELECT id FROM "user" WHERE email = 'lecturer@hcmut.edu.vn'))
);

-- Inserting assigned classes for displaying registration result purpose
INSERT INTO assigned_class (id, course_code, course_name, class_name, campus, room, weeks, days, start_time, end_time, type, credits, capacity, lecturer_name, semester_name) VALUES
(uuid_generate_v4(), 'CO1007', 'Computer Organization and Assembly Language', 'CC01', 'LTK', 'B4-203', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '12:00', '15:00', 'Theory', 3, 110, 'Hoang Van E', 'HK201'),
(uuid_generate_v4(), 'CO1005', 'Introduction to Computing', 'CC01', 'LTK', 'B1-319', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '8:00', '10:00', 'Theory', 3, 80, 'Vu Van G', 'HK201'),
(uuid_generate_v4(), 'IM1023', 'Production Management for Engineers', 'CC01', 'LTK', 'C4-110', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '16:00', '19:00', 'Theory', 3, 110, 'Tran Thi B', 'HK201'),
(uuid_generate_v4(), 'CO1023', 'Digital Systems', 'CC01', 'LTK', 'B1-306', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '13:00', '15:00', 'Theory', 3, 90, 'Nguyen Van A', 'HK201'),
(uuid_generate_v4(), 'CO1027', 'Programming Fundamentals', 'CC01', 'LTK', 'B4-304', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '4', '11:00', '14:00', 'Theory', 3, 80, 'Hoang Van E', 'HK201'),
(uuid_generate_v4(), 'CO2003', 'Data Structures & Algorithms', 'CC01', 'LTK', 'B4-209', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '13:00', '15:00', 'Theory', 3, 130, 'Tran Thi B', 'HK201'),
(uuid_generate_v4(), 'IM1013', 'General Economics', 'CC02', 'LTK', 'C4-104', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '14:00', '17:00', 'Theory', 3, 140, 'Le Van C', 'HK202'),
(uuid_generate_v4(), 'CO2007', 'Computer Architecture', 'CC01', 'LTK', 'A4-305', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '11:00', '14:00', 'Theory', 3, 120, 'Tran Thi B', 'HK202'),
(uuid_generate_v4(), 'CO2011', 'Mathematical Modeling', 'CC01', 'LTK', 'C5-306', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '12:00', '15:00', 'Theory', 3, 140, 'Pham Thi D', 'HK202'),
(uuid_generate_v4(), 'IM3001', 'Business Administration for Engineers', 'CC01', 'LTK', 'A4-304', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '4', '12:00', '14:00', 'Theory', 3, 70, 'Do Thi J', 'HK202'),
(uuid_generate_v4(), 'CO2017', 'Operating Systems', 'CC01', 'LTK', 'B1-305', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '12:00', '14:00', 'Theory', 3, 130, 'Dang Thi H', 'HK211'),
(uuid_generate_v4(), 'CO3001', 'Software Engineering', 'CC01', 'LTK', 'A4-309', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '12:00', '15:00', 'Theory', 3, 100, 'Vu Van G', 'HK211'),
(uuid_generate_v4(), 'CO3093', 'Computer Networking', 'CC01', 'LTK', 'C4-115', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '10:00', '12:00', 'Theory', 3, 80, 'Tran Thi B', 'HK211'),
(uuid_generate_v4(), 'IM1027', 'Engineering Economics', 'CC01', 'LTK', 'A4-217', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '14:00', '17:00', 'Theory', 3, 70, 'Bui Van I', 'HK211'),
(uuid_generate_v4(), 'CO3011', 'Software Project Management', 'CC01', 'LTK', 'C4-208', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '10:00', '13:00', 'Theory', 3, 130, 'Le Van C', 'HK212'),
(uuid_generate_v4(), 'CO3015', 'Software Testing', 'CC01', 'LTK', 'B6-206', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '12:00', '15:00', 'Theory', 3, 150, 'Hoang Van E', 'HK212'),
(uuid_generate_v4(), 'CO3017', 'Software Architecture', 'CC01', 'LTK', 'B6-214', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '4', '8:00', '11:00', 'Theory', 3, 120, 'Pham Thi D', 'HK212'),
(uuid_generate_v4(), 'CO3021', 'Database Management Systems', 'CC01', 'LTK', 'C5-208', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '11:00', '13:00', 'Theory', 3, 80, 'Hoang Van E', 'HK212'),
(uuid_generate_v4(), 'CO3023', 'Distributed and Object-Oriented Databases', 'CC01', 'LTK', 'C4-307', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '4', '16:00', '18:00', 'Theory', 3, 110, 'Nguyen Van A', 'HK212'),
(uuid_generate_v4(), 'IM1025', 'Project Management for Engineers', 'CC01', 'LTK', 'A4-108', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '16:00', '19:00', 'Theory', 3, 80, 'Tran Thi B', 'HK212'),
(uuid_generate_v4(), 'CO3027', 'E-commerce', 'CC01', 'LTK', 'B1-201', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '16:00', '18:00', 'Theory', 3, 120, 'Le Van C', 'HK221'),
(uuid_generate_v4(), 'CO3029', 'Data Mining', 'CC01', 'LTK', 'A4-102', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '14:00', '17:00', 'Theory', 3, 140, 'Le Van C', 'HK221'),
(uuid_generate_v4(), 'CO3031', 'Algorithm Analysis and Design', 'CC01', 'LTK', 'C5-201', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '11:00', '13:00', 'Theory', 3, 130, 'Hoang Van E', 'HK221'),
(uuid_generate_v4(), 'CO3033', 'Information Systems Security', 'CC01', 'LTK', 'C4-210', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '12:00', '15:00', 'Theory', 3, 100, 'Nguyen Van A', 'HK221'),
(uuid_generate_v4(), 'CO4031', 'Big Data and Decision Support Systems', 'CC01', 'LTK', 'B1-105', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '12:00', '14:00', 'Theory', 3, 80, 'Tran Thi B', 'HK221'),
(uuid_generate_v4(), 'CO3037', 'Internet of Things Applications Development', 'CC01', 'LTK', 'A4-112', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '16:00', '18:00', 'Theory', 3, 130, 'Le Van C', 'HK222'),
(uuid_generate_v4(), 'CO3041', 'Intelligent Systems', 'CC01', 'LTK', 'C4-304', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '9:00', '11:00', 'Theory', 3, 130, 'Le Van C', 'HK222'),
(uuid_generate_v4(), 'CO3043', 'Mobile Application Development', 'CC01', 'LTK', 'C5-118', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '15:00', '18:00', 'Theory', 3, 150, 'Bui Van I', 'HK222'),
(uuid_generate_v4(), 'CO3045', 'Game Programming','CC01', 'LTK', 'B6-101', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '9:00', '11:00', 'Theory', 3, 60, 'Le Van C', 'HK222'),
(uuid_generate_v4(), 'CO3057', 'Digital Image Processing and Computer Vision', 'CC01', 'LTK', 'C4-217', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '7:00', '9:00', 'Theory', 3, 60, 'Vu Van G', 'HK231'),
(uuid_generate_v4(), 'CO3059', 'Computer Graphics', 'CC01', 'LTK', 'C5-303', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '2', '10:00', '13:00', 'Theory', 3, 140, 'Pham Thi D', 'HK231'),
(uuid_generate_v4(), 'CO3061', 'Introduction to Artificial Intelligence', 'CC01', 'LTK', 'C4-217', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '16:00', '18:00', 'Theory', 3, 90, 'Le Van C', 'HK231'),
(uuid_generate_v4(), 'CO3065', 'Advanced Software Engineering', 'CC02', 'LTK', 'B6-115', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '15:00', '18:00', 'Theory', 3, 140, 'Ngo Thi F', 'HK231'),
(uuid_generate_v4(), 'CO3117', 'Machine Learning', 'CC01', 'LTK', 'B4-311', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '6', '7:00', '9:00', 'Theory', 3, 120, 'Pham Thi D', 'HK231'),
(uuid_generate_v4(), 'CO3067', 'Parallel Computing', 'CC01', 'LTK', 'A4-209', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '3', '8:00', '10:00', 'Theory', 3, 140, 'Nguyen Van A', 'HK232'),
(uuid_generate_v4(), 'CO3069', 'Cryptography and Network Security', 'CC01', 'LTK', 'B1-107', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '4', '12:00', '15:00', 'Theory', 3, 130, 'Nguyen Van A', 'HK232'),
(uuid_generate_v4(), 'CO3071', 'Distributed Systems', 'CC01', 'LTK', 'B1-207', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '11:00', '14:00', 'Theory', 3, 80, 'Dang Thi H', 'HK232'),
(uuid_generate_v4(), 'CO3085', 'Natural Language Processing', 'CC01', 'LTK', 'B4-105', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '7:00', '10:00', 'Theory', 3, 150, 'Hoang Van E', 'HK232'),
(uuid_generate_v4(), 'CO3089', 'Advanced Topics in Computer Science', 'CC01', 'LTK', 'C4-302', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '5', '13:00', '16:00', 'Theory', 3, 150, 'Hoang Van E', 'HK232'),
(uuid_generate_v4(), 'CO3115', 'System Analysis and Design', 'CC01', 'LTK', 'C4-306', '1,2,3,4,-,6,7,8,-,9,10,11,12,13,14', '7', '16:00', '19:00', 'Theory', 3, 150, 'Ngo Thi F', 'HK232');
