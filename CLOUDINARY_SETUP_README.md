# Hướng dẫn Cloudinary Image Upload

## Tổng quan
Hệ thống upload ảnh của CoolEnglish được xây dựng theo các Design Pattern:
- **Adapter Pattern**: Chuyển đổi Cloudinary SDK thành interface chung `IImageUploadService`
- **Factory Method Pattern**: Quản lý việc tạo và cấu hình các service upload
- **Decorator Pattern**: Xử lý các tính năng như resize, watermark cho ảnh avatar

## Cấu hình Cloudinary

### 1. Tạo tài khoản Cloudinary
- Truy cập: https://cloudinary.com/users/register/free
- Đăng ký tài khoản miễn phí

### 2. Lấy thông tin API
1. Đăng nhập vào Cloudinary Dashboard
2. Tìm phần "Account" hoặc "Settings"
3. Sao chép các thông tin:
   - **Cloud Name**: Tên cloud của bạn
   - **API Key**: Khóa API công khai
   - **API Secret**: Khóa bí mật (giữ kín)

### 3. Cấu hình file .env
Tạo file `.env` ở thư mục gốc của dự án (`D:\cc_ltw\workspace\DoAN\MIS\.env`):

```env
# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

Thay thế giá trị `your_cloud_name`, `your_api_key`, và `your_api_secret` bằng thông tin thực tế của bạn.

## Cấu trúc code

### Service Layer
- **IImageUploadService**: Interface chung định nghĩa các phương thức upload
- **CloudinaryImageUploadAdapter**: Adapter triển khai interface, bọc Cloudinary SDK
- **ImageUploadServiceFactory**: Factory tạo service upload
- **ImageProcessingDecorator**: Decorator base cho xử lý ảnh
- **AvatarUploadDecorator**: Decorator riêng cho upload avatar (resize 200x200)
- **UploadUtils**: Utility class giúp controller gọi upload dễ dàng

### Controller Integration

#### 1. ProfileController (`/profile`)
- Upload avatar ảnh đại diện
- Tự động resize về 200x200 pixel
- Lưu URL vào field `avatar` của Person entity

**Form:**
```html
<form method="post" action="/profile" enctype="multipart/form-data">
    <input type="file" name="avatar" accept="image/*">
    ...
</form>
```

#### 2. CourseController (`/admin/course/add`, `/admin/course/update`)
- Upload ảnh khóa học
- Lưu URL vào field `image` của Course entity

**Form:**
```html
<form method="post" action="/admin/course/add" enctype="multipart/form-data">
    <input type="file" name="courseImage" accept="image/*">
    ...
</form>
```

### Entity Updates

#### Person Entity
- Thêm field: `avatar` (String, max 500 chars)
- Phương thức mới: `updateProfile(fullName, gender, phone, email, avatar)`

#### Course Entity
- Thêm field: `image` (String, max 500 chars)
- Getter/Setter: `getImage()`, `setImage(String)`

#### AcademicContentEntity
- Thêm field: `imageLink` (String, max 500 chars)
- Getter/Setter: `getImageLink()`, `setImageLink(String)`
- Dành để thêm ảnh cho các học liệu trong tương lai

## Sử dụng UploadUtils

### Upload Avatar
```java
String avatarUrl = UploadUtils.uploadAvatar(filePart, userId);
person.setAvatar(avatarUrl);
```

### Upload ảnh học liệu
```java
String imageUrl = UploadUtils.uploadAcademicContentImage(filePart, contentId);
content.setImageLink(imageUrl);
```

### Upload ảnh tổng quát
```java
String imageUrl = UploadUtils.uploadImage(filePart);
```

### Xóa ảnh
```java
boolean deleted = UploadUtils.deleteImage(publicId);
```

## Giới hạn File

- **Định dạng**: JPG, PNG, GIF, WebP
- **Dung lượng tối đa**: 5MB
- **Validation**: Được thực hiện cả ở client (accept="image/*") và server

## Xử lý Lỗi

Tất cả các phương thức upload đều:
1. Kiểm tra file có hợp lệ hay không
2. Throw `IllegalArgumentException` nếu file không hợp lệ
3. Throw `IOException` nếu có lỗi upload

Controller nên catch các exception này và hiển thị message lỗi cho user.

## Upgrade trong tương lai

Để thêm support cho các cloud storage khác (AWS S3, Google Cloud):

1. Tạo adapter mới implement `IImageUploadService`:
   ```java
   public class AWSS3ImageUploadAdapter implements IImageUploadService {
       // ...
   }
   ```

2. Thêm vào Factory:
   ```java
   case "AWS_S3" -> new AWSS3ImageUploadAdapter();
   ```

3. Sử dụng: `ImageUploadServiceFactory.createService("AWS_S3")`

## Troubleshooting

### Lỗi: "Cloudinary configuration not found"
- Kiểm tra file `.env` tồn tại ở đúng thư mục
- Kiểm tra các biến môi trường được set đúng

### Lỗi: "Only image files are supported"
- File không phải ảnh hoặc content-type không đúng
- Thử upload file ảnh khác (JPG, PNG)

### Lỗi: "File size exceeds 5MB"
- Ảnh quá lớn
- Nén ảnh trước khi upload

### Ảnh không hiển thị
- URL không được lưu đúng vào database
- Kiểm tra database field `avatar` hoặc `image` có dữ liệu URL không
- Kiểm tra URL có accessible từ browser hay không

## Tài liệu tham khảo

- Cloudinary Java SDK: https://cloudinary.com/documentation/java_integration
- Design Pattern Adapter: https://refactoring.guru/design-patterns/adapter
- Design Pattern Factory: https://refactoring.guru/design-patterns/factory-method
- Design Pattern Decorator: https://refactoring.guru/design-patterns/decorator

