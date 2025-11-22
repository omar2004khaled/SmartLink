-- ===================================
-- DROP TABLES (in correct dependency order)
-- ===================================
DROP TABLE IF EXISTS Comments_like;
DROP TABLE IF EXISTS Comments_Attach;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Post_like;
DROP TABLE IF EXISTS Post_Attach;
DROP TABLE IF EXISTS Attachment;
DROP TABLE IF EXISTS Posts;

DROP TABLE IF EXISTS Works_On;
DROP TABLE IF EXISTS Experience;
DROP TABLE IF EXISTS Projects;
DROP TABLE IF EXISTS Skills;
DROP TABLE IF EXISTS Education;
DROP TABLE IF EXISTS JobSeekerProfile;
DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS Users;


-- ===================================
-- USERS TABLE
-- ===================================
CREATE TABLE Users (
    UserId          BIGINT AUTO_INCREMENT PRIMARY KEY,
    Phone           VARCHAR(20) UNIQUE,
    Name            VARCHAR(100),
    Role            ENUM('ADMIN','USER','Company'),
    Email           VARCHAR(150) UNIQUE NOT NULL,
    PasswordHash    VARCHAR(255) NOT NULL,
    active          BOOLEAN NOT NULL
);

CREATE UNIQUE INDEX UserByEmail ON Users(Email);


-- ===================================
-- LOCATION TABLE
-- ===================================
CREATE TABLE Location(
    LocationId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    Country         VARCHAR(32) NOT NULL,
    City            VARCHAR(16)
);


-- ===================================
-- USER PROFILE TABLE
-- ===================================
CREATE TABLE JobSeekerProfile (
    ProfileId       BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL UNIQUE,
    ProfilePicUrl   TEXT,
    Bio             TEXT,
    Headline        VARCHAR(255),
    Gender          ENUM('MALE','FEMALE'),
    LocationId      BIGINT,
    BirthDate       DATE,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE,
    FOREIGN KEY (LocationId) REFERENCES Location(LocationId) ON DELETE SET NULL
);

CREATE UNIQUE INDEX ProfileByUser ON JobSeekerProfile(UserId);


-- ===================================
-- EDUCATION TABLE
-- ===================================
CREATE TABLE Education (
    EducationId     BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    School          VARCHAR(200),
    degree           ENUM(
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

    FOREIGN KEY (ProfileId) REFERENCES JobSeekerProfile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX EduByProf ON Education(ProfileId);


-- ===================================
-- SKILLS TABLE
-- ===================================
CREATE TABLE Skills (
    SkillId         BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    SkillName       VARCHAR(100) NOT NULL,
    Proficiency     ENUM ('BEGINER','INTERMEDIATE','PROFECIENT'),

    FOREIGN KEY (ProfileId) REFERENCES JobSeekerProfile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX SkillsByProf ON Skills(ProfileId);


-- ===================================
-- PROJECTS TABLE (NO DIRECT USER)
-- ===================================
CREATE TABLE Projects (
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
CREATE TABLE Works_On (
    ProfileId BIGINT NOT NULL,
    ProjectId BIGINT NOT NULL,

    PRIMARY KEY (ProfileId, ProjectId),

    FOREIGN KEY (ProfileId) REFERENCES JobSeekerProfile(ProfileId) ON DELETE CASCADE,
    FOREIGN KEY (ProjectId) REFERENCES Projects(ProjectId) ON DELETE CASCADE
);


-- ===================================
-- EXPERIENCE TABLE
-- ===================================
CREATE TABLE Experience (
    ExperienceId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    ProfileId       BIGINT NOT NULL,
    CompanyName     VARCHAR(200)NOT NULL,
    Title           VARCHAR(50),
    Location        VARCHAR(150) NOT NULL,
    StartDate       DATE NOT NULL,
    EndDate         DATE NOT NULL,
    Description     TEXT,

    FOREIGN KEY (ProfileId) REFERENCES JobSeekerProfile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX ExperienceByProf ON Experience(ProfileId);


-- ===================================
-- POSTS TABLE
-- ===================================
CREATE TABLE Posts(
    PostId          BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL,
    Content         VARCHAR(2500) NOT NULL,
    CreatedAt       DATETIME,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);

CREATE INDEX PostsByUser ON Posts(UserId);


-- ===================================
-- ATTACHMENT TABLE (ONE DEFINITION)
-- ===================================
CREATE TABLE Attachment(
    AttachId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    URL         VARCHAR(150) NOT NULL,
    Type        ENUM('Video','Image','Audio','Files')
);

-- ===================================
-- POST ↔ ATTACHMENT (MANY-TO-MANY)
-- ===================================
CREATE TABLE Post_Attach(
    PostId      BIGINT,
    AttachId    BIGINT,
    PRIMARY KEY (PostId, AttachId),
    FOREIGN KEY (PostId) REFERENCES Posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (AttachId) REFERENCES Attachment(AttachId) ON DELETE CASCADE
);


-- ===================================
-- POST LIKES
-- ===================================
CREATE TABLE Post_like(
    UserId      BIGINT,
    PostId      BIGINT,
    PRIMARY KEY (PostId, UserId),

    FOREIGN KEY (PostId) REFERENCES Posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);


-- ===================================
-- COMMENTS
-- ===================================
CREATE TABLE Comments(
    CommentId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL,
    PostId          BIGINT NOT NULL,
    Content         VARCHAR(2500) NOT NULL,
    CreatedAt       DATETIME NOT NULL,
    RepliedTo      BIGINT,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE,
    FOREIGN KEY (PostId) REFERENCES Posts(PostId) ON DELETE CASCADE,
    FOREIGN KEY (RepliedTo) REFERENCES Comments(CommentId) ON DELETE CASCADE
);

CREATE INDEX CommentsByUser ON Comments(UserId);


-- ===================================
-- COMMENTS ↔ ATTACHMENT (MANY-TO-MANY)
-- ===================================
CREATE TABLE Comments_Attach(
    CommentId      BIGINT,
    AttachId        BIGINT,
    PRIMARY KEY     (CommentId, AttachId),
    
    FOREIGN KEY (CommentId) REFERENCES Comments(CommentId) ON DELETE CASCADE,
    FOREIGN KEY (AttachId) REFERENCES Attachment(AttachId) ON DELETE CASCADE
);


-- ===================================
-- COMMENT LIKES
-- ===================================
CREATE TABLE Comments_like(
    UserId      BIGINT,
    CommentId  BIGINT,
    PRIMARY KEY (CommentId, UserId),
    FOREIGN KEY (CommentId) REFERENCES Comments(CommentId) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);