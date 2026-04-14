-- Script SQL để cập nhật database cho Upload Ảnh
-- Thực thi các câu lệnh này để thêm các field mới vào database

SET NOCOUNT ON;

-- Thêm field avatar vào bảng person
IF COL_LENGTH('dbo.person', 'avatar') IS NULL
	ALTER TABLE [dbo].[person] ADD [avatar] NVARCHAR(500) NULL;

-- Thêm field image vào bảng Courses
IF COL_LENGTH('dbo.Courses', 'image') IS NULL
	ALTER TABLE [dbo].[Courses] ADD [image] NVARCHAR(500) NULL;

-- Thêm field image_link vào bảng AcademicContents
IF COL_LENGTH('dbo.AcademicContents', 'image_link') IS NULL
	ALTER TABLE [dbo].[AcademicContents] ADD [image_link] NVARCHAR(500) NULL;

-- Chuyển các cột text liên quan roadmap sang NVARCHAR để giữ nguyên dấu tiếng Việt
IF COL_LENGTH('dbo.Roadmaps', 'title') IS NOT NULL
	ALTER TABLE [dbo].[Roadmaps] ALTER COLUMN [title] NVARCHAR(255) NOT NULL;

IF COL_LENGTH('dbo.Roadmaps', 'description') IS NOT NULL
	ALTER TABLE [dbo].[Roadmaps] ALTER COLUMN [description] NVARCHAR(2000) NULL;

IF COL_LENGTH('dbo.AcademicContents', 'title') IS NOT NULL
	ALTER TABLE [dbo].[AcademicContents] ALTER COLUMN [title] NVARCHAR(255) NOT NULL;

IF COL_LENGTH('dbo.AcademicContents', 'description') IS NOT NULL
	ALTER TABLE [dbo].[AcademicContents] ALTER COLUMN [description] NVARCHAR(2000) NULL;

IF COL_LENGTH('dbo.Lessons', 'lesson_type') IS NOT NULL
	ALTER TABLE [dbo].[Lessons] ALTER COLUMN [lesson_type] NVARCHAR(50) NULL;

IF COL_LENGTH('dbo.Lessons', 'resource_url') IS NOT NULL
	ALTER TABLE [dbo].[Lessons] ALTER COLUMN [resource_url] NVARCHAR(500) NULL;

-- Lưu ý: các bản ghi đã bị mất dấu thành '?' trước đây không thể khôi phục tự động.
-- Cần cập nhật lại dữ liệu bằng Unicode literal nếu chạy SQL thủ công:
-- UPDATE dbo.Roadmaps SET title = N'Lộ trình IELTS từ 0 - 5.5' WHERE id = 7;

-- Verify: Kiểm tra các field mới
-- SELECT * FROM person WHERE avatar IS NOT NULL;
-- SELECT * FROM Courses WHERE image IS NOT NULL;
-- SELECT * FROM AcademicContents WHERE image_link IS NOT NULL;

