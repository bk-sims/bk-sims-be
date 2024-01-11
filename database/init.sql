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
    owner_id INT,
    organization_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at DATE NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organization (id)
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
