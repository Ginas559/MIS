# Combo 3: Facade + Singleton Pattern - EnrollmentFacade

## Mô tả

Xây dựng **EnrollmentFacade** - một lớp Singleton điều phối toàn bộ luồng ghi danh phức tạp:

1. **Kiểm tra chỗ trống** (Check Capacity)
2. **Tạo đơn ghi danh** (Create Enrollment)
3. **Tính phí** (Calculate Fee from Course)
4. **Xuất hóa đơn** (Generate Invoice)

---

## Cấu trúc được Triển khai

### 1. **Singleton Pattern**
- `private static EnrollmentFacade instance`
- `public static synchronized getInstance()` - thread-safe
- Private constructor

### 2. **Facade Pattern**
- **Subsystems** (DAO):
  - `EnglishClassDAO` - quản lý lớp học
  - `EnrollmentDAO` - quản lý ghi danh
  - `InvoiceDAO` - quản lý hóa đơn
  - `StudentDAO` - quản lý học viên
  
- **Public Method**: `enrollStudent(classID, studentEmail)`
  - Gọi quy trình phức tạp qua API đơn giản

### 3. **Tái sử dụng Patterns**
- **State Pattern** (Task H): `clazz.register(student)` tự động chặn nếu lớp CLOSED
- **Template Method** (Task D): `DAO.insert()`, `DAO.update()` xử lý transaction

---

## Luồng ghi danh (Enrollment Flow)

```
User → Facade.enrollStudent(classID, email)
         ↓
    ┌────────────────────────────────────────────────┐
    │ Step 1: Kiểm tra xem lớp tồn tại & có chỗ      │
    │ - Load EnglishClass từ DB                       │
    │ - Kiểm tra currentEnrollment < maxCapacity      │
    └────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────────┐
    │ Step 2: Ghi danh qua State Pattern             │
    │ - Gọi clazz.register(student)                  │
    │ - State tự động chặn nếu CLOSED               │
    │ - Nếu đủ người → tự chuyển OPEN → RUNNING     │
    └────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────────┐
    │ Step 3: Tạo Enrollment & Tính phí              │
    │ - new Enrollment(class, student)               │
    │ - Tính totalFee từ Course                      │
    └────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────────┐
    │ Step 4: Xuất hóa đơn                          │
    │ - new Invoice(enrollment, totalFee)           │
    │ - Status: UNPAID                              │
    └────────────────────────────────────────────────┘
         ↓
    ┌────────────────────────────────────────────────┐
    │ Step 5: Lưu DB                                │
    │ - enrollmentDAO.insert(enrollment)             │
    │ - englishClassDAO.update(clazz)               │
    │ - invoiceDAO.insert(invoice)                  │
    └────────────────────────────────────────────────┘
         ↓
   Trả về EnrollmentResult(enrollment, invoice)
```

---

## Entities được tạo

### Invoice
- `id` (PK, auto-increment)
- `invoiceNumber` (unique)
- `enrollment` (FK)
- `totalAmount` (Double)
- `status` (UNPAID, PARTIALLY_PAID, PAID, CANCELLED)
- `createdAt`, `paidAt`

---

## SQL Server Schema

Đã thêm vào `sqlserver-init-seed.sql`:

```sql
CREATE TABLE Invoices (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    invoiceNumber NVARCHAR(50) UNIQUE NOT NULL,
    enrollment_id BIGINT FOREIGN KEY,
    totalAmount FLOAT NOT NULL,
    status NVARCHAR(20) NOT NULL,
    createdAt DATETIME2 NOT NULL,
    paidAt DATETIME2 NULL,
    CHECK (status IN ('UNPAID', 'PARTIALLY_PAID', 'PAID', 'CANCELLED'))
)
```

---

## Exception Handling

`EnrollmentFacade.enrollStudent()` có thể ném:

1. **IllegalArgumentException**
   - classID hoặc studentEmail null
   - Lớp không tồn tại
   - Học viên không tồn tại

2. **IllegalStateException**
   - Lớp CLOSED (từ State Pattern)
   - Lớp RUNNING (từ State Pattern)
   - Lớp CANCELLED (từ State Pattern)
   - Lớp đã đầy

---

## Unit Tests

File: `EnrollmentFacadeTest.java`

- ✅ Test Singleton
- ✅ Test validation (null input)
- ✅ Test blocking CLOSED state
- ✅ Test EnrollmentResult

---

## Sử dụng

```java
// Lấy Singleton
EnrollmentFacade facade = EnrollmentFacade.getInstance();

// Ghi danh
try {
    EnrollmentFacade.EnrollmentResult result = 
        facade.enrollStudent("CLS001", "student@test.com");
    
    System.out.println("Enrollment ID: " + result.getEnrollment().getId());
    System.out.println("Invoice: " + result.getInvoice().getInvoiceNumber());
    System.out.println("Total: " + result.getInvoice().getTotalAmount());
    
} catch (IllegalArgumentException e) {
    System.err.println("Lỗi dữ liệu: " + e.getMessage());
} catch (IllegalStateException e) {
    System.err.println("Không thể ghi danh: " + e.getMessage());
}
```

---

## Tính năng chính

✅ **Singleton** - Đảm bảo chỉ 1 instance Facade  
✅ **Facade** - Che giấu logic phức tạp, API đơn giản  
✅ **State Pattern** - Luật nghiệp ghi danh (CLOSED → block)  
✅ **Template Method** - DAO xử lý transaction  
✅ **Thread-safe** - `synchronized getInstance()`  
✅ **Exception handling** - Xử lý các tình huống lỗi  

---

## File liên quan

- `vn.iotstar.coolenglish.facade.EnrollmentFacade`
- `vn.iotstar.coolenglish.entity.Invoice`
- `vn.iotstar.coolenglish.dao.impl.InvoiceDAO`
- `vn.iotstar.coolenglish.enums.InvoiceStatus`
- `sqlserver-init-seed.sql` (cập nhật Invoices table)
- `persistence.xml` (đã đăng ký Invoice entity)

