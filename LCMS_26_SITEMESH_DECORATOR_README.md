# LCMS-26: SiteMesh Decorator Pattern - Đồng bộ giao diện Servlet/JSP

## Mô tả

Triển khai **SiteMesh Decorator** để tạo khung giao diện chung (Header/Footer + CSS/JS chung) cho các trang public, theo đúng tinh thần **Decorator Pattern**:

1. Trang nội dung giữ logic riêng (content page)
2. Decorator bọc bên ngoài để bổ sung layout chung
3. Không thay đổi luồng servlet/controller hiện tại

---

## Cấu trúc được Triển khai

### 1. **Decorator Pattern (UI Layer)**
- **Component gốc**: các trang JSP nội dung (`index.jsp`, `login.jsp`, `register.jsp`, `verify-otp.jsp`)
- **Decorator**: `WEB-INF/decorators/main.jsp`
- **Decorator engine**: SiteMesh Filter (`ConfigurableSiteMeshFilter`)

### 2. **SiteMesh Configuration**
- `web.xml`: đăng ký SiteMesh filter ở mức `/*` (REQUEST + FORWARD)
- `sitemesh3.xml`: mapping URL public vào `main.jsp`

### 3. **Nguyên tắc “thêm mới tối thiểu”**
- Không sửa servlet mapping hay business flow
- Không đụng các trang admin hiện hữu để tránh ảnh hưởng flow cũ
- Chỉ tách phần layout lặp lại sang decorator

---

## Luồng render với SiteMesh

```
Client Request (/login, /signup, /index.jsp, /verify-otp)
        ↓
Servlet/JSP xử lý như cũ (không đổi controller flow)
        ↓
SiteMesh Filter intercept response HTML
        ↓
Áp dụng decorator /WEB-INF/decorators/main.jsp
        ↓
Render:
- Header/Navbar chung
- Nội dung trang riêng (<sitemesh:write property="body"/>)
- Footer chung
```

---

## Những gì đã thay đổi

### Cấu hình mới
- Thêm SiteMesh Filter trong `src/main/webapp/WEB-INF/web.xml`
- Thêm file `src/main/webapp/WEB-INF/sitemesh3.xml`

### Template mới
- Thêm `src/main/webapp/WEB-INF/decorators/main.jsp`
  - Khung HTML chung
  - Navbar dùng session user hiện có
  - Footer chung
  - Import CSS/JS dùng chung

### Trang public được áp dụng decorator
- `index.jsp`
- `login.jsp`
- `register.jsp`
- `verify-otp.jsp`

Các trang này chỉ giữ nội dung đặc thù, bỏ phần khung lặp (header/footer/assets include) để tránh duplication.

---

## Tinh thần Decorator đã áp dụng

Decorator Pattern ở đây được áp dụng đúng mục tiêu:

- **Không thay đổi bản chất component**: logic của từng trang/login/signup không đổi.
- **Bổ sung hành vi trình bày động**: thêm layout chung từ bên ngoài qua decorator.
- **Tách concern rõ ràng**:
  - Content page: dữ liệu + form + thông báo
  - Decorator page: khung giao diện thống nhất

Điều này giúp đồng bộ UI toàn cục mà vẫn giữ code cũ ổn định.

---

## Mapping vai trò theo mẫu Decorator chuẩn

Đối chiếu theo cấu trúc chuẩn:

- **Component**: contract trang nội dung HTML mà SiteMesh xử lý (`title`, `head`, `body`).
- **ConcreteComponent**: các JSP nội dung cụ thể:
  - `index.jsp`
  - `login.jsp`
  - `register.jsp`
  - `verify-otp.jsp`
- **Decorator**: cơ chế bọc của SiteMesh (ở framework layer) gồm:
  - `ConfigurableSiteMeshFilter` (khai báo trong `web.xml`)
  - `Content` model SiteMesh (dữ liệu đã parse từ trang gốc)
- **ConcreteDecorator**: `WEB-INF/decorators/main.jsp` (layout cụ thể thêm Header/Footer/assets dùng chung).
- **Client**: trình duyệt/người dùng gửi request vào các URL đã map trong `sitemesh3.xml`.

Lưu ý: dự án dùng Decorator ở **framework level** (SiteMesh), nên vai trò `Decorator` không viết thành abstract class Java thủ công như ví dụ OOP thuần, nhưng bản chất pattern là tương đương.

---

## Lợi ích cho mở rộng tương lai

Khi dự án có thêm giao diện mới (ví dụ: trang profile, trang thông báo, trang báo cáo), decorator hiện tại giúp:

- **Thêm trang nhanh hơn**: trang mới chỉ cần viết phần nội dung chính, không phải copy lại header/footer/assets.
- **Giảm lỗi giao diện không đồng bộ**: đổi menu, logo, footer chỉ sửa tại `main.jsp`, toàn bộ trang dùng chung tự cập nhật.
- **Giảm rủi ro phá flow cũ**: thay đổi layout tập trung ở một điểm, không cần sửa rải rác nhiều JSP như trước.
- **Dễ chia việc trong team**: một người phụ trách layout chung, người khác làm nội dung trang mà ít xung đột code.

So với cách cũ (mỗi JSP tự chứa khung HTML đầy đủ), hướng decorator tiết kiệm công bảo trì và giữ “vibe” giao diện thống nhất lâu dài.

---

## Tổng hợp lợi ích của Decorator Pattern (SiteMesh)

### 1. Lợi ích kỹ thuật
- **Tách biệt trách nhiệm (Separation of Concerns)**: trang nội dung xử lý business/UI riêng; decorator xử lý layout chung.
- **Giảm trùng lặp code (DRY)**: bỏ lặp navbar/footer/assets ở nhiều JSP.
- **Mở rộng không xâm lấn (Open/Closed)**: thêm lớp bọc giao diện mà không phải sửa logic servlet/controller cũ.
- **Áp dụng động theo URL**: bật/tắt decorator bằng mapping trong `sitemesh3.xml`, không cần sửa từng trang.

### 2. Lợi ích bảo trì
- **Sửa một nơi, cập nhật toàn bộ**: thay đổi header/footer/menu tại `main.jsp` sẽ phản ánh lên các trang đã map.
- **Giảm lỗi không đồng bộ giao diện**: tránh tình trạng mỗi trang dùng biến thể header/footer khác nhau.
- **Dễ chuẩn hóa tài nguyên chung**: CSS/JS framework được quản lý tập trung, hạn chế include thiếu/thừa.

### 3. Lợi ích vận hành dự án nhóm
- **Hạn chế xung đột merge**: team frontend chỉnh layout chung ở decorator, người khác tập trung page content.
- **Onboarding nhanh hơn**: thành viên mới chỉ cần biết “trang nào map decorator” là triển khai đúng cấu trúc.
- **Giữ ổn định flow cũ**: thay đổi giao diện ít đụng business code nên giảm rủi ro regression.

### 4. Lợi ích mở rộng trong tương lai
- **Thêm page mới nhanh**: chỉ code phần thân trang, không dựng lại khung.
- **Dễ tạo nhiều layout theo ngữ cảnh**: có thể bổ sung thêm decorator khác (public/admin/auth) và map theo URL.
- **Thuận lợi cho tái thiết kế UI**: đổi diện mạo tổng thể mà không phải sửa hàng loạt JSP nội dung.

### 5. Ý nghĩa pattern trong đồ án
- **Đúng bản chất Decorator**: mở rộng hành vi trình bày bằng lớp bọc bên ngoài, không thay đổi component gốc.
- **Phù hợp Servlet/JSP thực tế**: dùng implementation phổ biến (SiteMesh) thay vì viết lại toàn bộ hạ tầng thủ công.

---

## File liên quan

- `src/main/webapp/WEB-INF/web.xml`
- `src/main/webapp/WEB-INF/sitemesh3.xml`
- `src/main/webapp/WEB-INF/decorators/main.jsp`
- `src/main/webapp/index.jsp`
- `src/main/webapp/login.jsp`
- `src/main/webapp/register.jsp`
- `src/main/webapp/verify-otp.jsp`
