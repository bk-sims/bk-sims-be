-- Create the database
CREATE DATABASE bk_sims;

-- Connect to the newly created database
\c bk_sims;

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
    avatar_file_url VARCHAR(255) UNIQUE,
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
    CONSTRAINT fk_admin_aao FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE token (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(255) UNIQUE NOT NULL,
    expired BOOLEAN NOT NULL,
    revoked BOOLEAN NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES "user" (id)
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


INSERT INTO "user" (id, code, first_name, last_name, gender, dob, email, password, role, phone) VALUES
(uuid_generate_v4(), '1', 'System', 'Admin', 'Male', '1999-05-15', 'admin@hcmut.edu.vn', '$2a$10$RsvaI18Q3GN81PvY7u0ng.47TmoRAjRMVNly7cYfC7Nio15lvane2', 'ADMIN', '1234567890');

INSERT INTO "user" (id, code, first_name, last_name, gender, dob, email, password, role, phone) VALUES
(uuid_generate_v4(), '2', 'System', 'Lecturer', 'Female', '1999-05-15', 'lecturer@hcmut.edu.vn', '$2a$10$ZhbIAdxTXc2FSr74lB86mewOTb8yQ0hr7lDqXw5WwGBfkHevkEFMS', 'LECTURER', '1234567891');

INSERT INTO "user" (id, code, first_name, last_name, gender, dob, email, password, role, phone) VALUES
(uuid_generate_v4(), '3', 'System', 'Student', 'Male', '1999-05-15', 'student@hcmut.edu.vn', '$2a$10$0VhhG4kKYN8SIRLOzlX7ouK4z.JVF0qYnVZrx4c8v4YoVNJ93gL9O', 'STUDENT', '1234567892');

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

