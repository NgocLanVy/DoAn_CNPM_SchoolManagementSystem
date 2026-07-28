/* =====================================================================
   DB_SchoolManagementSystem
   Script tạo Database + toàn bộ bảng cho hệ thống Quản lý Trường học
   Chạy trong SQL Server Management Studio (SSMS)
   ===================================================================== */

IF DB_ID('DB_SchoolManagementSystem') IS NOT NULL
BEGIN
    ALTER DATABASE DB_SchoolManagementSystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE DB_SchoolManagementSystem;
END
GO

CREATE DATABASE DB_SchoolManagementSystem;
GO

USE DB_SchoolManagementSystem;
GO

--1. Roles - Vai trò: Admin, Teacher, Student, Parent
CREATE TABLE Roles (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL UNIQUE     -- Admin / Teacher / Student / Parent
);
GO

--2. Users - Tài khoản đăng nhập dùng chung cho 4 role
CREATE TABLE Users (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50)  NOT NULL UNIQUE,
    PasswordHash NVARCHAR(256) NOT NULL,
    FullName NVARCHAR(100) NULL,
    DateOfBirth DATE NULL,
    Address NVARCHAR(200) NULL,
    Gender NVARCHAR(10)  NULL,
    Email NVARCHAR(100) NULL,
    Phone NVARCHAR(20)  NULL,
    AvatarUrl NVARCHAR(255) NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    RoleId INT NOT NULL,
    CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleId) REFERENCES Roles(Id)
);
GO

   --3. TeacherProfiles - Hồ sơ giáo viên (tạo trước vì SchoolClasses tham chiếu tới)
CREATE TABLE TeacherProfiles (
    Id              INT IDENTITY(1,1) PRIMARY KEY,
    UserId          INT NOT NULL UNIQUE,
    TeacherCode     NVARCHAR(20) NOT NULL UNIQUE,
    Specialization  NVARCHAR(100) NULL,
    HireDate        DATE NULL,
    CONSTRAINT FK_TeacherProfiles_Users FOREIGN KEY (UserId) REFERENCES Users(Id)
);
GO

 --  4. SchoolClasses - Lớp học
CREATE TABLE SchoolClasses (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    ClassName           NVARCHAR(20) NOT NULL,      -- VD: 10A1
    GradeLevel          INT NOT NULL,               -- 10, 11, 12
    SchoolYear          NVARCHAR(20) NOT NULL,       -- VD: 2025-2026
    HomeroomTeacherId   INT NULL,
    CONSTRAINT FK_SchoolClasses_Teacher FOREIGN KEY (HomeroomTeacherId) REFERENCES TeacherProfiles(Id)
);
GO

  -- 5. Subjects - Môn học
CREATE TABLE Subjects (
    Id              INT IDENTITY(1,1) PRIMARY KEY,
    SubjectName     NVARCHAR(100) NOT NULL,
    SubjectCode     NVARCHAR(20) NOT NULL UNIQUE,
    Description     NVARCHAR(255) NULL
);
GO

   --6. StudentProfiles - Hồ sơ học sinh
CREATE TABLE StudentProfiles (
    Id              INT IDENTITY(1,1) PRIMARY KEY,
    UserId          INT NOT NULL UNIQUE,
    StudentCode     NVARCHAR(20) NOT NULL UNIQUE,
    SchoolClassId   INT NULL,
    CONSTRAINT FK_StudentProfiles_Users FOREIGN KEY (UserId) REFERENCES Users(Id),
    CONSTRAINT FK_StudentProfiles_Class FOREIGN KEY (SchoolClassId) REFERENCES SchoolClasses(Id)
);
GO

 --  7. ParentStudents - Liên kết Phụ huynh <-> Học sinh (n-n)
CREATE TABLE ParentStudents (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    ParentUserId        INT NOT NULL,
    StudentProfileId    INT NOT NULL,
    Relationship        NVARCHAR(20) NULL,   -- Cha / Mẹ / Người giám hộ
    CONSTRAINT FK_ParentStudents_Parent  FOREIGN KEY (ParentUserId) REFERENCES Users(Id),
    CONSTRAINT FK_ParentStudents_Student FOREIGN KEY (StudentProfileId) REFERENCES StudentProfiles(Id)
);
GO

  -- 8. TeachingAssignments - Phân công giảng dạy
CREATE TABLE TeachingAssignments (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    SchoolClassId       INT NOT NULL,
    SubjectId           INT NOT NULL,
    TeacherProfileId    INT NOT NULL,
    SchoolYear          NVARCHAR(20) NULL,
    CONSTRAINT FK_TA_Class   FOREIGN KEY (SchoolClassId) REFERENCES SchoolClasses(Id),
    CONSTRAINT FK_TA_Subject FOREIGN KEY (SubjectId) REFERENCES Subjects(Id),
    CONSTRAINT FK_TA_Teacher FOREIGN KEY (TeacherProfileId) REFERENCES TeacherProfiles(Id)
);
GO

 --  9. Attendances - Điểm danh
CREATE TABLE Attendances (
    Id                      INT IDENTITY(1,1) PRIMARY KEY,
    StudentProfileId        INT NOT NULL,
    SubjectId               INT NOT NULL,
    [Date]                  DATE NOT NULL,
    Status                  NVARCHAR(20) NOT NULL,  -- Present/Absent/Late/ExcusedAbsence
    Note                    NVARCHAR(255) NULL,
    RecordedByTeacherId     INT NULL,
    CONSTRAINT FK_Attendance_Student FOREIGN KEY (StudentProfileId) REFERENCES StudentProfiles(Id),
    CONSTRAINT FK_Attendance_Subject FOREIGN KEY (SubjectId) REFERENCES Subjects(Id),
    CONSTRAINT FK_Attendance_Teacher FOREIGN KEY (RecordedByTeacherId) REFERENCES Users(Id)
);
GO

 -- 10. Grades - Điểm số
CREATE TABLE Grades (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    StudentProfileId    INT NOT NULL,
    SubjectId           INT NOT NULL,
    ExamType            NVARCHAR(30) NOT NULL,   -- Mieng/15Phut/GiuaKy/CuoiKy
    Score               FLOAT NOT NULL CHECK (Score >= 0 AND Score <= 10),
    Semester            NVARCHAR(30) NULL,        -- VD: HK1 2025-2026
    CONSTRAINT FK_Grades_Student FOREIGN KEY (StudentProfileId) REFERENCES StudentProfiles(Id),
    CONSTRAINT FK_Grades_Subject FOREIGN KEY (SubjectId) REFERENCES Subjects(Id)
);
GO

 --  11. Schedules - Thời khóa biểu
CREATE TABLE Schedules (
    Id              INT IDENTITY(1,1) PRIMARY KEY,
    SchoolClassId   INT NOT NULL,
    SubjectId       INT NOT NULL,
    TeacherId       INT NULL,     -- FK -> TeacherProfiles.Id
    DayOfWeek       INT NOT NULL, -- 0=Sunday...6=Saturday
    StartTime       TIME NOT NULL,
    EndTime         TIME NOT NULL,
    Room            NVARCHAR(20) NULL,
    CONSTRAINT FK_Schedules_Class   FOREIGN KEY (SchoolClassId) REFERENCES SchoolClasses(Id),
    CONSTRAINT FK_Schedules_Subject FOREIGN KEY (SubjectId) REFERENCES Subjects(Id),
    CONSTRAINT FK_Schedules_Teacher FOREIGN KEY (TeacherId) REFERENCES TeacherProfiles(Id)
);
GO

--   12. Notifications - Thông báo GV <-> PH
CREATE TABLE Notifications (
    Id              INT IDENTITY(1,1) PRIMARY KEY,
    SenderId        INT NULL,
    ReceiverId      INT NULL,
    Title           NVARCHAR(200) NULL,
    Content         NVARCHAR(MAX) NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    IsRead          BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_Notifications_Sender   FOREIGN KEY (SenderId)   REFERENCES Users(Id),
    CONSTRAINT FK_Notifications_Receiver FOREIGN KEY (ReceiverId) REFERENCES Users(Id)
);
GO

  -- 13. Tuitions - Học phí
CREATE TABLE Tuitions (
    Id                  INT IDENTITY(1,1) PRIMARY KEY,
    StudentProfileId    INT NOT NULL,
    Semester            NVARCHAR(30) NULL,
    Amount              DECIMAL(18,2) NOT NULL,
    DueDate             DATE NOT NULL,
    PaidDate            DATE NULL,
    Status              NVARCHAR(20) NOT NULL DEFAULT 'Unpaid',  -- Unpaid/Paid/Overdue
    CONSTRAINT FK_Tuitions_Student FOREIGN KEY (StudentProfileId) REFERENCES StudentProfiles(Id)
);
GO

--   DỮ LIỆU MẪU (seed data) để test
INSERT INTO Roles (RoleName) VALUES (N'Admin'), (N'Teacher'), (N'Student'), (N'Parent');
GO
INSERT INTO Users (Username, PasswordHash, FullName, Gender, IsActive, RoleId)
VALUES
(N'admin',    N'123456', N'Quản trị viên',     N'Nam', 1, 1),
(N'gv001',    N'123456', N'Nguyễn Văn A',      N'Nam', 1, 2),
(N'hs001',    N'123456', N'Trần Thị B',        N'Nữ',  1, 3),
(N'ph001',    N'123456', N'Trần Văn C',        N'Nam', 1, 4);
GO

INSERT INTO TeacherProfiles (UserId, TeacherCode, Specialization, HireDate)
VALUES (2, N'GV0001', N'Toán học', '2020-08-01');
GO

INSERT INTO SchoolClasses (ClassName, GradeLevel, SchoolYear, HomeroomTeacherId)
VALUES (N'10A1', 10, N'2025-2026', 1);
GO

INSERT INTO StudentProfiles (UserId, StudentCode, SchoolClassId)
VALUES (3, N'HS0001', 1);
GO

INSERT INTO Subjects (SubjectName, SubjectCode, Description)
VALUES (N'Toán học', N'MATH01', N'Môn Toán khối THPT');
GO

PRINT N'Tạo database và dữ liệu mẫu thành công!';
