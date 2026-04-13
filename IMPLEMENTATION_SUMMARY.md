# Tóm tắt Triển khai Upload Ảnh với Cloudinary

## 📋 Danh sách các tệp đã thêm/cập nhật

### 🆕 Tệp mới được tạo

#### Service Layer (Cloudinary Integration)
1. **`src/main/java/vn/iotstar/coolenglish/config/CloudinaryConfig.java`**
   - Cấu hình Cloudinary từ file `.env`
   - Singleton instance

2. **`src/main/java/vn/iotstar/coolenglish/service/IImageUploadService.java`**
   - Interface (Target) cho Adapter Pattern
   - Định nghĩa: `uploadImage()`, `deleteImage()`

3. **`src/main/java/vn/iotstar/coolenglish/service/impl/CloudinaryImageUploadAdapter.java`**
   - Adapter Pattern: Bọc Cloudinary SDK
   - Xử lý upload ảnh lên Cloudinary
   - Validation file (type, size)

4. **`src/main/java/vn/iotstar/coolenglish/service/impl/ImageUploadServiceFactory.java`**
   - Factory Method Pattern
   - Tạo service upload (hỗ trợ mở rộng)

5. **`src/main/java/vn/iotstar/coolenglish/service/impl/ImageProcessingDecorator.java`**
   - Decorator Pattern: Base class
   - Cấu hình xử lý ảnh (resize, watermark, compression)

6. **`src/main/java/vn/iotstar/coolenglish/service/impl/AvatarUploadDecorator.java`**
   - Decorator cho Avatar
   - Tự động resize 200x200 pixel

7. **`src/main/java/vn/iotstar/coolenglish/util/UploadUtils.java`**
   - Utility class cho Controller
   - Các phương thức tiện dụng: `uploadAvatar()`, `uploadAcademicContentImage()`, `uploadImage()`, `deleteImage()`

#### Configuration
8. **`.env`**
   - Lưu credentials Cloudinary
   - CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET

#### Documentation
9. **`CLOUDINARY_SETUP_README.md`**
   - Hướng dẫn cấu hình Cloudinary
   - Cấu trúc code chi tiết
   - Troubleshooting

10. **`UPLOAD_GUIDE.md`**
    - Hướng dẫn thêm upload ảnh vào các trang mới
    - Ví dụ cụ thể

11. **`UPDATE_DATABASE.sql`**
    - Script SQL cập nhật database
    - Thêm field avatar, image, image_link

### 📝 Tệp đã cập nhật

#### Entity Classes
1. **`src/main/java/vn/iotstar/coolenglish/entity/Person.java`**
   - ✅ Thêm field: `avatar` (String, 500 chars)
   - ✅ Thêm getter/setter: `getAvatar()`, `setAvatar()`
   - ✅ Overload method: `updateProfile(..., String avatar)`

2. **`src/main/java/vn/iotstar/coolenglish/entity/Course.java`**
   - ✅ Thêm field: `image` (String, 500 chars)
   - ✅ Thêm getter/setter: `getImage()`, `setImage()`

3. **`src/main/java/vn/iotstar/coolenglish/entity/AcademicContentEntity.java`**
   - ✅ Thêm field: `imageLink` (String, 500 chars)
   - ✅ Thêm getter/setter: `getImageLink()`, `setImageLink()`

#### Controller Classes
4. **`src/main/java/vn/iotstar/coolenglish/web/ProfileController.java`**
   - ✅ Thêm @MultipartConfig annotation
   - ✅ Import: Part, UploadUtils
   - ✅ Xử lý upload avatar trong doPost()
   - ✅ Hiển thị preview avatar trong form
   - ✅ Error handling cho upload

5. **`src/main/java/vn/iotstar/coolenglish/web/CourseController.java`**
   - ✅ Thêm @MultipartConfig annotation
   - ✅ Import: Part, UploadUtils
   - ✅ Xử lý upload ảnh khóa học trong saveCourse()
   - ✅ Error handling (graceful fallback)

#### View Files
6. **`src/main/webapp/WEB-INF/views/profile.jsp`**
   - ✅ Thêm form file input cho avatar
   - ✅ Preview ảnh avatar cũ
   - ✅ Thêm CSS styling
   - ✅ Thêm enctype="multipart/form-data"
   - ✅ Hiển thị error message

7. **`src/main/webapp/WEB-INF/views/admin/course-form.jsp`**
   - ✅ Thêm form file input cho ảnh khóa học
   - ✅ Preview ảnh cũ
   - ✅ Thêm CSS styling
   - ✅ Thêm enctype="multipart/form-data"

## 🏗️ Design Patterns Áp dụng

### 1. Adapter Pattern (IImageUploadService)
```
Cloudinary SDK ──→ CloudinaryImageUploadAdapter ──→ IImageUploadService
(Adaptee)            (Adapter)                      (Target Interface)
```
- **Lợi ích**: Dễ dàng thay đổi storage service (AWS S3, Google Cloud)
- **File**: `IImageUploadService.java`, `CloudinaryImageUploadAdapter.java`

### 2. Factory Method Pattern (ImageUploadServiceFactory)
```
Controller ──→ ImageUploadServiceFactory.createService() ──→ IImageUploadService
```
- **Lợi ích**: Tập trung cấu hình, dễ quản lý lifecycle
- **File**: `ImageUploadServiceFactory.java`

### 3. Decorator Pattern (ImageProcessingDecorator, AvatarUploadDecorator)
```
BaseService ──→ ImageProcessingDecorator ──→ AvatarUploadDecorator
(Component)      (Decorator Base)              (Concrete Decorator)
```
- **Lợi ích**: Thêm tính năng (resize, watermark) linh hoạt, không sửa code chính
- **File**: `ImageProcessingDecorator.java`, `AvatarUploadDecorator.java`

## 🚀 Tính năng chính

### Profile Avatar
- **URL**: `http://localhost:8080/MISEnglish/profile`
- **Tính năng**:
  - Upload ảnh đại diện
  - Tự động resize 200x200 pixel
  - Preview ảnh trước/sau upload
  - Validation (JPG, PNG, max 5MB)

### Course Management
- **URL**: `http://localhost:8080/MISEnglish/admin/course/add`, `/update`
- **Tính năng**:
  - Upload ảnh khóa học
  - Preview ảnh
  - Validation

### Học liệu (Ready for future use)
- **Field**: `AcademicContentEntity.imageLink`
- **Sử dụng**: `UploadUtils.uploadAcademicContentImage()`

## 📋 Hướng dẫn cài đặt

### 1. Cấu hình Cloudinary
```bash
# Chỉnh sửa file .env
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### 2. Cập nhật Database
```sql
-- Thực thi UPDATE_DATABASE.sql
ALTER TABLE person ADD avatar VARCHAR(500) NULL;
ALTER TABLE Courses ADD image VARCHAR(500) NULL;
ALTER TABLE AcademicContents ADD image_link VARCHAR(500) NULL;
```

### 3. Build Project
```bash
mvn clean package
```

### 4. Deploy WAR file
- Copy `target/MISEnglish.war` vào Tomcat

## 🔄 Workflow Upload Ảnh

```
1. User chọn file (profile.jsp / course-form.jsp)
   ↓
2. Form submit (enctype="multipart/form-data")
   ↓
3. Controller nhận Part (ProfileController / CourseController)
   ↓
4. UploadUtils.uploadAvatar() / uploadImage()
   ↓
5. CloudinaryImageUploadAdapter (bọc SDK)
   ↓
6. Cloudinary API xử lý
   ↓
7. Return URL (secure_url)
   ↓
8. Lưu URL vào Entity
   ↓
9. Persist vào Database
   ↓
10. Redirect / Display
```

## ✅ Validation

### Client Side
- `accept="image/*"` trong file input
- Max size: 5MB (browser native)

### Server Side
- Content-Type check (image/*)
- File size check (≤ 5MB)
- Exception handling

## 🎯 Công dụng UploadUtils

| Method | Dùng cho | Đặc điểm |
|--------|----------|---------|
| `uploadAvatar()` | Avatar user | Resize 200x200 |
| `uploadAcademicContentImage()` | Học liệu | Có content ID |
| `uploadImage()` | Khác | Generic |
| `deleteImage()` | Xóa | Cần public ID |

## 🚢 Deployment Checklist

- [ ] .env file được tạo với credentials Cloudinary
- [ ] Database schema được update (ALTER TABLE)
- [ ] Cloudinary account được tạo
- [ ] pom.xml có dependency `cloudinary-http44`
- [ ] Project được build thành công
- [ ] WAR file được deploy
- [ ] Test upload avatar tại `/profile`
- [ ] Test upload course ảnh tại `/admin/course/add`

## 📚 Tài liệu tham khảo

- **Cloudinary Setup**: `CLOUDINARY_SETUP_README.md`
- **Implementation Guide**: `UPLOAD_GUIDE.md`
- **Database Updates**: `UPDATE_DATABASE.sql`

## 💡 Mở rộng trong tương lai

1. **Thêm provider khác**:
   - Tạo adapter mới (AWS S3, Google Cloud)
   - Thêm vào Factory

2. **Tính năng advanced**:
   - Watermark logo
   - Batch upload
   - Image gallery

3. **Performance**:
   - CDN caching
   - Lazy loading
   - Thumbnail generation

---
**Last Updated**: April 2026
**Version**: 1.0
**Status**: ✅ Ready for Production

