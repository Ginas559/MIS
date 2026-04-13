# 🏗️ Kiến trúc Upload Ảnh - Chi tiết Technical

## 📊 Kiến trúc Tổng quát

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                          │
│  (profile.jsp, course-form.jsp - HTML Form with file input)  │
└────────────────────┬────────────────────────────────────────┘
                     │ HTTP POST (multipart/form-data)
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                      SERVLET LAYER                           │
│  (ProfileController, CourseController)                       │
│  - @MultipartConfig                                          │
│  - Extract Part from request                                 │
│  - Call UploadUtils methods                                  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    UTILITY LAYER                             │
│         (UploadUtils - Facade for Controllers)               │
│  - uploadAvatar()                                            │
│  - uploadAcademicContentImage()                              │
│  - uploadImage()                                             │
│  - deleteImage()                                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    FACTORY LAYER                             │
│           (ImageUploadServiceFactory)                        │
│  - createService(type) → IImageUploadService                 │
│  - createDefaultService() → Cloudinary                       │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                   SERVICE LAYER (Adapter Pattern)            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         IImageUploadService (Interface)             │   │
│  │  - uploadImage(Part)                                 │   │
│  │  - uploadImage(Part, publicId)                       │   │
│  │  - deleteImage(publicId)                             │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │ implements                            │
│                     ↓                                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   CloudinaryImageUploadAdapter                       │   │
│  │  - uploadImage() implementation                       │   │
│  │  - validateFileType()                                │   │
│  │  - Build Cloudinary options                          │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │                                        │
│                     ↓                                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         ImageProcessingDecorator                     │   │
│  │  - Wrap IImageUploadService                          │   │
│  │  - Add processing config                             │   │
│  │  - enableResize, enableWatermark, etc.               │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │ extends                               │
│                     ↓                                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │        AvatarUploadDecorator                         │   │
│  │  - Special handling for avatar                       │   │
│  │  - Resize 200x200                                    │   │
│  │  - Add transformation options                        │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                   CONFIG LAYER                               │
│              (CloudinaryConfig)                              │
│  - Load .env credentials                                    │
│  - Create Cloudinary singleton instance                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
        ┌────────────────────────────────┐
        │     Cloudinary API             │
        │  (External Cloud Service)      │
        └────────────────────────────────┘
                     │
                     ↓
        ┌────────────────────────────────┐
        │    Cloud Storage (CDN)         │
        │  (Secure URL returned)         │
        └────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                   ENTITY LAYER                               │
│  (Person, Course, AcademicContentEntity)                    │
│  - Save image URL to database field                         │
│  - Person.avatar                                            │
│  - Course.image                                             │
│  - AcademicContentEntity.imageLink                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
        ┌────────────────────────────────┐
        │     Database (SQL Server)      │
        │  - Persist image URLs          │
        └────────────────────────────────┘
```

---

## 🎯 Design Patterns Chi tiết

### 1️⃣ ADAPTER PATTERN

**Vấn đề**: Cloudinary SDK có interface khác với code chính

**Giải pháp**: Tạo adapter wrapper

```java
// Target Interface (Contract)
public interface IImageUploadService {
    String uploadImage(Part filePart) throws IOException;
    String uploadImage(Part filePart, String publicId) throws IOException;
    boolean deleteImage(String publicId);
}

// Adaptee (Cloudinary SDK)
Cloudinary cloudinary = new Cloudinary(...);
cloudinary.uploader().upload(inputStream, options);

// Adapter
public class CloudinaryImageUploadAdapter implements IImageUploadService {
    private final Cloudinary cloudinary;
    
    @Override
    public String uploadImage(Part filePart) throws IOException {
        // Chuyển đổi Part → InputStream
        // Gọi cloudinary.uploader().upload()
        // Trả về secure_url
    }
}
```

**Lợi ích**:
- Code không phụ thuộc Cloudinary SDK trực tiếp
- Dễ thay AWS S3 mà không cần sửa Controller
- Decoupling và Loose Coupling

```
Before:
Controller → Cloudinary SDK (tightly coupled)

After:
Controller → IImageUploadService → Cloudinary SDK (loosely coupled)
         ↓
         AWSS3Adapter
         ↓
         GoogleCloudAdapter
```

---

### 2️⃣ FACTORY METHOD PATTERN

**Vấn đề**: Tạo service ở nhiều chỗ, khó quản lý cấu hình

**Giải pháp**: Tập trung tạo service trong Factory

```java
// Factory
public class ImageUploadServiceFactory {
    public static IImageUploadService createService(String type) {
        return switch (type) {
            case "CLOUDINARY" -> new CloudinaryImageUploadAdapter();
            case "AWS_S3" -> new AWSS3ImageUploadAdapter();
            case "GOOGLE" -> new GoogleCloudAdapter();
            default -> throw new IllegalArgumentException();
        };
    }
}

// Usage
IImageUploadService service = ImageUploadServiceFactory.createService("CLOUDINARY");
String url = service.uploadImage(filePart);
```

**Lợi ích**:
- Một chỗ quản lý việc tạo instance
- Dễ thêm loại service mới
- Tuân thủ Open/Closed Principle

```
Usage pattern:
┌─────────────────┐
│ Client Code     │
└────────┬────────┘
         │ .createService()
         ↓
┌─────────────────────────────────────┐
│ ImageUploadServiceFactory           │
│ - createService(type): ServiceImpl   │
└────────┬────────────────────────────┘
         ↓
    Conditional Creation
    ├─→ CLOUDINARY → CloudinaryImageUploadAdapter
    ├─→ AWS_S3 → AWSS3ImageUploadAdapter
    └─→ GOOGLE → GoogleCloudAdapter
```

---

### 3️⃣ DECORATOR PATTERN

**Vấn đề**: Avatar cần resize, future có watermark, compression

**Giải pháp**: Decorator thêm tính năng lên adapter

```java
// Component interface
public interface IImageUploadService {
    String uploadImage(Part filePart) throws IOException;
}

// Concrete Component
public class CloudinaryImageUploadAdapter implements IImageUploadService {
    // Basic upload
}

// Decorator (Base)
public class ImageProcessingDecorator implements IImageUploadService {
    protected IImageUploadService wrappedService;
    protected ImageProcessingConfig config; // resize, watermark settings
}

// Concrete Decorator
public class AvatarUploadDecorator extends ImageProcessingDecorator {
    // config.setResizeWidth(200);
    // config.setResizeHeight(200);
    // Thêm transformation options vào Cloudinary
}

// Usage
IImageUploadService baseService = new CloudinaryImageUploadAdapter();
IImageUploadService avatarService = new AvatarUploadDecorator(baseService);
String url = avatarService.uploadImage(filePart);
```

**Flow với Decorator**:

```
uploadImage() call
    ↓
AvatarUploadDecorator.uploadImage()
    ├─ Set resize config (200x200)
    ├─ Add transformation to cloudinary options
    ↓
CloudinaryImageUploadAdapter.uploadImage()
    ├─ Validate file
    ├─ Call cloudinary.uploader().upload(inputStream, options)
    ↓
Return URL with transformed image
```

**Lợi ích**:
- Thêm/tắt tính năng mà không sửa code
- Composition thay inheritance
- Flexible và maintainable

```
Decorator Chain Pattern:
baseService
    ↓
ImageProcessingDecorator(baseService)
    ├─ resize config
    ├─ compression config
    ├─ watermark config
    ↓
AvatarUploadDecorator(ImageProcessingDecorator)
    ├─ Avatar specific: 200x200
    ├─ Face detection gravity
    ↓
Final Service Ready to Use
```

---

## 📦 Class Relationships

```java
// Adapter Pattern: Chuyển đổi SDK
interface IImageUploadService {
    uploadImage(Part)
}
class CloudinaryImageUploadAdapter implements IImageUploadService {
    private Cloudinary cloudinary;
}

// Factory Pattern: Tạo service
class ImageUploadServiceFactory {
    static IImageUploadService createService(String type)
}

// Decorator Pattern: Thêm tính năng
class ImageProcessingDecorator implements IImageUploadService {
    protected IImageUploadService wrappedService;
    protected ImageProcessingConfig config;
}
class AvatarUploadDecorator extends ImageProcessingDecorator {
    // Specific for avatar
}

// Utility: Helper for Controller
class UploadUtils {
    static uploadAvatar(Part, userId)
    static uploadImage(Part)
    static deleteImage(publicId)
}

// Config: Centralized configuration
class CloudinaryConfig {
    static Cloudinary getInstance()
}
```

---

## 🔄 Data Flow Example: Upload Avatar

```
1. User selects file in profile.jsp
   ↓
2. <form enctype="multipart/form-data" method="post">
   ↓
3. ProfileController.doPost()
   │
   ├─ Part avatarPart = req.getPart("avatar")
   │
   ├─ UploadUtils.uploadAvatar(avatarPart, userId)
   │   │
   │   ├─ ImageUploadServiceFactory.createDefaultService()
   │   │   → CloudinaryImageUploadAdapter
   │   │
   │   ├─ new AvatarUploadDecorator(baseService)
   │   │
   │   └─ avatarService.uploadImage(filePart, publicId)
   │       │
   │       ├─ AvatarUploadDecorator.uploadImage()
   │       │  │
   │       │  ├─ validateFileType() - Check image/*
   │       │  ├─ buildCloudinaryOptions() - Add resize 200x200
   │       │  │
   │       │  └─ Delegate to wrapped service
   │       │
   │       └─ CloudinaryImageUploadAdapter.uploadImage()
   │           │
   │           ├─ Try-with-resources: InputStream is = filePart.getInputStream()
   │           │
   │           └─ cloudinary.uploader().upload(is, options)
   │               → Returns Map with "secure_url"
   │               → Parse and return URL
   │
   ├─ String avatarUrl = "https://res.cloudinary.com/.../user_123.jpg"
   │
   ├─ person.setAvatar(avatarUrl)
   │
   ├─ personDAO.update(person)
   │  └─ INSERT/UPDATE INTO person SET avatar = ?
   │
   └─ Redirect to /profile?msg=updated

4. Next request to /profile
   │
   ├─ profileController.doGet()
   │
   ├─ Person person = personDAO.findById(...)
   │
   ├─ req.setAttribute("person", person)
   │
   └─ Forward to profile.jsp
       │
       └─ <img src="${person.avatar}"> displays image from Cloudinary CDN
```

---

## 🛡️ Error Handling Flow

```
try {
    Part filePart = req.getPart("avatar")
    
    if (filePart == null || filePart.getSize() == 0) {
        throw IllegalArgumentException("File upload không được để trống")
    }
    
    UploadUtils.uploadAvatar(filePart, userId)
    │
    ├─ Validate file type (image/*)
    │  └─ if NOT image → IllegalArgumentException
    │
    ├─ Validate file size (≤ 5MB)
    │  └─ if TOO LARGE → IllegalArgumentException
    │
    └─ Cloudinary upload
       └─ if NETWORK ERROR → IOException
       
} catch (IllegalArgumentException e) {
    // Invalid file format/size
    req.setAttribute("error", e.getMessage())
    forward to profile.jsp
    
} catch (IOException e) {
    // Network error, Cloudinary down, etc.
    req.setAttribute("error", "Lỗi upload ảnh: " + e.getMessage())
    forward to profile.jsp
    
} finally {
    // Part resource auto-closed (try-with-resources)
}
```

---

## 📊 Component Interaction Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│ Controller Layer                                                  │
├──────────────────────────────────────────────────────────────────┤
│ ProfileController          │ CourseController                     │
│ - doPost()                 │ - saveCourse()                       │
│ - Handles /profile         │ - Handles /admin/course/*            │
└───────────┬────────────────┴───────────────┬──────────────────────┘
            │                               │
            │ calls                         │ calls
            ↓                               ↓
      ┌─────────────────────────────────────────┐
      │ UploadUtils (Facade)                     │
      │ - uploadAvatar(Part, userId)             │
      │ - uploadAcademicContentImage()           │
      │ - uploadImage(Part)                      │
      │ - deleteImage(publicId)                  │
      └──────────────┬──────────────────────────┘
                     │ uses
                     ↓
      ┌──────────────────────────────────────────────┐
      │ ImageUploadServiceFactory                    │
      │ - createService(type)                        │
      │ - createDefaultService()                     │
      └──────────────┬───────────────────────────────┘
                     │ returns
                     ↓
      ┌──────────────────────────────────────────────┐
      │ IImageUploadService (Interface)              │
      │ + uploadImage(Part): String                  │
      │ + uploadImage(Part, pubId): String           │
      │ + deleteImage(pubId): boolean                │
      └──────────────┬───────────────────────────────┘
                     │ implemented by
           ┌─────────┴──────────────┐
           ↓                        ↓
    ┌─────────────────────┐  ┌──────────────────────────┐
    │CloudinaryAdapter    │  │ImageProcessingDecorator  │
    │- uploadImage()      │  │- wrappedService          │
    │- validateFile()     │  │- config (resize, etc.)   │
    │- callCloudinary()   │  │- delegates to wrapped    │
    └─────────────────────┘  └────────────┬─────────────┘
                                          │ extended by
                                          ↓
                                   ┌──────────────────────┐
                                   │AvatarUploadDecorator │
                                   │- Avatar specific     │
                                   │- Resize 200x200     │
                                   └──────────────────────┘

      ┌──────────────────────────────────────────────┐
      │ CloudinaryConfig                             │
      │ - getInstance(): Cloudinary                  │
      └────────────────┬─────────────────────────────┘
                       │ reads from
                       ↓
      ┌──────────────────────────────────────────────┐
      │ .env file                                    │
      │ CLOUDINARY_CLOUD_NAME=...                    │
      │ CLOUDINARY_API_KEY=...                       │
      │ CLOUDINARY_API_SECRET=...                    │
      └──────────────────────────────────────────────┘
```

---

## 🗄️ Database Integration

```
┌─────────────────────────────────────────────────┐
│ Entity Classes (JPA)                            │
├─────────────────────────────────────────────────┤
│ Person                  │ Course               │
│ - id                    │ - courseID           │
│ - fullName              │ - courseName         │
│ - avatar ← NEW          │ - image ← NEW        │
│ - email                 │ - fee                │
│ - gender                │ - status             │
└────────┬────────────────┴──────────┬───────────┘
         │ persisted by              │ persisted by
         ↓                           ↓
    ┌────────────────────────────────────────────┐
    │ Hibernate ORM                              │
    │ - Entity mapping                           │
    │ - SQL generation                           │
    └────────┬─────────────────────────────────┘
             │
             ↓
    ┌────────────────────────────────────────────┐
    │ MSSQL Database                             │
    ├────────────────────────────────────────────┤
    │ Table: person                              │
    │ - id INT PRIMARY KEY                       │
    │ - full_name VARCHAR(100)                   │
    │ - avatar VARCHAR(500) NULL  ← NEW          │
    │ - email VARCHAR(100)                       │
    │                                            │
    │ Table: Courses                             │
    │ - courseID VARCHAR(50) PRIMARY KEY         │
    │ - courseName VARCHAR(255)                  │
    │ - image VARCHAR(500) NULL  ← NEW           │
    │ - fee DECIMAL(10,2)                        │
    └────────────────────────────────────────────┘
```

---

## 🎯 Key Concepts

| Concept | Implementation | File |
|---------|---|---|
| **Target Interface** | IImageUploadService | service/IImageUploadService.java |
| **Adapter** | CloudinaryImageUploadAdapter | service/impl/CloudinaryImageUploadAdapter.java |
| **Factory** | ImageUploadServiceFactory | service/impl/ImageUploadServiceFactory.java |
| **Decorator Base** | ImageProcessingDecorator | service/impl/ImageProcessingDecorator.java |
| **Concrete Decorator** | AvatarUploadDecorator | service/impl/AvatarUploadDecorator.java |
| **Utility/Facade** | UploadUtils | util/UploadUtils.java |
| **Configuration** | CloudinaryConfig | config/CloudinaryConfig.java |

---

**Tài liệu này giải thích chi tiết từng pattern và cách chúng tương tác trong hệ thống upload ảnh.**

