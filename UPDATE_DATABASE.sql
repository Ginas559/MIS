-- Script SQL để cập nhật database cho Upload Ảnh
-- Thực thi các câu lệnh này để thêm các field mới vào database

-- Thêm field avatar vào bảng person
ALTER TABLE [dbo].[person]
ADD [avatar] VARCHAR(500) NULL;

-- Thêm field image vào bảng Courses
ALTER TABLE [dbo].[Courses]
ADD [image] VARCHAR(500) NULL;

-- Thêm field image_link vào bảng AcademicContents
ALTER TABLE [dbo].[AcademicContents]
ADD [image_link] VARCHAR(500) NULL;

-- Verify: Kiểm tra các field mới
-- SELECT * FROM person WHERE avatar IS NOT NULL;
-- SELECT * FROM Courses WHERE image IS NOT NULL;
-- SELECT * FROM AcademicContents WHERE image_link IS NOT NULL;

