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


INSERT INTO Users 
    (Username, PasswordHash, FullName, Gender, IsActive, RoleId)
VALUES
(N'gv002', N'123456', N'Lê Thị Hồng',       N'Nữ',  1, 2),
(N'gv003', N'123456', N'Phạm Minh Tuấn',    N'Nam', 1, 2),
(N'gv004', N'123456', N'Đặng Thị Lan',      N'Nữ',  1, 2),

(N'hs002', N'123456', N'Nguyễn Minh Anh',   N'Nữ',  1, 3),
(N'hs003', N'123456', N'Lê Hoàng Nam',     N'Nam', 1, 3),
(N'hs004', N'123456', N'Phạm Ngọc Mai',    N'Nữ',  1, 3),
(N'hs005', N'123456', N'Trần Minh Đức',    N'Nam', 1, 3),
(N'hs006', N'123456', N'Nguyễn Thùy Linh', N'Nữ',  1, 3),
(N'hs007', N'123456', N'Võ Quốc Huy',      N'Nam', 1, 3),
(N'hs008', N'123456', N'Đỗ Khánh Vy',      N'Nữ',  1, 3),
(N'hs009', N'123456', N'Bùi Anh Khoa',      N'Nam', 1, 3),
(N'hs010', N'123456', N'Hoàng Gia Hân',     N'Nữ',  1, 3),

(N'ph002', N'123456', N'Lê Văn Thành',      N'Nam', 1, 4),
(N'ph003', N'123456', N'Nguyễn Thị Hoa',    N'Nữ',  1, 4),
(N'ph004', N'123456', N'Phạm Văn Long',     N'Nam', 1, 4);
GO

INSERT INTO TeacherProfiles
    (UserId, TeacherCode, Specialization, HireDate)
VALUES
((SELECT Id FROM Users WHERE Username = N'gv002'),
 N'GV0002', N'Ngữ văn', '2021-08-01'),

((SELECT Id FROM Users WHERE Username = N'gv003'),
 N'GV0003', N'Tiếng Anh', '2022-08-15'),

((SELECT Id FROM Users WHERE Username = N'gv004'),
 N'GV0004', N'Vật lý', '2019-09-01');
GO



INSERT INTO SchoolClasses
    (ClassName, GradeLevel, SchoolYear, HomeroomTeacherId)
VALUES
(
    N'10A2',
    10,
    N'2025-2026',
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0002')
),
(
    N'11A1',
    11,
    N'2025-2026',
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0003')
),
(
    N'11A2',
    11,
    N'2025-2026',
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0004')
),
(
    N'12A1',
    12,
    N'2025-2026',
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001')
);
GO


INSERT INTO Subjects
    (SubjectName, SubjectCode, Description)
VALUES
(N'Ngữ văn',       N'LIT01', N'Môn Ngữ văn THPT'),
(N'Tiếng Anh',     N'ENG01', N'Môn Tiếng Anh THPT'),
(N'Vật lý',        N'PHY01', N'Môn Vật lý THPT'),
(N'Hóa học',       N'CHEM01', N'Môn Hóa học THPT'),
(N'Sinh học',      N'BIO01', N'Môn Sinh học THPT'),
(N'Lịch sử',       N'HIS01', N'Môn Lịch sử THPT'),
(N'Địa lý',        N'GEO01', N'Môn Địa lý THPT');
GO


INSERT INTO StudentProfiles
    (UserId, StudentCode, SchoolClassId)
VALUES
(
    (SELECT Id FROM Users WHERE Username = N'hs002'),
    N'HS0002',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs003'),
    N'HS0003',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs004'),
    N'HS0004',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A2')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs005'),
    N'HS0005',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A2')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs006'),
    N'HS0006',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A1')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs007'),
    N'HS0007',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A1')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs008'),
    N'HS0008',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A2')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs009'),
    N'HS0009',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A2')
),
(
    (SELECT Id FROM Users WHERE Username = N'hs010'),
    N'HS0010',
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'12A1')
);
GO


INSERT INTO TeachingAssignments
    (SchoolClassId, SubjectId, TeacherProfileId, SchoolYear)
VALUES
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001'),
    N'2025-2026'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'LIT01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0002'),
    N'2025-2026'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0003'),
    N'2025-2026'
),

(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A2'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001'),
    N'2025-2026'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A2'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'PHY01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0004'),
    N'2025-2026'
),

(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0003'),
    N'2025-2026'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'LIT01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0002'),
    N'2025-2026'
),

(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A2'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'PHY01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0004'),
    N'2025-2026'
),

(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'12A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001'),
    N'2025-2026'
);
GO


INSERT INTO ParentStudents
    (ParentUserId, StudentProfileId, Relationship)
VALUES
(
    (SELECT Id FROM Users WHERE Username = N'ph001'),
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0001'),
    N'Cha'
),
(
    (SELECT Id FROM Users WHERE Username = N'ph002'),
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0002'),
    N'Cha'
),
(
    (SELECT Id FROM Users WHERE Username = N'ph003'),
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0004'),
    N'Mẹ'
),
(
    (SELECT Id FROM Users WHERE Username = N'ph004'),
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0006'),
    N'Cha'
);
GO


INSERT INTO Attendances
    (StudentProfileId, SubjectId, [Date], Status, Note, RecordedByTeacherId)
VALUES
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0001'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    '2025-09-08',
    N'Present',
    N'Có mặt',
    (SELECT Id FROM Users WHERE Username = N'gv001')
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0002'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    '2025-09-08',
    N'Late',
    N'Đến muộn 10 phút',
    (SELECT Id FROM Users WHERE Username = N'gv001')
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0003'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    '2025-09-08',
    N'Absent',
    N'Nghỉ không phép',
    (SELECT Id FROM Users WHERE Username = N'gv001')
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0004'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'LIT01'),
    '2025-09-09',
    N'Present',
    NULL,
    (SELECT Id FROM Users WHERE Username = N'gv002')
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0006'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    '2025-09-09',
    N'ExcusedAbsence',
    N'Có giấy xin phép',
    (SELECT Id FROM Users WHERE Username = N'gv003')
);
GO


INSERT INTO Grades
    (StudentProfileId, SubjectId, ExamType, Score, Semester)
VALUES
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0001'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    N'Mieng',
    8.5,
    N'HK1 2025-2026'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0001'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    N'15Phut',
    9.0,
    N'HK1 2025-2026'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0002'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    N'GiuaKy',
    7.5,
    N'HK1 2025-2026'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0003'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'LIT01'),
    N'GiuaKy',
    8.0,
    N'HK1 2025-2026'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0004'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    N'CuoiKy',
    9.0,
    N'HK1 2025-2026'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0006'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    N'GiuaKy',
    6.5,
    N'HK1 2025-2026'
);
GO



INSERT INTO Schedules
    (SchoolClassId, SubjectId, TeacherId, DayOfWeek, StartTime, EndTime, Room)
VALUES
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001'),
    1,
    '07:00',
    '07:45',
    N'P101'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'LIT01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0002'),
    2,
    '07:00',
    '07:45',
    N'P101'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0003'),
    3,
    '08:00',
    '08:45',
    N'P101'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'10A2'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'MATH01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0001'),
    4,
    '07:00',
    '07:45',
    N'P102'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A1'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'ENG01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0003'),
    5,
    '08:00',
    '08:45',
    N'P201'
),
(
    (SELECT Id FROM SchoolClasses WHERE ClassName = N'11A2'),
    (SELECT Id FROM Subjects WHERE SubjectCode = N'PHY01'),
    (SELECT Id FROM TeacherProfiles WHERE TeacherCode = N'GV0004'),
    1,
    '09:00',
    '09:45',
    N'LAB01'
);
GO


INSERT INTO Tuitions
    (StudentProfileId, Semester, Amount, DueDate, PaidDate, Status)
VALUES
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0001'),
    N'HK1 2025-2026',
    3500000,
    '2025-09-30',
    '2025-09-20',
    N'Paid'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0002'),
    N'HK1 2025-2026',
    3500000,
    '2025-09-30',
    NULL,
    N'Unpaid'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0003'),
    N'HK1 2025-2026',
    3500000,
    '2025-09-30',
    '2025-09-25',
    N'Paid'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0004'),
    N'HK1 2025-2026',
    3700000,
    '2025-10-05',
    NULL,
    N'Unpaid'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0006'),
    N'HK1 2025-2026',
    3700000,
    '2025-10-05',
    '2025-09-28',
    N'Paid'
),
(
    (SELECT Id FROM StudentProfiles WHERE StudentCode = N'HS0008'),
    N'HK1 2025-2026',
    4000000,
    '2025-10-10',
    NULL,
    N'Overdue'
);
GO


INSERT INTO Notifications
    (SenderId, ReceiverId, Title, Content, IsRead)
VALUES
(
    (SELECT Id FROM Users WHERE Username = N'gv001'),
    (SELECT Id FROM Users WHERE Username = N'ph001'),
    N'Thông báo điểm danh',
    N'Học sinh Trần Thị B đã được điểm danh có mặt môn Toán.',
    0
),
(
    (SELECT Id FROM Users WHERE Username = N'gv002'),
    (SELECT Id FROM Users WHERE Username = N'ph002'),
    N'Thông báo học tập',
    N'Phụ huynh vui lòng theo dõi tình hình học tập của học sinh.',
    0
),
(
    (SELECT Id FROM Users WHERE Username = N'gv003'),
    (SELECT Id FROM Users WHERE Username = N'ph003'),
    N'Thông báo kiểm tra',
    N'Học sinh có bài kiểm tra Tiếng Anh trong tuần này.',
    1
);
GO


SELECT * FROM Roles;
SELECT * FROM Users;
SELECT * FROM TeacherProfiles;
SELECT * FROM SchoolClasses;
SELECT * FROM Subjects;
SELECT * FROM StudentProfiles;
SELECT * FROM ParentStudents;
SELECT * FROM TeachingAssignments;
SELECT * FROM Attendances;
SELECT * FROM Grades;
SELECT * FROM Schedules;
SELECT * FROM Notifications;
SELECT * FROM Tuitions;