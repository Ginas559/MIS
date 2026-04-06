# Class Management + State Pattern

## What was added
- `EnglishClass` entity: represents the class context in the State Pattern.
- `Enrollment` entity: stores student enrollments.
- State Pattern package: `ClassState`, `OpenClassState`, `ClosedClassState`, `BlockedClassState`, `ClassStateFactory`.
- CRUD + enrollment controller: `ClassController`.
- Admin JSPs: `class-list.jsp`, `class-form.jsp`.
- SQL Server seed script updates: `sqlserver-init-seed.sql`.

## How it works
- `OPEN`: student enrollment is allowed.
- `CLOSED`: enrollment is blocked with an exception.
- `RUNNING` and `CANCELLED`: enrollment is also blocked.
- When an `OPEN` class reaches `maxCapacity`, it automatically transitions to `RUNNING`.

## Routes
- `/admin/class` - class list
- `/admin/class/add` - add class
- `/admin/class/update` - update class
- `/admin/class/delete` - delete class
- `/admin/class/register` - enroll a student by email

## SQL Server seed
Run:
```powershell
sqlcmd -S localhost,1433 -U sa -P 123456 -i "D:\cc_ltw\workspace\MISEnglish\sqlserver-init-seed.sql" -C
```

## Notes
- `EnglishClass` is used instead of `Class` because `Class` is a Java reserved keyword.
- `person` uses single-table inheritance for `Student`, `Teacher`, and `Staff`.

