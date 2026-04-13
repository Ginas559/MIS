# Hướng dẫn thêm Upload Ảnh vào các trang khác

## Mục đích
Tài liệu này hướng dẫn cách thêm chức năng upload ảnh vào các trang mới sử dụng hệ thống Cloudinary đã được xây dựng.

## Các bước cơ bản

### 1. Controller - Thêm @MultipartConfig
```java
import jakarta.servlet.http.Part;
import vn.iotstar.coolenglish.util.UploadUtils;

@MultipartConfig(
    maxFileSize = 5242880,
    maxRequestSize = 5242880,
    fileSizeThreshold = 0
)
@WebServlet("/your-url")
public class YourController extends HttpServlet {
    // ...
}
```

### 2. Controller - Xử lý upload ảnh trong doPost
```java
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    
    // Lấy các dữ liệu khác từ form
    String name = req.getParameter("name");
    
    // Xử lý upload ảnh
    String imageUrl = null;
    try {
        Part imagePart = req.getPart("imageFieldName");
        if (imagePart != null && imagePart.getSize() > 0) {
            // Chọn phương thức upload phù hợp:
            // Cho avatar: imageUrl = UploadUtils.uploadAvatar(imagePart, userId);
            // Cho content: imageUrl = UploadUtils.uploadAcademicContentImage(imagePart, contentId);
            // Cho khác: imageUrl = UploadUtils.uploadImage(imagePart);
            imageUrl = UploadUtils.uploadImage(imagePart);
        }
    } catch (IllegalArgumentException e) {
        // Hiển thị error message
        req.setAttribute("error", e.getMessage());
        // Forward lại form
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/your-form.jsp");
        dispatcher.forward(req, resp);
        return;
    } catch (IOException e) {
        req.setAttribute("error", "Lỗi upload ảnh: " + e.getMessage());
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/your-form.jsp");
        dispatcher.forward(req, resp);
        return;
    }
    
    // Lưu dữ liệu vào entity
    YourEntity entity = new YourEntity();
    entity.setName(name);
    if (imageUrl != null) {
        entity.setImageUrl(imageUrl);
    }
    
    // Lưu vào database
    dao.insert(entity);
    
    // Redirect hoặc forward
    resp.sendRedirect(req.getContextPath() + "/your-list");
}
```

### 3. Entity - Thêm field image
```java
@Entity
@Table(name = "YourTable")
public class YourEntity {
    // ... các field khác
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // Getter
    public String getImageUrl() {
        return imageUrl;
    }
    
    // Setter
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
```

### 4. JSP Form - Thêm file input
```html
<form method="post" action="${pageContext.request.contextPath}/your-url" enctype="multipart/form-data">
    <!-- Các input khác -->
    
    <!-- Upload ảnh -->
    <div>
        <label for="imageField" class="form-label">Ảnh</label>
        <!-- Nếu có ảnh cũ, hiển thị preview -->
        <% if (entity != null && entity.getImageUrl() != null) { %>
            <img src="<%= entity.getImageUrl() %>" style="max-width: 300px; margin-bottom: 1rem;">
        <% } %>
        <!-- File input -->
        <input type="file" id="imageField" name="imageFieldName" class="form-control" accept="image/*">
        <small class="text-muted">Định dạng: JPG, PNG. Tối đa 5MB</small>
    </div>
    
    <button type="submit" class="btn btn-primary">Lưu</button>
</form>
```

## Các phương thức UploadUtils sẵn có

### Dành cho Avatar
```java
String avatarUrl = UploadUtils.uploadAvatar(Part filePart, Long userId);
```
- **Tự động resize**: 200x200 pixel
- **Công dụng**: Ảnh đại diện người dùng
- **Lưu vào**: Person.avatar

### Dành cho Content Image
```java
String contentImageUrl = UploadUtils.uploadAcademicContentImage(Part filePart, Long contentId);
```
- **Công dụng**: Ảnh học liệu
- **Lưu vào**: AcademicContentEntity.imageLink

### Tổng quát
```java
String imageUrl = UploadUtils.uploadImage(Part filePart);
```
- **Công dụng**: Ảnh tổng quát cho bất kỳ nơi nào
- **Lưu vào**: Tùy theo field của entity

### Xóa ảnh
```java
boolean deleted = UploadUtils.deleteImage(String publicId);
```
- **Công dụng**: Xóa ảnh khỏi Cloudinary
- **Lưu ý**: Cần extract publicId từ URL hoặc lưu publicId trong database

## Ví dụ cụ thể: Thêm upload ảnh cho Lesson

### 1. Lesson Entity
```java
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // Getter/Setter
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
```

### 2. LessonController
```java
@MultipartConfig(maxFileSize = 5242880, maxRequestSize = 5242880, fileSizeThreshold = 0)
@WebServlet("/lesson/*")
public class LessonController extends HttpServlet {
    private LessonDAO lessonDAO = new LessonDAO();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        
        String imageUrl = null;
        try {
            Part imagePart = req.getPart("lessonImage");
            if (imagePart != null && imagePart.getSize() > 0) {
                imageUrl = UploadUtils.uploadImage(imagePart);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi upload ảnh: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/lesson-form.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setDescription(description);
        if (imageUrl != null) {
            lesson.setImageUrl(imageUrl);
        }
        
        lessonDAO.insert(lesson);
        resp.sendRedirect(req.getContextPath() + "/lesson/list");
    }
}
```

### 3. lesson-form.jsp
```html
<form method="post" action="${pageContext.request.contextPath}/lesson/add" enctype="multipart/form-data">
    <div>
        <label for="title">Tiêu đề</label>
        <input type="text" id="title" name="title" class="form-control" required>
    </div>
    
    <div>
        <label for="description">Mô tả</label>
        <textarea id="description" name="description" class="form-control"></textarea>
    </div>
    
    <div>
        <label for="lessonImage">Ảnh</label>
        <input type="file" id="lessonImage" name="lessonImage" class="form-control" accept="image/*">
        <small>JPG, PNG. Tối đa 5MB</small>
    </div>
    
    <button type="submit" class="btn btn-primary">Tạo</button>
</form>
```

## Lưu ý quan trọng

1. **Luôn thêm enctype="multipart/form-data"** vào form HTML
2. **Xử lý Exception** cho từng trường hợp upload ảnh
3. **Validate file type** tự động (chỉ cho image/*)
4. **Validate file size** tự động (tối đa 5MB)
5. **Lưu URL** vào database, không lưu file
6. **Hiển thị preview** ảnh cũ nếu có

## Các tường hợp sử dụng

### Khi nào dùng uploadAvatar?
- Upload ảnh đại diện người dùng
- Cần resize tự động về 200x200

### Khi nào dùng uploadAcademicContentImage?
- Upload ảnh cho học liệu (module, lesson, etc.)
- Cần quản lý ảnh với contentId

### Khi nào dùng uploadImage?
- Upload ảnh khác (course, banner, etc.)
- Không cần xử lý đặc biệt

## Chỉnh sửa config Decorator

Nếu muốn thay đổi kích thước resize hoặc các option khác:

```java
// Tạo config tùy chỉnh
ImageProcessingDecorator.ImageProcessingConfig config = 
    new ImageProcessingDecorator.ImageProcessingConfig();
config.setResizeWidth(400);
config.setResizeHeight(400);
config.setEnableCompression(true);
config.setCompressionQuality(90);

// Sử dụng decorator với config tùy chỉnh
IImageUploadService service = ImageUploadServiceFactory.createDefaultService();
IImageUploadService avatarService = new AvatarUploadDecorator(service, config);
String url = avatarService.uploadImage(filePart, publicId);
```

## Tiếp theo

- Thêm Watermark: Mở rộng AvatarUploadDecorator để thêm watermark
- Tối ưu ảnh: Thêm compression lớn hơn
- CDN caching: Cấu hình Cloudinary CDN
- Batch upload: Hỗ trợ upload nhiều ảnh cùng lúc

