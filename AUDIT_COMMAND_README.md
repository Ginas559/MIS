# Task N: Audit Log + Command Pattern + JPA Entity Listener

## Mô tả

Xây dựng hệ thống **Audit Log** ghi lại mọi thay đổi dữ liệu bằng **JPA Entity Listeners** và triển khai **Command Pattern** để:

1. **Đóng gói hành động ghi log** thành command
2. **Lưu lịch sử command** để hỗ trợ undo
3. **Tái sử dụng tư duy Protection Proxy (Task G)** để check quyền trước khi execute/undo

---

## Cấu trúc được Triển khai

### 1. **JPA Entity Listener (Audit tự động)**
- `AuditEntityListener`
  - `@PostPersist` → ghi `INSERT`
  - `@PostUpdate` → ghi `UPDATE`
  - `@PreRemove` → ghi `DELETE`

- `AuditSnapshotBuilder`
  - Trích xuất `entityId`
  - Snapshot các field scalar ra JSON (`details`)
  - Bỏ qua field nhạy cảm như `password`

### 2. **Command Pattern**
- **Command**: `AuditCommand` (`execute()`, `undo()`)
- **ConcreteCommand**: `PersistAuditLogCommand`
- **Receiver**: `AuditLogReceiver` (persist/delete `AuditLog`)
- **Invoker**: `AuditCommandInvoker`
  - Lưu stack lịch sử command
  - `undoLast()` để rollback log gần nhất

### 2.1 **Các thành phần tham gia trong Command Pattern (theo chuẩn)**
- **Command**:
  - `AuditCommand` là interface định nghĩa hành vi command qua `execute()` và `undo()`
  - Request ghi audit được đóng gói thành object command

- **ConcreteCommand**:
  - `PersistAuditLogCommand` implement `AuditCommand`
  - Gắn kết giữa `AuditLogReceiver` và hành động ghi/xóa audit
  - `execute()` gọi `receiver.save(auditLog)`
  - `undo()` gọi `receiver.deleteById(persistedAuditId)`

- **Client**:
  - `AuditEntityListener` đóng vai trò client trong luồng hiện tại
  - Khi callback JPA chạy, listener tạo `PersistAuditLogCommand`, gắn receiver và actor/security proxy phù hợp

- **Invoker**:
- `AuditCommandInvoker` nhận command từ client và gọi `execute()`
- Lưu command vào history stack để hỗ trợ `undoLast()`
- `undoLast()` chỉ xóa command khỏi history khi `undo()` thành công (nếu undo fail thì giữ lại command)

- **Receiver**:
  - `AuditLogReceiver` là nơi xử lý business logic thực tế với DB (`persist`, `delete`)
  - ConcreteCommand chỉ điều phối, không chứa transaction logic chính

### 3. **Protection Proxy cho Audit Command**
- `AuditCommandSecurityProxy` kiểm tra quyền trước khi:
  - `execute()`
  - `undo()`

- Rule:
  - `execute()` → cho phép mọi role (để đảm bảo truy vết đầy đủ)
  - `undo()` → chỉ `ADMIN` được phép
  - actor null/system → cho phép (để không phá luồng hệ thống)

### 4. **Actor Context từ session**
- `AuditActorContext` + `AuditActor` (ThreadLocal)
- `AuthenticationFilter` set/clear actor theo request admin để listener lấy được user hiện tại

---

## Luồng Audit (Flow)

```
User/System request → AuthenticationFilter (nếu có session admin)
                       ↓
               set AuditActorContext
                       ↓
         DAO insert/update/delete entity
                       ↓
               JPA callback kích hoạt
                       ↓
              AuditEntityListener.record()
                       ↓
  Tạo PersistAuditLogCommand + SecurityProxy
                       ↓
      AuditCommandInvoker.executeCommand(...)
                       ↓
            AuditLogReceiver.persist(AuditLog)
                       ↓
                 lưu vào bảng audit_logs
```

Undo:

```
AuditCommandInvoker.undoLast()
   → command.undo()
   → AuditLogReceiver.deleteById(auditId)
```

---

## Entity AuditLog

- `auditId` (PK, auto-increment)
- `action` (`INSERT`/`UPDATE`/`DELETE`)
- `entityName`
- `entityId`
- `actorId`
- `actorUsername`
- `actorRole`
- `changedAt`
- `details` (snapshot JSON)

---

## Entity đã gắn Listener

- `Course`
- `Room`
- `EnglishClass`
- `Schedule`
- `Session`
- `Enrollment`
- `Invoice`
- `ExamResult`
- `UserAccount`
- `Person` (nên áp dụng cho cả hierarchy Student/Teacher/Staff)

---

## Persistence / Schema

- Đã đăng ký thêm entity:
  - `vn.iotstar.coolenglish.entity.AuditLog` trong `persistence.xml`
- Dự án đang dùng `hibernate.hbm2ddl.auto=update` nên bảng `audit_logs` được tự tạo/cập nhật theo entity.

---

## Unit Tests

### `PersistAuditLogCommandTest`
- ✅ `execute()` gọi receiver.save
- ✅ `undo()` gọi receiver.deleteById
- ✅ `AuditCommandInvoker` lưu history và undo được command gần nhất

### `AuditCommandSecurityProxyTest`
- ✅ `execute()` cho phép mọi role (`ADMIN/STAFF/STUDENT`)
- ✅ `undo()` chỉ cho `ADMIN`, role khác bị `SecurityException`

---

## Sử dụng

Trong luồng hiện tại không cần gọi tay để tạo audit log. Chỉ cần thao tác CRUD qua JPA/DAO, listener sẽ tự ghi log.

Nếu cần undo log gần nhất (phục vụ demo pattern):

```java
boolean undone = AuditCommandInvoker.getInstance().undoLast();
```

---

## Kịch bản demo quyền Undo (để trình bày)

### 1) STAFF bị chặn Undo
- Đăng nhập bằng tài khoản `STAFF`.
- Thực hiện một thao tác CRUD (ví dụ update class/course) để tạo audit log.
- Đặt breakpoint sau khi DAO đã chạy xong, ví dụ:
  - `src/main/java/vn/iotstar/coolenglish/web/ClassController.java`
  - hàm `saveClass(...)`
  - dòng `redirectToList(req, resp);` (ngay sau `classDAO.update(...)` / `classDAO.insert(...)`)
- Trong Debug Evaluate Expression:

```java
vn.iotstar.coolenglish.audit.command.AuditCommandInvoker.getInstance().undoLast()
```

- Kết quả mong đợi: ném `SecurityException` với thông điệp `Ban khong co quyen undo audit log.`
- Có thể kiểm tra actor hiện tại trong thread debug:

```java
vn.iotstar.coolenglish.audit.context.AuditActorContext.getCurrentActor()
```

- Để expression này trả về actor đúng (không bị `null`), cần đặt breakpoint trong:
  - `src/main/java/vn/iotstar/coolenglish/audit/listener/AuditEntityListener.java`
  - hàm `record(...)`
  - ngay trong request CRUD đang chạy
- Nếu actor trả về không phải `ADMIN` (hoặc `null`), undo sẽ bị chặn theo rule proxy hiện tại.

### 2) ADMIN được phép Undo
- Đăng nhập bằng tài khoản `ADMIN`.
- Thực hiện thao tác CRUD để tạo audit log.
- Tại đúng breakpoint tương tự ở `saveCourse(...)`, chạy lại expression:

```java
vn.iotstar.coolenglish.audit.command.AuditCommandInvoker.getInstance().undoLast()
```

- Kết quả mong đợi: trả về `true`.
- Trước khi gọi undo, nên kiểm tra actor hiện tại:

```java
vn.iotstar.coolenglish.audit.context.AuditActorContext.getCurrentActor()
```

- Kết quả mong đợi: actor là `ADMIN`.
- Nếu kiểm tra ở ngữ cảnh ngoài request/breakpoint sai chỗ thì có thể nhận `null`.
- Kiểm tra SQL:

```sql
SELECT TOP 5 * FROM audit_logs ORDER BY audit_id DESC;
```

- Dòng audit mới nhất sẽ biến mất sau undo thành công.

---

## Tính năng chính

✅ **JPA Entity Listener** - Audit tự động theo lifecycle  
✅ **Command Pattern** - Command/Receiver/Invoker tách vai trò rõ ràng  
✅ **Undo Support** - `undoLast()` cho command đã execute  
✅ **Proxy-based Authorization** - mọi role đều được ghi audit, nhưng chỉ `ADMIN` được undo  
✅ **Ít xâm lấn code cũ** - chỉ gắn listener + actor context, không đổi logic nghiệp vụ chính  

---

## Lợi ích áp dụng vào dự án

- **Giảm trùng lặp code audit**: dùng Entity Listener nên không cần viết log thủ công trong từng controller/DAO.
- **Dễ bảo trì và mở rộng**: thêm loại audit command mới mà không sửa invoker/client hiện có (phù hợp OCP).
- **Tách biệt trách nhiệm**:
  - `Command` điều phối hành vi
  - `Receiver` xử lý DB
  - `Proxy` xử lý phân quyền
- **Tăng khả năng truy vết**: lưu được action, actor, entity và snapshot dữ liệu tại thời điểm thay đổi.
- **Dễ test**: từng vai trò pattern có thể test độc lập (`Command`, `Proxy`, `Invoker`).

---

## Hướng mở rộng tương lai

- Thêm trang admin để xem/lọc `audit_logs` theo actor, action, thời gian.
- Expose endpoint nội bộ cho undo có kiểm soát (chỉ ADMIN).
- Mở rộng undo từ mức audit-log sang mức nghiệp vụ (nếu đề tài yêu cầu).
- Lưu history undo bền vững hơn (DB/queue) thay vì chỉ trong memory runtime.
- Bổ sung masking dữ liệu nhạy cảm nâng cao trong `details`.

---

## File liên quan

- `vn.iotstar.coolenglish.entity.AuditLog`
- `vn.iotstar.coolenglish.audit.listener.AuditEntityListener`
- `vn.iotstar.coolenglish.audit.util.AuditSnapshotBuilder`
- `vn.iotstar.coolenglish.audit.command.AuditCommand`
- `vn.iotstar.coolenglish.audit.command.PersistAuditLogCommand`
- `vn.iotstar.coolenglish.audit.command.AuditCommandInvoker`
- `vn.iotstar.coolenglish.audit.receiver.AuditLogReceiver`
- `vn.iotstar.coolenglish.audit.proxy.AuditCommandSecurityProxy`
- `vn.iotstar.coolenglish.audit.context.AuditActor`
- `vn.iotstar.coolenglish.audit.context.AuditActorContext`
- `vn.iotstar.coolenglish.web.AuthenticationFilter`
- `src/main/resources/META-INF/persistence.xml`
- `vn.iotstar.coolenglish.audit.command.PersistAuditLogCommandTest`
- `vn.iotstar.coolenglish.audit.proxy.AuditCommandSecurityProxyTest`
