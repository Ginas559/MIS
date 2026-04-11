<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Learning Space</title>
</head>
<body>
    <h2>Vũ trụ Học liệu Thông minh</h2>

    <p><strong>Roadmap:</strong> ${not empty roadmap ? roadmap.title : roadmapCode}</p>

    <c:if test="${currentContent != null}">
        <h3>Bài học hiện tại: ${currentContent.title}</h3>
        <pre>${renderedContent}</pre>

        <c:if test="${premiumLocked}">
            <p style="color: red;">Ban chua duoc cap quyen vao roadmap nay. Vui long lien he Admin/Staff.</p>
        </c:if>

        <c:if test="${nextContent != null}">
            <c:url var="nextLearningUrl" value="/learning">
                <c:param name="title" value="${nextContent.title}" />
                <c:param name="roadmapCode" value="${roadmapCode}" />
            </c:url>
            <p>
                <a href="${nextLearningUrl}">Bai tiep theo: ${nextContent.title}</a>
            </p>
        </c:if>
    </c:if>

    <c:if test="${currentContent == null}">
        <p>Chưa có học liệu phù hợp.</p>
    </c:if>
</body>
</html>

