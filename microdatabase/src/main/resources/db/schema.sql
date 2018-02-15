CREATE TABLE Activity (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    ActivityTypeID BigInt,
    Acronym VarChar(50),
    BoundaryID BigInt
);

CREATE TABLE ActionType (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50)
);

CREATE TABLE Boundary (
    ID BigSerial PRIMARY KEY,
    HealthParameterID BigInt,
    ActivityId BigInt,
    Upperbound double precision,
    Lowerbound double precision,
    LightWarning_Offset double precision,
    Status VarChar(50),
    ItsGood Boolean,
    MinAge double precision,
    MaxAge double precision
);

CREATE TABLE HealthParameter (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    Acronym VarChar(10)
);

CREATE TABLE Log (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    LogTime TIMESTAMP,
    HealthParameterID BigInt,
    HealthParameterValue double precision,
    SessionID BigInt
);

CREATE TABLE Operator (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    Surname VarChar(50),
    RoleID BigInt,
    isActive Boolean
);

CREATE TABLE Role (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50)
);

CREATE TABLE Session (
    ID BigSerial PRIMARY KEY,
    CF VarChar(50),
    StartDate TIMESTAMP,
    EndDate TIMESTAMP,
    MicroServiceInstanceId BigInt
);

CREATE TABLE Task (
    ID BigInt NOT NULL,
    SessionID BigInt NOT NULL,
    OperatorID BigInt,
    StartTime TIMESTAMP,
    EndTime TIMESTAMP,
    ActivityID BigInt,
    StatusID BigInt
);

ALTER TABLE Task ADD CONSTRAINT task_pkey PRIMARY KEY (id, sessionid);

CREATE TABLE TaskStatus (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50)
);