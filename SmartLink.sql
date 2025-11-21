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
    Phone           VARCHAR(32) UNIQUE,
    Name            VARCHAR(100),
    Role            VARCHAR(50),
    Email           VARCHAR(150) UNIQUE NOT NULL,
    PasswordHash    VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX UserByEmail ON Users(Email);


-- ===================================
-- LOCATION TABLE
-- ===================================
CREATE TABLE Location(
    LocationId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    Country         VARCHAR(32),
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
    Degree          VARCHAR(200),
    FieldOfStudy    VARCHAR(200),
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
    Proficiency     VARCHAR(50),

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
    CompanyName     VARCHAR(200),
    Title           VARCHAR(200),
    Location        VARCHAR(150),
    StartDate       DATE,
    EndDate         DATE,
    Description     TEXT,

    FOREIGN KEY (ProfileId) REFERENCES JobSeekerProfile(ProfileId) ON DELETE CASCADE
);

CREATE INDEX ExperienceByProf ON Experience(ProfileId);


-- ===================================
-- POSTS TABLE
-- ===================================
CREATE TABLE Posts(
    PostId          BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT,
    Content         VARCHAR(2500),
    CreatedAt       DATETIME,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);

CREATE INDEX PostsByUser ON Posts(UserId);


-- ===================================
-- ATTACHMENT TABLE (ONE DEFINITION)
-- ===================================
CREATE TABLE Attachment(
    AttachId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    URL         VARCHAR(100)
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
    CommentsId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT,
    PostId          BIGINT,
    Content         VARCHAR(2500),
    CreatedAt       DATETIME,

    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE,
    FOREIGN KEY (PostId) REFERENCES Posts(PostId) ON DELETE CASCADE
);

CREATE INDEX CommentsByUser ON Comments(UserId);


-- ===================================
-- COMMENTS ↔ ATTACHMENT (MANY-TO-MANY)
-- ===================================
CREATE TABLE Comments_Attach(
    CommentsId      BIGINT,
    AttachId        BIGINT,
    PRIMARY KEY     (CommentsId, AttachId),
    
    FOREIGN KEY (CommentsId) REFERENCES Comments(CommentsId) ON DELETE CASCADE,
    FOREIGN KEY (AttachId) REFERENCES Attachment(AttachId) ON DELETE CASCADE
);


-- ===================================
-- COMMENT LIKES
-- ===================================
CREATE TABLE Comments_like(
    UserId      BIGINT,
    CommentsId  BIGINT,
    PRIMARY KEY (CommentsId, UserId),

    FOREIGN KEY (CommentsId) REFERENCES Comments(CommentsId) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES Users(UserId) ON DELETE CASCADE
);
