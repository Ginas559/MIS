# ✅ Developer Checklist - Upload Ảnh Cloudinary

## 📋 Pre-Deployment Checklist

### 1. Configuration Setup
- [ ] Tạo file `.env` ở thư mục gốc dự án
  ```
  CLOUDINARY_CLOUD_NAME=your_value
  CLOUDINARY_API_KEY=your_value
  CLOUDINARY_API_SECRET=your_value
  ```
- [ ] Kiểm tra credentials từ Cloudinary Dashboard
- [ ] Không commit `.env` vào git (thêm vào .gitignore)

### 2. Database Setup
- [ ] Execute script `UPDATE_DATABASE.sql`
  - [ ] ALTER TABLE person ADD avatar
  - [ ] ALTER TABLE Courses ADD image
  - [ ] ALTER TABLE AcademicContents ADD image_link
- [ ] Verify columns được tạo trong SQL Server Management Studio
- [ ] Backup database trước update

### 3. Code Review
- [ ] Kiểm tra tất cả 7 Java files được tạo
  - [ ] CloudinaryConfig.java
  - [ ] IImageUploadService.java
  - [ ] CloudinaryImageUploadAdapter.java
  - [ ] ImageUploadServiceFactory.java
  - [ ] ImageProcessingDecorator.java
  - [ ] AvatarUploadDecorator.java
  - [ ] UploadUtils.java
- [ ] Kiểm tra 8 files cập nhật:
  - [ ] Person.java
  - [ ] Course.java
  - [ ] AcademicContentEntity.java
  - [ ] ProfileController.java
  - [ ] CourseController.java
  - [ ] profile.jsp
  - [ ] course-form.jsp
- [ ] Code review cho imports và annotations
- [ ] Check no syntax errors

### 4. Build & Compile
- [ ] Run `mvn clean install`
- [ ] Check build success
- [ ] No compilation errors
- [ ] No dependency conflicts
- [ ] WAR file generated successfully

### 5. Deployment
- [ ] Stop Tomcat server
- [ ] Backup old WAR file
- [ ] Copy new WAR file to `$TOMCAT_HOME/webapps/`
- [ ] Start Tomcat server
- [ ] Check Tomcat logs for errors
- [ ] Verify application starts without exception

### 6. Functional Testing
#### Profile Avatar Upload
- [ ] Navigate to http://localhost:8080/MISEnglish/profile
- [ ] Form loads correctly
- [ ] Select valid image file (JPG/PNG < 5MB)
- [ ] Update profile
- [ ] Success message appears
- [ ] Avatar displays with 200x200 size
- [ ] Database has URL in person.avatar field
- [ ] Cloudinary dashboard shows uploaded image

#### Course Image Upload
- [ ] Navigate to http://localhost:8080/MISEnglish/admin/course/add
- [ ] Form loads correctly
- [ ] Select valid image file
- [ ] Create/Update course
- [ ] Success message appears
- [ ] Image displays correctly
- [ ] Database has URL in Courses.image field
- [ ] Cloudinary dashboard shows uploaded image

#### Error Handling
- [ ] Upload non-image file → Error message appears
- [ ] Upload file > 5MB → Error message appears
- [ ] Skip file upload → Profile/Course updates successfully
- [ ] Network error → Graceful error handling
- [ ] Form retains data on error

### 7. Database Verification
```sql
-- Run these queries to verify:
SELECT id, full_name, avatar FROM person WHERE avatar IS NOT NULL;
SELECT courseID, courseName, image FROM Courses WHERE image IS NOT NULL;
SELECT id, title, image_link FROM AcademicContents WHERE image_link IS NOT NULL;
```
- [ ] At least one record with avatar
- [ ] At least one record with image
- [ ] URL format is correct (starts with https://res.cloudinary.com)

### 8. Performance Check
- [ ] Avatar upload completes within 5 seconds
- [ ] Image displays without lag
- [ ] Cloudinary CDN loads quickly
- [ ] No slow DB queries
- [ ] No memory leaks

### 9. Browser Compatibility
- [ ] Test in Chrome
  - [ ] File input works
  - [ ] Form submission works
  - [ ] Image displays
- [ ] Test in Firefox
  - [ ] File input works
  - [ ] Form submission works
  - [ ] Image displays
- [ ] Test in Edge
  - [ ] File input works
  - [ ] Form submission works
  - [ ] Image displays

### 10. Security Check
- [ ] .env file is NOT in version control
- [ ] Credentials are NOT logged
- [ ] File size validation works (5MB limit)
- [ ] File type validation works (image/* only)
- [ ] No SQL injection vulnerabilities
- [ ] No path traversal vulnerabilities
- [ ] HTTPS recommended for production

### 11. Documentation Review
- [ ] Đọc `QUICK_START.md`
- [ ] Đọc `CLOUDINARY_SETUP_README.md`
- [ ] Biết cách thêm upload vào trang mới (UPLOAD_GUIDE.md)
- [ ] Hiểu design pattern (ARCHITECTURE_DETAILED.md)
- [ ] Biết cách test (TEST_CASES.md)

### 12. Backup & Recovery
- [ ] Database backup lúc trước update
- [ ] Old WAR file backup
- [ ] Rollback plan sẵn sàng
- [ ] Know how to restore database

---

## 🧪 Testing Checklist

### Happy Path (Thành công)
- [ ] Upload valid avatar → success
- [ ] Avatar displays with correct size (200x200)
- [ ] Upload course image → success
- [ ] Image displays correctly
- [ ] Refresh page → avatar/image still there
- [ ] Update avatar → old image replaced

### Error Cases (Lỗi)
- [ ] Non-image file → error message
- [ ] Too large file → error message
- [ ] No file selected → skipped, profile updated
- [ ] Network error → graceful handling
- [ ] Invalid form data → validation message
- [ ] Cloudinary down → appropriate error

### Edge Cases
- [ ] Very large file (4.9 MB) → success
- [ ] Rapid sequential uploads → last wins
- [ ] Multiple concurrent users → no conflicts
- [ ] Special characters in filename → handled
- [ ] Very long filename → truncated
- [ ] Session timeout → re-login required

---

## 🔍 Code Review Checklist

### CloudinaryConfig.java
- [ ] Singleton pattern used correctly
- [ ] .env loading with proper error handling
- [ ] Null checks on credentials
- [ ] Clear error message if config missing

### IImageUploadService.java
- [ ] Interface methods are clear
- [ ] IOException declared in throws
- [ ] Supports both uploadImage variants
- [ ] deleteImage method included

### CloudinaryImageUploadAdapter.java
- [ ] Implements IImageUploadService correctly
- [ ] File type validation (image/*)
- [ ] File size validation (≤ 5MB)
- [ ] Proper exception handling
- [ ] Uses try-with-resources for streams
- [ ] Returns secure_url from Cloudinary
- [ ] Handles both public_id and auto-generated

### ImageUploadServiceFactory.java
- [ ] Factory method pattern correct
- [ ] Supports "CLOUDINARY" type
- [ ] Extensible for future providers
- [ ] Error handling for unknown type
- [ ] Static methods where appropriate

### ImageProcessingDecorator.java
- [ ] Decorator pattern correct
- [ ] Config class properly designed
- [ ] Wraps service correctly
- [ ] Delegates methods properly
- [ ] Getters/setters for config

### AvatarUploadDecorator.java
- [ ] Extends ImageProcessingDecorator
- [ ] Resize config set to 200x200
- [ ] Uses face-aware gravity
- [ ] Proper error handling
- [ ] Transformation options correct

### UploadUtils.java
- [ ] Facade pattern correct
- [ ] Three upload methods: avatar, content, generic
- [ ] Error handling and wrapping
- [ ] Static methods for utility use
- [ ] Clear method names

### ProfileController.java
- [ ] @MultipartConfig annotation present
- [ ] Import Part and UploadUtils
- [ ] GET shows profile
- [ ] POST handles upload
- [ ] Error handling separate logic
- [ ] Redirect on success
- [ ] Session validation

### CourseController.java
- [ ] @MultipartConfig annotation present
- [ ] saveCourse method handles image
- [ ] Try-catch for upload errors
- [ ] Graceful fallback (save without image)
- [ ] Proper error messages

### profile.jsp
- [ ] enctype="multipart/form-data"
- [ ] File input for avatar
- [ ] Preview image displays
- [ ] CSS styling looks good
- [ ] Error message display
- [ ] Success message display

### course-form.jsp
- [ ] enctype="multipart/form-data"
- [ ] File input for image
- [ ] Preview image displays
- [ ] CSS styling looks good
- [ ] Responsive design

### Entity Updates
- [ ] Person: avatar field, getter/setter, updateProfile
- [ ] Course: image field, getter/setter
- [ ] AcademicContentEntity: imageLink field, getter/setter

---

## 📊 Test Results Log

```
Date: ________________
Tester: ________________
Build Version: ________________

TEST CATEGORY          PASSED    FAILED    NOTES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Configuration           [ ]       [ ]      ________________
Database Setup          [ ]       [ ]      ________________
Build & Compile         [ ]       [ ]      ________________
Deployment              [ ]       [ ]      ________________
Avatar Upload           [ ]       [ ]      ________________
Course Image Upload     [ ]       [ ]      ________________
Error Handling          [ ]       [ ]      ________________
Performance             [ ]       [ ]      ________________
Browser Compat          [ ]       [ ]      ________________
Security                [ ]       [ ]      ________________
Code Quality            [ ]       [ ]      ________________
Documentation           [ ]       [ ]      ________________

OVERALL STATUS:  [ ] PASS    [ ] FAIL

Issues Found:
1. _______________________________________
2. _______________________________________
3. _______________________________________

Sign-off: ____________________  Date: ____________________
```

---

## 📞 Troubleshooting Quick Links

| Problem | Solution | File |
|---------|----------|------|
| Cloudinary config error | Check .env file | CLOUDINARY_SETUP_README.md |
| Upload fails | Check file type/size | CLOUDINARY_SETUP_README.md |
| Image doesn't display | Verify URL in DB | TEST_CASES.md |
| Design pattern questions | Read architecture | ARCHITECTURE_DETAILED.md |
| How to add upload to new page | Follow guide | UPLOAD_GUIDE.md |
| Test cases reference | See manual tests | TEST_CASES.md |

---

## 🎯 Sign-Off

- [ ] All checklist items completed
- [ ] All tests passed
- [ ] Documentation reviewed
- [ ] Ready for production deployment

**Approved by**: ____________________  
**Date**: ____________________  
**Version**: 1.0  

---

**Status**: ✅ Ready for Production Deployment

