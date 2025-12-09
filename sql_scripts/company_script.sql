CREATE TABLE CompanyProfile (
    CompanyProfileId BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserId          BIGINT NOT NULL UNIQUE,
    CompanyName     VARCHAR(100) NOT NULL,
    Website         VARCHAR(255),
    Industry        VARCHAR(100),
    Description     TEXT,
    LogoUrl         TEXT,
    CoverImageUrl   TEXT,
    numberOfFollowers BIGINT,
    LocationId      BIGINT,
    Founded         YEAR,
    CreatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (UserId) REFERENCES user(UserId) ON DELETE CASCADE,
    FOREIGN KEY (LocationId) REFERENCES location(LocationId) ON DELETE SET NULL
);

CREATE UNIQUE INDEX CompanyProfileByUser ON CompanyProfile(UserId);
CREATE INDEX CompanyProfileByIndustry ON CompanyProfile(Industry);


CREATE TABLE Company_Followers (
    FollowerId      BIGINT,
    CompanyId       BIGINT,
    FollowedAt      DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (FollowerId, CompanyId),
    
    FOREIGN KEY (FollowerId) REFERENCES user(UserId) ON DELETE CASCADE,
    FOREIGN KEY (CompanyId) REFERENCES user(UserId) ON DELETE CASCADE
);

CREATE INDEX FollowersByUser ON Company_Followers(FollowerId);
CREATE INDEX FollowersByCompany ON Company_Followers(CompanyId);