-- seed_admin_manual.sql
-- SQL Server - tạo/cập nhật tài khoản ADMIN cho CoolEnglish
-- Chạy nhiều lần vẫn an toàn (idempotent).

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @AdminEmail NVARCHAR(100) = 'admin@coolenglish.local';
    DECLARE @AdminUsername NVARCHAR(50) = 'admin';
    DECLARE @AdminPassword NVARCHAR(100) = 'admin123'; -- doi ngay sau khi dang nhap lan dau
    DECLARE @AdminFullName NVARCHAR(100) = N'System Admin';
    DECLARE @AdminGender NVARCHAR(20) = 'OTHER';       -- MALE/FEMALE/OTHER
    DECLARE @AdminStaffCode NVARCHAR(50) = 'ADM001';
    DECLARE @AdminPosition NVARCHAR(100) = N'Administrator';

    -- 1) Upsert person (SINGLE_TABLE, dung person_type = STAFF)
    MERGE dbo.person AS target
    USING (
        SELECT
            @AdminEmail AS email,
            @AdminFullName AS full_name,
            @AdminGender AS gender,
            @AdminStaffCode AS staff_id,
            @AdminPosition AS position,
            CAST('STAFF' AS NVARCHAR(31)) AS person_type
    ) AS source
    ON target.email = source.email
    WHEN MATCHED THEN
        UPDATE SET
            target.full_name = source.full_name,
            target.gender = source.gender,
            target.staff_id = source.staff_id,
            target.position = source.position,
            target.person_type = source.person_type
    WHEN NOT MATCHED THEN
        INSERT (full_name, gender, phone, email, avatar, staff_id, position, person_type)
        VALUES (source.full_name, source.gender, NULL, source.email, NULL, source.staff_id, source.position, source.person_type);

    DECLARE @PersonId BIGINT;
    SELECT TOP 1 @PersonId = p.id
    FROM dbo.person p
    WHERE p.email = @AdminEmail
    ORDER BY p.id DESC;

    IF @PersonId IS NULL
    BEGIN
        THROW 50001, 'Khong tao/lay duoc person cho admin.', 1;
    END

    -- 2) Upsert user_account
    MERGE dbo.user_account AS target
    USING (
        SELECT
            @AdminEmail AS email,
            @AdminUsername AS username,
            @AdminPassword AS [password],
            CAST('ADMIN' AS NVARCHAR(20)) AS [role],
            @PersonId AS related_id
    ) AS source
    ON target.email = source.email
    WHEN MATCHED THEN
        UPDATE SET
            target.username = source.username,
            target.[password] = source.[password],
            target.[role] = source.[role],
            target.related_id = source.related_id
    WHEN NOT MATCHED THEN
        INSERT (email, username, [password], [role], related_id)
        VALUES (source.email, source.username, source.[password], source.[role], source.related_id);

    COMMIT TRANSACTION;

    SELECT
        ua.user_id,
        ua.email,
        ua.username,
        ua.[role],
        ua.related_id,
        p.full_name,
        p.person_type
    FROM dbo.user_account ua
    LEFT JOIN dbo.person p ON p.id = ua.related_id
    WHERE ua.email = @AdminEmail;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrNum INT = ERROR_NUMBER();
    DECLARE @ErrState INT = ERROR_STATE();
    RAISERROR(@ErrMsg, 16, 1);
END CATCH;