# Test Cases - Upload Ảnh với Cloudinary

## 🧪 Manual Testing Guide

### Test Environment
- **Server**: Tomcat 10.1.x
- **Database**: SQL Server
- **Browser**: Chrome, Firefox, Edge (mới nhất)
- **Cloudinary**: Account được setup ở .env

---

## ✅ Test Case 1: Profile Avatar Upload

### Preconditions
- User đã đăng nhập
- Cloudinary credentials đã cấu hình
- Database đã update schema (avatar field)

### Test Steps

#### 1.1 Truy cập trang Profile
```
1. Navigate to: http://localhost:8080/MISEnglish/profile
2. Expected: Hiển thị form profile với các field: fullName, email, phone, gender
3. Expected: Có section "Anh dai dien" với file input
4. Expected: Nếu user đã có avatar, hiển thị preview ảnh
✅ PASS / ❌ FAIL
```

#### 1.2 Upload Avatar Hợp lệ
```
1. Click "Chon file" ở section Anh dai dien
2. Select valid image file (JPG, PNG, < 5MB)
3. Modify fullName field (required)
4. Click "Cap nhat" button
5. Expected: Success message "Da cap nhat ho so thanh cong"
6. Expected: Avatar ảnh hiển thị ở profile
7. Expected: Database person.avatar field có URL từ Cloudinary
✅ PASS / ❌ FAIL

Details:
- Avatar URL format: https://res.cloudinary.com/{cloud_name}/image/upload/...
- Size: Tự động resize về 200x200 pixel
- File stored: Cloudinary public folder
```

#### 1.3 Update Avatar (Replace Old)
```
1. Truy cập /profile lại
2. Expected: Avatar cũ được hiển thị
3. Select ảnh mới khác
4. Click "Cap nhat"
5. Expected: Avatar cũ bị thay thế
6. Expected: URL trong database thay đổi
7. Expected: Cloudinary image được overwrite (cùng public_id)
✅ PASS / ❌ FAIL
```

#### 1.4 Upload Invalid File - Type
```
1. Select non-image file (PDF, DOC, MP4, etc.)
2. Click "Cap nhat"
3. Expected: Error message "Chỉ hỗ trợ upload ảnh"
4. Expected: Form không submit
5. Expected: User vẫn ở trang profile
6. Expected: Có thể thử upload khác
✅ PASS / ❌ FAIL
```

#### 1.5 Upload Invalid File - Size
```
1. Select image file > 5MB (use image > 5242880 bytes)
2. Click "Cap nhat"
3. Expected: Error message "Dung lượng ảnh không vượt quá 5MB"
4. Expected: Form không submit
5. Expected: User vẫn ở trang profile
✅ PASS / ❌ FAIL
```

#### 1.6 Skip Avatar Upload (Optional)
```
1. Truy cập /profile
2. Chỉ modify fullName, phone, gender
3. Skip avatar file (leave empty)
4. Click "Cap nhat"
5. Expected: Profile updated thành công
6. Expected: Avatar field giữ nguyên (không thay đổi)
7. Expected: Redirect to /profile?msg=updated
✅ PASS / ❌ FAIL
```

#### 1.7 Session Validation
```
1. Upload avatar
2. Expected: Session user được update
3. Close browser
4. Open browser lại, login vào lại
5. Expected: Avatar vẫn hiển thị (persist in DB)
✅ PASS / ❌ FAIL
```

---

## ✅ Test Case 2: Course Image Upload

### Preconditions
- User là ADMIN hoặc STAFF
- Cloudinary credentials đã cấu hình
- Database đã update schema (image field)

### Test Steps

#### 2.1 Truy cập Course Add Form
```
1. Navigate to: http://localhost:8080/MISEnglish/admin/course/add
2. Expected: Hiển thị form course với các field: courseID, courseName, description, level, duration, fee, status
3. Expected: Có section "Anh khoa hoc" với file input
✅ PASS / ❌ FAIL
```

#### 2.2 Add Course with Image
```
1. Fill all required fields:
   - courseID: "TOEIC_2024"
   - courseName: "TOEIC Preparation"
   - description: "Test description"
   - level: "Intermediate"
   - duration: 12
   - fee: 200.00
   - status: ACTIVE
2. Select course image (JPG, PNG, < 5MB)
3. Click "Them moi"
4. Expected: Success, redirect to course list
5. Expected: Course ảnh được upload lên Cloudinary
6. Expected: Database Courses.image field có URL
✅ PASS / ❌ FAIL
```

#### 2.3 Update Course Image
```
1. Navigate to: /admin/course/update?id=TOEIC_2024
2. Expected: Form pre-filled với course data
3. Expected: Existing image được preview
4. Select new image
5. Click "Cap nhat"
6. Expected: Course image được thay thế
7. Expected: URL trong database thay đổi
8. Expected: Cloudinary image overwritten
✅ PASS / ❌ FAIL
```

#### 2.4 Add Course Without Image
```
1. Fill course form (all required fields)
2. Skip image file (leave empty)
3. Click "Them moi"
4. Expected: Course được tạo thành công
5. Expected: Course.image field remain NULL
6. Expected: Có thể update image sau
✅ PASS / ❌ FAIL
```

#### 2.5 Delete Course with Image
```
1. Create course with image
2. Navigate to course list
3. Delete the course
4. Expected: Course deleted from DB
5. Expected: Course ảnh vẫn tồn tại ở Cloudinary (orphaned)
   (Note: Implement deleteImage() nếu muốn cascade delete)
✅ PASS / ❌ FAIL
```

#### 2.6 Course List Display
```
1. Navigate to: /admin/course hoặc /course
2. Expected: List course được hiển thị
3. Expected: Nếu course có image, preview được hiển thị
4. Expected: Image load thành công từ Cloudinary CDN
✅ PASS / ❌ FAIL
```

---

## ✅ Test Case 3: Error Handling

### 3.1 Cloudinary Config Error
```
1. Remove/corrupt .env file
2. Restart application
3. Try to upload avatar
4. Expected: Error "Cloudinary configuration not found"
5. Expected: Graceful error handling, không crash
✅ PASS / ❌ FAIL
```

### 3.2 Network Error (Cloudinary Down)
```
1. Block Cloudinary API (firewall/network simulation)
2. Try to upload avatar
3. Expected: Error "Lỗi upload ảnh: ..."
4. Expected: Form shows error message
5. Expected: Can retry after network restored
✅ PASS / ❌ FAIL
```

### 3.3 File Read Error
```
1. Upload file, then delete file từ file system ngay lập tức
2. (Simulate race condition)
3. Expected: Graceful IOException handling
4. Expected: User gets error message
✅ PASS / ❌ FAIL
```

### 3.4 Malformed Multipart Form
```
1. Send POST request without proper multipart/form-data
2. Expected: Servlet framework handles, không crash
3. Expected: User gets appropriate error or form resubmit
✅ PASS / ❌ FAIL
```

---

## ✅ Test Case 4: Performance & Edge Cases

### 4.1 Large File (Just Under 5MB)
```
1. Create image file 4.9 MB
2. Upload to avatar
3. Expected: Success upload
4. Expected: Cloudinary processes without timeout
✅ PASS / ❌ FAIL
```

### 4.2 Multiple Uploads (Concurrency)
```
1. 2-3 users upload avatar simultaneously
2. Expected: All uploads success
3. Expected: Each user's avatar correct
4. Expected: No file corruption
5. Expected: No race condition
✅ PASS / ❌ FAIL
```

### 4.3 Rapid Sequential Uploads
```
1. User uploads avatar 5 times rapidly (fast clicks)
2. Expected: All uploads processed correctly
3. Expected: Final avatar is the last uploaded
4. Expected: No partial/corrupted data in DB
✅ PASS / ❌ FAIL
```

### 4.4 Image File Formats
```
1. Test JPG upload
   Expected: ✅ Success
2. Test PNG upload
   Expected: ✅ Success
3. Test GIF upload
   Expected: ✅ Success (image/* includes GIF)
4. Test WebP upload
   Expected: ✅ Success (modern browsers)
✅ PASS / ❌ FAIL
```

### 4.5 Database Persistence
```
1. Upload avatar
2. Verify URL saved in person.avatar field
3. Restart application
4. Re-login user
5. Navigate to /profile
6. Expected: Avatar still visible (persisted)
7. Expected: URL unchanged
✅ PASS / ❌ FAIL
```

---

## ✅ Test Case 5: Browser Compatibility

### 5.1 File Input Validation
```
Test in: Chrome, Firefox, Edge, Safari
1. Click file input
2. Expected: Accept only image/* files
3. Expected: Accept button shows only images
✅ Chrome / ❌ Chrome
✅ Firefox / ❌ Firefox
✅ Edge / ❌ Edge
✅ Safari / ❌ Safari
```

### 5.2 Form Submission Encoding
```
Test in: Chrome, Firefox, Edge
1. Select file
2. Submit form
3. Expected: multipart/form-data encoding correct
4. Expected: Part received in servlet
5. Expected: File content readable
✅ Chrome / ❌ Chrome
✅ Firefox / ❌ Firefox
✅ Edge / ❌ Edge
```

### 5.3 Image Preview Display
```
Test in: Chrome, Firefox, Edge
1. Upload avatar
2. Return to /profile
3. Expected: <img> tag displays image
4. Expected: No 404 errors
5. Expected: CDN URL accessible
✅ Chrome / ❌ Chrome
✅ Firefox / ❌ Firefox
✅ Edge / ❌ Edge
```

---

## 🔍 Debugging Checklist

### Check Upload Succeeded
```
Browser DevTools:
1. Open Developer Tools (F12)
2. Network tab
3. Find POST request to /profile
4. Response status: 200 OK
5. Check form redirects
6. Final GET /profile?msg=updated

Database:
SELECT avatar FROM person WHERE id = ?;
Expected: URL like https://res.cloudinary.com/...
```

### Check Image Displays
```
Browser:
1. Right-click avatar → "Open image in new tab"
2. Expected: Image loads from https://res.cloudinary.com/
3. Expected: 200 response code
4. Expected: Image dimensions expected (200x200 for avatar)

Console:
1. Check for CORS errors
2. Check for 404 errors
3. Check for network errors
```

### Check Cloudinary API Working
```
Java Code Test:
1. Create test in CloudinaryConfigTest
2. Verify getInstance() returns non-null
3. Verify cloudinary instance can call uploader()

REST API Test:
curl -X GET "https://api.cloudinary.com/v1_1/{cloud_name}/resources/image"
  -u {api_key}:{api_secret}

Dashboard:
1. Login Cloudinary Dashboard
2. Media Library tab
3. Check uploaded images exist
4. Verify public_id pattern: user_*, content_*
```

### Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| "Cloudinary not found" | .env missing | Create .env with credentials |
| File upload hangs | Network timeout | Check internet connection |
| "Only image files" error | Wrong file type | Select JPG/PNG |
| Image 404 | URL not saved in DB | Check person.avatar field |
| Image not resize | Decorator not applied | Check AvatarUploadDecorator |
| Session lost | HTTP/HTTPS mismatch | Ensure same protocol |

---

## 📊 Test Report Template

```
=== UPLOAD IMAGE TEST REPORT ===

Date: ____________________
Tester: ____________________
Build Version: ____________________

Test Case        Status    Notes
────────────────────────────────────────────
1.1 Access Profile     ✅/❌   ____
1.2 Upload Valid       ✅/❌   ____
1.3 Update Avatar      ✅/❌   ____
1.4 Invalid Type       ✅/❌   ____
1.5 Invalid Size       ✅/❌   ____
1.6 Skip Upload        ✅/❌   ____
1.7 Session Persist    ✅/❌   ____
2.1 Course Form        ✅/❌   ____
2.2 Add with Image     ✅/❌   ____
2.3 Update Image       ✅/❌   ____
2.4 Without Image      ✅/❌   ____
3.1 Config Error       ✅/❌   ____
3.2 Network Error      ✅/❌   ____
4.1 Large File         ✅/❌   ____
4.2 Concurrent Upload  ✅/❌   ____
5.1 Browser Compat     ✅/❌   ____

Overall Status: ✅ PASS / ❌ FAIL

Issues Found:
1. _____________________________________
2. _____________________________________

Recommendations:
1. _____________________________________
2. _____________________________________
```

---

## 🚀 Performance Test

```
Metrics to measure:
- File upload time (< 5 seconds for 1MB)
- Response time (/profile load with avatar preview)
- Database query time (SELECT person with avatar)
- CDN response time (Image load from Cloudinary)

Tools:
- Browser DevTools Performance tab
- SQL Server Query Analyzer
- Cloudinary Dashboard Analytics
```

---

**Last Updated**: April 2026

