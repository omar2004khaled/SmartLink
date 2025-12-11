-- ===================================
-- DROP TABLES (in correct dependency order)
-- ===================================
DROP TABLE IF EXISTS connections;
DROP TABLE IF EXISTS comments_like;
DROP TABLE IF EXISTS comments_attach;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS post_attach;
DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS posts;

DROP TABLE IF EXISTS works_on;
DROP TABLE IF EXISTS experience;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS education;
DROP TABLE IF EXISTS job_seeker_profile;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS user;


-- ===================================
-- USER TABLE
-- ===================================
CREATE TABLE user (
    UserId          BIGINT AUTO_INCREMENT PRIMARY KEY,
    Phone           VARCHAR(20) UNIQUE,
    Name            VARCHAR(100),
    Role            ENUM('ADMIN','USER','Company'),
    Email           VARCHAR(150) UNIQUE NOT NULL,
    PasswordHash    VARCHAR(255) NOT NULL,
    active          BOOLEAN NOT NULL
);

CREATE UNIQUE INDEX UserByEmail ON user(Email);


-- ===================================
-- LOCATION TABLE
-- ===================================
CREATE TABLE location(
    LocationId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    Country         VARCHAR(32) NOT NULL,
    City            VARCHAR(16)
);


-- ===================================
-- USER PROFILE TABLE
-- ===================================
CREATE TABLE job_seeker_profile (
    ProfileId       BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL UNIQUE,
    ProfilePicUrl   TEXT,
    Bio             TEXT,
    Headline        VARCHAR(255),
    Gender          ENUM('MALE','FEMALE'),
    LocationId      BIGINT,
    BirthDate       DATE,
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE,
    FOREIGN KEY (LocationId) REFERENCES location(LocationId) ON DELETE SET NULL
);

CREATE UNIQUE INDEX ProfileByUser ON job_seeker_profile(UserId);


-- ===================================
-- EDUCATION TABLE
-- ===================================
CREATE TABLE education (
    EducationId     BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    School          VARCHAR(200),
    degree          ENUM(
    'Certificate',
    'Diploma',
    'Associate',
    'Bachelor of Arts (BA)',
    'Bachelor of Science (BSc)',
    'Bachelor of Engineering (BEng)',
    'Master of Arts (MA)',
    'Master of Science (MSc)',
    'Master of Engineering (MEng)',
    'Doctor of Philosophy (PhD)',
    'Professional Doctorate (DBA, EdD, etc.)'
)
,
    FieldOfStudy    ENUM(
    'Computer Science',
    'Information Technology',
    'Software Engineering',
    'Data Science',
    'Business Administration',
    'Accounting',
    'Finance',
    'Economics',
    'Marketing',
    'Mechanical Engineering',
    'Electrical Engineering',
    'Civil Engineering',
    'Biology',
    'Chemistry',
    'Physics',
    'Mathematics',
    'Nursing',
    'Psychology',
    'Education',
    'Architecture'
),
    StartDate       DATE,
    EndDate         DATE,
    Description     TEXT,

    FOREIGN KEY (ProfileId) REFERENCES job_seeker_profile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX EduByProf ON education(ProfileId);


-- ===================================
-- SKILLS TABLE
-- ===================================
CREATE TABLE skills (
    SkillId         BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    SkillName       VARCHAR(100) NOT NULL,
    Proficiency     ENUM ('BEGINER','INTERMEDIATE','PROFECIENT'),

    FOREIGN KEY (ProfileId) REFERENCES job_seeker_profile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX SkillsByProf ON skills(ProfileId);


-- ===================================
-- PROJECTS TABLE (NO DIRECT USER)
-- ===================================
CREATE TABLE projects (
    ProjectId       BIGINT AUTO_INCREMENT PRIMARY KEY,
    Title           VARCHAR(200) NOT NULL,
    Description     TEXT,
    ProjectUrl      TEXT,
    StartDate       DATE,
    EndDate         DATE
);


-- ===================================
-- WORKS_ON — linking Profile ↔ Project (MANY-TO-MANY)
-- ===================================
CREATE TABLE works_on (
    ProfileId BIGINT NOT NULL,
    ProjectId BIGINT NOT NULL,

    PRIMARY KEY (ProfileId, ProjectId),

    FOREIGN KEY (ProfileId) REFERENCES job_seeker_profile(ProfileId) ON DELETE CASCADE,
    FOREIGN KEY (ProjectId) REFERENCES projects(ProjectId) ON DELETE CASCADE
);


-- ===================================
-- EXPERIENCE TABLE
-- ===================================
CREATE TABLE experience (
    ExperienceId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    CompanyName     VARCHAR(200)NOT NULL,
    Title           VARCHAR(50),
    Location        VARCHAR(150) NOT NULL,
    StartDate       DATE NOT NULL,
    EndDate         DATE NOT NULL,
    Description     TEXT,

    FOREIGN KEY (ProfileId) REFERENCES job_seeker_profile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX ExperienceByProf ON experience(ProfileId);


-- ===================================
-- POSTS TABLE
-- ===================================
CREATE TABLE posts(
    PostId          BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL,
    Content         VARCHAR(2500) NOT NULL,
    CreatedAt       DATETIME,
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE
);

CREATE INDEX PostsByUser ON posts(UserId);


-- ===================================
-- ATTACHMENT TABLE (ONE DEFINITION)
-- ===================================
CREATE TABLE attachment(
    AttachId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    URL         VARCHAR(150) NOT NULL,
    Type        ENUM('Video','Image','Audio','Files')
);

-- ===================================
-- POST ↔ ATTACHMENT (MANY-TO-MANY)
-- ===================================
CREATE TABLE post_attach(
    PostId      BIGINT,
    AttachId    BIGINT,
    PRIMARY KEY (PostId, AttachId),
    FOREIGN KEY (PostId) REFERENCES posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (AttachId) REFERENCES attachment(AttachId) ON DELETE CASCADE
);


-- ===================================
-- POST LIKES
-- ===================================
CREATE TABLE post_like(
    UserId      BIGINT,
    PostId      BIGINT,
    PRIMARY KEY (PostId, UserId),

    FOREIGN KEY (PostId) REFERENCES posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE
);


-- ===================================
-- COMMENTS
-- ===================================
CREATE TABLE comments(
    CommentId       BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL,
    PostId          BIGINT NOT NULL,
    Content         VARCHAR(2500) NOT NULL,
    CreatedAt       DATETIME NOT NULL,
    RepliedTo       BIGINT,
    AttachId        BIGINT,
    RepliedTo      BIGINT,
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE,
    FOREIGN KEY (PostId) REFERENCES posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (RepliedTo) REFERENCES comments(CommentId) ON DELETE CASCADE
);

CREATE INDEX CommentsByUser ON comments(UserId);
CREATE INDEX CommentsByPost ON comments(PostId);




-- ===================================
-- COMMENTS ↔ ATTACHMENT (MANY-TO-MANY)
-- ===================================
CREATE TABLE comments_attach(
    CommentId      BIGINT,
    AttachId        BIGINT,
    PRIMARY KEY     (CommentId, AttachId),
    
    FOREIGN KEY (CommentId) REFERENCES comments(CommentId) ON DELETE CASCADE,
    FOREIGN KEY (AttachId) REFERENCES attachment(AttachId) ON DELETE CASCADE
);


-- ===================================
-- COMMENT LIKES
-- ===================================
CREATE TABLE comments_like(
    UserId      BIGINT,
    CommentId  BIGINT,
    PRIMARY KEY (CommentId, UserId),
    FOREIGN KEY (CommentId) REFERENCES comments(CommentId) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE
);


-- ===================================
-- CONNECTIONS TABLE
-- ===================================
CREATE TABLE connections (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id       BIGINT NOT NULL,
    receiver_id     BIGINT NOT NULL,
    created_at      DATETIME NOT NULL,
    status          ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    
    FOREIGN KEY (sender_id) REFERENCES user(UserId) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES user(UserId) ON DELETE CASCADE,
    
    UNIQUE KEY unique_connection (sender_id, receiver_id)
);

CREATE INDEX idx_sender ON connections(sender_id);
CREATE INDEX idx_receiver ON connections(receiver_id);
CREATE INDEX idx_status ON connections(status);