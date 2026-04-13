# Quick Start - Upload Ảnh với Cloudinary

## ⚡ 5 bước nhanh để chạy được

### Bước 1: Cấu hình Cloudinary
```bash
# Tạo file .env ở thư mục gốc dự án
# D:\cc_ltw\workspace\DoAN\MIS\.env

CLOUDINARY_CLOUD_NAME=dqwqbfhty  # Thay bằng cloud name của bạn
CLOUDINARY_API_KEY=123456789      # Thay bằng API key
CLOUDINARY_API_SECRET=abcdef123   # Thay bằng API secret
```

### Bước 2: Cập nhật Database
```sql
-- Chạy câu lệnh này trong SQL Server Management Studio
ALTER TABLE [dbo].[person] ADD [avatar] VARCHAR(500) NULL;
ALTER TABLE [dbo].[Courses] ADD [image] VARCHAR(500) NULL;
```

### Bước 3: Build Project
```bash
cd D:\cc_ltw\workspace\DoAN\MIS
mvn clean install
```

### Bước 4: Deploy
- Copy `target/MISEnglish.war` vào thư mục Tomcat `webapps`
- Restart Tomcat

### Bước 5: Test
- Truy cập: http://localhost:8080/MISEnglish/profile
- Upload ảnh avatar
- Kiểm tra ảnh được hiển thị

---

## 📌 Các tính năng sẵn dùng

### 1. Profile Avatar
```url
GET  http://localhost:8080/MISEnglish/profile
POST http://localhost:8080/MISEnglish/profile (với file upload)
```
**Form field**: `avatar`

### 2. Course Image
```url
GET  http://localhost:8080/MISEnglish/admin/course/add
POST http://localhost:8080/MISEnglish/admin/course/add (với file upload)
GET  http://localhost:8080/MISEnglish/admin/course/update
POST http://localhost:8080/MISEnglish/admin/course/update (với file upload)
```
**Form field**: `courseImage`

---

## 🔧 Nếu gặp lỗi

### Lỗi: "Cloudinary configuration not found"
```
✅ Kiểm tra: File .env tồn tại ở thư mục gốc
✅ Kiểm tra: Biến CLOUDINARY_CLOUD_NAME đã được set
✅ Kiểm tra: Không có space thừa trong .env
```

### Lỗi: "Only image files are supported"
```
✅ Upload file ảnh (JPG, PNG, GIF)
✅ Không upload file document, video, etc.
✅ Dung lượng < 5MB
```

### Lỗi: "File size exceeds 5MB"
```
✅ Nén/resize ảnh trước khi upload
✅ Hoặc sửa MAX_FILE_SIZE trong @MultipartConfig
```

### Ảnh không hiển thị sau upload
```
✅ Kiểm tra URL được lưu vào database
✅ Kiểm tra URL có công khai (secure_url)
✅ Kiểm tra browser console có error gì
```

---

## 📂 Tệp quan trọng

| Tệp | Mục đích |
|-----|---------|
| `.env` | Credentials Cloudinary |
| `ProfileController.java` | Upload avatar |
| `CourseController.java` | Upload course ảnh |
| `IImageUploadService.java` | Interface adapter |
| `UploadUtils.java` | Helper methods |
| `profile.jsp` | Avatar form |
| `course-form.jsp` | Course form |

---

## 🎓 Design Patterns

Dự án sử dụng 3 design pattern:

### Adapter Pattern ✅
Chuyển Cloudinary SDK thành interface chung `IImageUploadService`

### Factory Method ✅
`ImageUploadServiceFactory` tạo service upload

### Decorator ✅
`AvatarUploadDecorator` thêm tính năng resize avatar

---

## 📖 Để tìm hiểu thêm

- Hướng dẫn chi tiết: `CLOUDINARY_SETUP_README.md`
- Thêm upload vào trang khác: `UPLOAD_GUIDE.md`
- Danh sách thay đổi: `IMPLEMENTATION_SUMMARY.md`

---

## ✨ Tính năng chính

| Tính năng | Status |
|----------|--------|
| Upload avatar | ✅ Done |
| Upload course ảnh | ✅ Done |
| Avatar resize 200x200 | ✅ Done |
| Image validation | ✅ Done |
| Error handling | ✅ Done |
| File preview | ✅ Done |
| AWS S3 support | 🔄 Sẵn sàng mở rộng |
| Watermark | 🔄 Sẵn sàng mở rộng |

---

## 🚀 Sắp tới

Có thể thêm vào các bước tiếp theo:
- [ ] Upload ảnh cho học liệu (modules, lessons)
- [ ] Batch upload nhiều ảnh
- [ ] Watermark logo CoolEnglish
- [ ] Image gallery
- [ ] CDN caching

---

**Hỗ trợ**: Xem `CLOUDINARY_SETUP_README.md` hoặc `UPLOAD_GUIDE.md`

