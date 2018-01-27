CREATE TABLE Log (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    LogTime TIMESTAMP,
    HealthParameterID BigInt,
    HealthParameterValue double precision
);

CREATE TABLE HealthParameter (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    Signature VarChar(10)
);

CREATE TABLE Status (
    ID BigSerial PRIMARY KEY,
    HealthParameterID BigInt,
    ActivityID BigInt,
    UpperBound double precision,
    LowerBound double precision
);

CREATE TABLE Activity (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50),
    ExpectedEffect VarChar(500),
    TypeID BigInt,
    StatusID BigInt
);

CREATE TABLE ActionType (
    ID BigSerial PRIMARY KEY,
    Name VarChar(50)
);

CREATE TABLE Task (
    ID BigSerial PRIMARY KEY,
    OperatorID BigInt,
    StartTime TIMESTAMP,
    EndTime TIMESTAMP,
    ActivityID BigInt,
    Advancement VarChar(50)
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