/*
    SQL Server bootstrap script for MISEnglish
    - Creates database and required tables if missing
    - Seeds demo accounts, courses, rooms, and person records
    - Safe to run multiple times (uses IF NOT EXISTS / UPDATE patterns)
*/

SET NOCOUNT ON;

IF DB_ID(N'coolenglish') IS NULL
BEGIN
    CREATE DATABASE coolenglish;
END;
GO

USE coolenglish;
GO

/* -----------------------------
   1) TABLES
------------------------------ */

IF OBJECT_ID(N'dbo.user_account', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.user_account (
        user_id BIGINT IDENTITY(1,1) NOT NULL,
        email NVARCHAR(100) NOT NULL,
        username NVARCHAR(50) NOT NULL,
        password NVARCHAR(100) NOT NULL,
        related_id BIGINT NULL,
        role NVARCHAR(20) NOT NULL,
        CONSTRAINT PK_user_account PRIMARY KEY (user_id),
        CONSTRAINT UQ_user_account_email UNIQUE (email),
        CONSTRAINT CK_user_account_role CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT', 'STAFF'))
    );
END;

IF COL_LENGTH(N'dbo.user_account', N'user_id') IS NULL
BEGIN
    ALTER TABLE dbo.user_account
    ADD user_id BIGINT IDENTITY(1,1) NOT NULL;
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints kc
    WHERE kc.parent_object_id = OBJECT_ID(N'dbo.user_account')
      AND kc.[type] = 'PK'
      AND kc.name = N'PK_user_account'
)
BEGIN
    DECLARE @pkName NVARCHAR(128);
    SELECT @pkName = kc.name
    FROM sys.key_constraints kc
    WHERE kc.parent_object_id = OBJECT_ID(N'dbo.user_account')
      AND kc.[type] = 'PK';

    IF @pkName IS NOT NULL
    BEGIN
        EXEC(N'ALTER TABLE dbo.user_account DROP CONSTRAINT ' + QUOTENAME(@pkName));
    END;

    ALTER TABLE dbo.user_account
    ADD CONSTRAINT PK_user_account PRIMARY KEY (user_id);
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints kc
    WHERE kc.parent_object_id = OBJECT_ID(N'dbo.user_account')
      AND kc.[type] = 'UQ'
      AND kc.name = N'UQ_user_account_email'
)
BEGIN
    ALTER TABLE dbo.user_account
    ADD CONSTRAINT UQ_user_account_email UNIQUE (email);
END;
GO

IF OBJECT_ID(N'dbo.Courses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Courses (
        courseID NVARCHAR(50) NOT NULL,
        courseName NVARCHAR(255) NULL,
        description NVARCHAR(MAX) NULL,
        level NVARCHAR(100) NULL,
        duration INT NULL,
        fee FLOAT NULL,
        status NVARCHAR(50) NULL,
        CONSTRAINT PK_Courses PRIMARY KEY (courseID)
    );
END;
GO

IF OBJECT_ID(N'dbo.Rooms', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Rooms (
        roomID NVARCHAR(50) NOT NULL,
        roomName NVARCHAR(255) NULL,
        capacity INT NULL,
        location NVARCHAR(255) NULL,
        status NVARCHAR(50) NULL,
        CONSTRAINT PK_Rooms PRIMARY KEY (roomID),
        CONSTRAINT CK_Rooms_status CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE'))
    );
END;
GO

IF OBJECT_ID(N'dbo.Classes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Classes (
        classID NVARCHAR(50) NOT NULL,
        className NVARCHAR(150) NOT NULL,
        start_date DATE NULL,
        end_date DATE NULL,
        courseID NVARCHAR(50) NULL,
        roomID NVARCHAR(50) NULL,
        maxCapacity INT NULL,
        currentEnrollment INT NULL,
        status NVARCHAR(20) NOT NULL,
        CONSTRAINT PK_Classes PRIMARY KEY (classID),
        CONSTRAINT CK_Classes_status CHECK (status IN ('PLANNED', 'OPEN', 'RUNNING', 'CLOSED', 'CANCELLED'))
    );
END;
GO

IF OBJECT_ID(N'dbo.person', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.person (
        id BIGINT IDENTITY(1,1) NOT NULL,
        person_type NVARCHAR(31) NOT NULL,
        full_name NVARCHAR(100) NOT NULL,
        gender NVARCHAR(10) NULL,
        phone NVARCHAR(20) NULL,
        email NVARCHAR(100) NULL,
        student_id NVARCHAR(50) NULL,
        date_of_birth DATE NULL,
        registration_date DATE NULL,
        teacher_id NVARCHAR(50) NULL,
        specialty NVARCHAR(100) NULL,
        hire_date DATE NULL,
        certificate NVARCHAR(255) NULL,
        staff_id NVARCHAR(50) NULL,
        position NVARCHAR(100) NULL,
        status NVARCHAR(30) NULL,
        CONSTRAINT PK_person PRIMARY KEY (id),
        CONSTRAINT CK_person_type CHECK (person_type IN ('STUDENT', 'TEACHER', 'STAFF')),
        CONSTRAINT CK_person_gender CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER'))
    );
END;
GO

IF OBJECT_ID(N'dbo.Enrollments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Enrollments (
        id BIGINT IDENTITY(1,1) NOT NULL,
        class_id NVARCHAR(50) NOT NULL,
        student_person_id BIGINT NOT NULL,
        status NVARCHAR(20) NOT NULL,
        enrolledAt DATETIME2 NOT NULL,
        CONSTRAINT PK_Enrollments PRIMARY KEY (id),
        CONSTRAINT FK_Enrollments_Classes FOREIGN KEY (class_id) REFERENCES dbo.Classes(classID),
        CONSTRAINT FK_Enrollments_Person FOREIGN KEY (student_person_id) REFERENCES dbo.person(id),
        CONSTRAINT CK_Enrollments_status CHECK (status IN ('ENROLLED', 'STUDYING', 'COMPLETED', 'DROPPED', 'CANCELLED'))
    );
END;
GO

IF OBJECT_ID(N'dbo.Invoices', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Invoices (
        id BIGINT IDENTITY(1,1) NOT NULL,
        invoiceNumber NVARCHAR(50) NOT NULL UNIQUE,
        enrollment_id BIGINT NULL,
        totalAmount FLOAT NOT NULL,
        status NVARCHAR(20) NOT NULL,
        createdAt DATETIME2 NOT NULL,
        paidAt DATETIME2 NULL,
        CONSTRAINT PK_Invoices PRIMARY KEY (id),
        CONSTRAINT FK_Invoices_Enrollments FOREIGN KEY (enrollment_id) REFERENCES dbo.Enrollments(id),
        CONSTRAINT CK_Invoices_status CHECK (status IN ('UNPAID', 'PARTIALLY_PAID', 'PAID', 'CANCELLED'))
    );
END;
GO

/* -----------------------------
   2) SEED USER ACCOUNTS
   Passwords are plain text because current login checks plain text.
------------------------------ */

IF NOT EXISTS (SELECT 1 FROM dbo.user_account WHERE email = 'admin@coolenglish.vn')
BEGIN
    INSERT INTO dbo.user_account (email, username, password, role)
    VALUES ('admin@coolenglish.vn', 'admin', 'admin123', 'ADMIN');
END
ELSE
BEGIN
    UPDATE dbo.user_account
    SET username = 'admin', password = 'admin123', role = 'ADMIN'
    WHERE email = 'admin@coolenglish.vn';
END;

IF NOT EXISTS (SELECT 1 FROM dbo.user_account WHERE email = 'staff@coolenglish.vn')
BEGIN
    INSERT INTO dbo.user_account (email, username, password, role)
    VALUES ('staff@coolenglish.vn', 'staff', 'staff123', 'STAFF');
END
ELSE
BEGIN
    UPDATE dbo.user_account
    SET username = 'staff', password = 'staff123', role = 'STAFF'
    WHERE email = 'staff@coolenglish.vn';
END;

IF NOT EXISTS (SELECT 1 FROM dbo.user_account WHERE email = 'teacher@coolenglish.vn')
BEGIN
    INSERT INTO dbo.user_account (email, username, password, role)
    VALUES ('teacher@coolenglish.vn', 'teacher', 'teacher123', 'TEACHER');
END
ELSE
BEGIN
    UPDATE dbo.user_account
    SET username = 'teacher', password = 'teacher123', role = 'TEACHER'
    WHERE email = 'teacher@coolenglish.vn';
END;

IF NOT EXISTS (SELECT 1 FROM dbo.user_account WHERE email = 'student@coolenglish.vn')
BEGIN
    INSERT INTO dbo.user_account (email, username, password, role)
    VALUES ('student@coolenglish.vn', 'student', 'student123', 'STUDENT');
END
ELSE
BEGIN
    UPDATE dbo.user_account
    SET username = 'student', password = 'student123', role = 'STUDENT'
    WHERE email = 'student@coolenglish.vn';
END;
GO

/* -----------------------------
   3) SEED COURSES
------------------------------ */

MERGE dbo.Courses AS target
USING (
    SELECT 'CE101' AS courseID, 'Basic English Communication' AS courseName,
           'Foundation course for daily speaking and listening' AS description,
           'BEGINNER' AS level, 48 AS duration, 1800000.0 AS fee, 'ACTIVE' AS status
    UNION ALL
    SELECT 'CE201', 'English for Office',
           'Business writing, meetings, and presentations',
           'INTERMEDIATE', 60, 2400000.0, 'ACTIVE'
    UNION ALL
    SELECT 'CE301', 'IELTS Preparation',
           'Intensive IELTS 4 skills practice',
           'UPPER_INTERMEDIATE', 72, 3500000.0, 'ACTIVE'
    UNION ALL
    SELECT 'CE401', 'Public Speaking in English',
           'Confidence building and speech techniques',
           'ADVANCED', 36, 2200000.0, 'ACTIVE'
    UNION ALL
    SELECT 'CE999', 'Archived Pilot Course',
           'Old pilot course for test data',
           'BEGINNER', 24, 900000.0, 'INACTIVE'
) AS src
ON target.courseID = src.courseID
WHEN MATCHED THEN
    UPDATE SET
        courseName = src.courseName,
        description = src.description,
        level = src.level,
        duration = src.duration,
        fee = src.fee,
        status = src.status
WHEN NOT MATCHED THEN
    INSERT (courseID, courseName, description, level, duration, fee, status)
    VALUES (src.courseID, src.courseName, src.description, src.level, src.duration, src.fee, src.status);
GO

/* -----------------------------
   4) SEED ROOMS
------------------------------ */

MERGE dbo.Rooms AS target
USING (
    SELECT 'R101' AS roomID, 'Room Sky A' AS roomName, 20 AS capacity, 'Building A - Floor 1' AS location, 'AVAILABLE' AS status
    UNION ALL
    SELECT 'R102', 'Room Sky B', 25, 'Building A - Floor 1', 'AVAILABLE'
    UNION ALL
    SELECT 'R201', 'Room Cloud A', 30, 'Building A - Floor 2', 'OCCUPIED'
    UNION ALL
    SELECT 'R202', 'Room Cloud B', 35, 'Building A - Floor 2', 'MAINTENANCE'
) AS src
ON target.roomID = src.roomID
WHEN MATCHED THEN
    UPDATE SET
        roomName = src.roomName,
        capacity = src.capacity,
        location = src.location,
        status = src.status
WHEN NOT MATCHED THEN
    INSERT (roomID, roomName, capacity, location, status)
    VALUES (src.roomID, src.roomName, src.capacity, src.location, src.status);
GO

/* -----------------------------
   5) SEED CLASSES
------------------------------ */

MERGE dbo.Classes AS target
USING (
    SELECT 'CLS001' AS classID, 'Starter Speaking A1 - Morning' AS className,
           'CE101' AS courseID, 'R101' AS roomID, 2 AS maxCapacity, 0 AS currentEnrollment, 'OPEN' AS status
    UNION ALL
    SELECT 'CLS002', 'Office English B1 - Evening',
           'CE201', 'R102', 20, 12, 'RUNNING'
    UNION ALL
    SELECT 'CLS003', 'IELTS Weekend Closed Batch',
           'CE301', 'R201', 25, 25, 'CLOSED'
) AS src
ON target.classID = src.classID
WHEN MATCHED THEN
    UPDATE SET
        className = src.className,
        courseID = src.courseID,
        roomID = src.roomID,
        maxCapacity = src.maxCapacity,
        currentEnrollment = src.currentEnrollment,
        status = src.status
WHEN NOT MATCHED THEN
    INSERT (classID, className, courseID, roomID, maxCapacity, currentEnrollment, status)
    VALUES (src.classID, src.className, src.courseID, src.roomID, src.maxCapacity, src.currentEnrollment, src.status);
GO

/* -----------------------------
   6) SEED PERSON (single-table inheritance)
------------------------------ */

IF NOT EXISTS (SELECT 1 FROM dbo.person WHERE email = 'staff@coolenglish.vn' AND person_type = 'STAFF')
BEGIN
    INSERT INTO dbo.person (person_type, full_name, gender, phone, email, staff_id, position)
    VALUES ('STAFF', 'Main Staff', 'FEMALE', '0901000001', 'staff@coolenglish.vn', 'STF001', 'Academic Coordinator');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.person WHERE email = 'teacher@coolenglish.vn' AND person_type = 'TEACHER')
BEGIN
    INSERT INTO dbo.person (person_type, full_name, gender, phone, email, teacher_id, status)
    VALUES ('TEACHER', 'Main Teacher', 'MALE', '0901000002', 'teacher@coolenglish.vn', 'TCH001', 'ACTIVE');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.person WHERE email = 'student@coolenglish.vn' AND person_type = 'STUDENT')
BEGIN
    INSERT INTO dbo.person (person_type, full_name, gender, phone, email, student_id, status)
    VALUES ('STUDENT', 'Main Student', 'OTHER', '0901000003', 'student@coolenglish.vn', 'STD001', 'ACTIVE');
END;
GO

/* -----------------------------
   7) SEED ENROLLMENTS
------------------------------ */

IF NOT EXISTS (
    SELECT 1
    FROM dbo.Enrollments e
    INNER JOIN dbo.person p ON p.id = e.student_person_id
    WHERE e.class_id = 'CLS002' AND p.email = 'student@coolenglish.vn'
)
BEGIN
    INSERT INTO dbo.Enrollments (class_id, student_person_id, status, enrolledAt)
    SELECT TOP 1 'CLS002', p.id, 'ENROLLED', SYSUTCDATETIME()
    FROM dbo.person p
    WHERE p.email = 'student@coolenglish.vn' AND p.person_type = 'STUDENT';
END;
GO

/* Quick check */
SELECT 'user_account' AS table_name, COUNT(*) AS total_rows FROM dbo.user_account
UNION ALL
SELECT 'Courses', COUNT(*) FROM dbo.Courses
UNION ALL
SELECT 'Rooms', COUNT(*) FROM dbo.Rooms
UNION ALL
SELECT 'Classes', COUNT(*) FROM dbo.Classes
UNION ALL
SELECT 'Enrollments', COUNT(*) FROM dbo.Enrollments
UNION ALL
SELECT 'Invoices', COUNT(*) FROM dbo.Invoices
UNION ALL
SELECT 'person', COUNT(*) FROM dbo.person;
GO

