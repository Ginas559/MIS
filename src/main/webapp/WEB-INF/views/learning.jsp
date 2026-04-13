    <c:if test="${currentContent == null}">
        <p>ChÆ°a cÃ³ há»c liá»u phÃ¹ há»£p.</p>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="vn.iotstar.coolenglish.entity.AcademicContent" %>
<%@ page import="vn.iotstar.coolenglish.entity.Module" %>
<%!
    private boolean hasChildren(AcademicContent content) {
        if (content == null) {
            return false;
        }
        List<AcademicContent> children = content.getChildren();
        return children != null && !children.isEmpty();
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value == null ? "" : value, "UTF-8");
    }

    private void renderNode(jakarta.servlet.jsp.JspWriter out, AcademicContent node, Long currentContentId,
            String roadmapCode, String contextPath) throws Exception {
        if (node == null) {
            return;
        }

        boolean active = currentContentId != null && currentContentId.equals(node.getId());
        boolean branch = hasChildren(node);

        out.write("<li class='tree-node'>");
        if (branch) {
            out.write("<span class='tree-label tree-branch" + (active ? " active" : "") + "'>" + node.getTitle() + "</span>");
        } else {
            String url = contextPath + "/learning?roadmapCode=" + encode(roadmapCode) + "&title=" + encode(node.getTitle());
            out.write("<a class='tree-label tree-leaf" + (active ? " active" : "") + "' href='" + url + "'>" + node.getTitle() + "</a>");
        }

        if (branch) {
            out.write("<ul class='tree-list'>");
            for (AcademicContent child : node.getChildren()) {
                renderNode(out, child, currentContentId, roadmapCode, contextPath);
            }
            out.write("</ul>");
        }

        out.write("</li>");
    }
%>
    </c:if>
        <c:if test="${nextContent != null}">
            <c:url var="nextLearningUrl" value="/learning">
                <c:param name="title" value="${nextContent.title}" />
                <c:param name="roadmapCode" value="${roadmapCode}" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <style>
        .layout { display: flex; gap: 20px; align-items: flex-start; }
        .sidebar { width: 340px; min-width: 280px; background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 16px; }
        .content { flex: 1; background: #fff; border: 1px solid #dee2e6; border-radius: 8px; padding: 16px; }
        .tree-list { list-style: none; margin: 0; padding-left: 18px; }
        .tree-node { margin: 6px 0; }
        .tree-label { display: inline-block; color: #212529; text-decoration: none; }
        .tree-branch { font-weight: 600; }
        .tree-leaf:hover { text-decoration: underline; }
        .active { font-weight: 700; color: #0d6efd; }
    </style>
            </c:url>
<body class="p-3">
    <h2>Vu tru Hoc lieu Thong minh</h2>
        </c:if>
    </c:if>
    <div class="layout">
        <aside class="sidebar">
            <h5 class="mb-3">Lo trinh hoc tap</h5>
            <%
                Module rootModule = (Module) request.getAttribute("rootModule");
                Long currentContentId = (Long) request.getAttribute("currentContentId");
                String roadmapCode = (String) request.getAttribute("roadmapCode");
                String contextPath = request.getContextPath();
                if (rootModule != null) {
                    out.write("<ul class='tree-list'>");
                    renderNode(out, rootModule, currentContentId, roadmapCode, contextPath);
                    out.write("</ul>");
                } else {
                    out.write("<p class='text-muted'>Chua co du lieu roadmap.</p>");
                }
            %>
        </aside>

        <main class="content">
            <c:if test="${currentContent != null}">
                <h3>Bai hoc hien tai: ${currentContent.title}</h3>
                <pre>${renderedContent}</pre>
    <c:if test="${currentContent != null}">
                <c:if test="${premiumLocked}">
                    <p style="color: red;">Ban chua duoc cap quyen vao roadmap nay. Vui long lien he Admin/Staff.</p>
                </c:if>
    <h2>VÅ© trá»¥ Há»c liá»u ThÃ´ng minh</h2>
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
                <p>Chua co hoc lieu phu hop.</p>
            </c:if>
        </main>
    </div>
        <h3>BÃ i há»c hiá»n táº¡i: ${currentContent.title}</h3>
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
        <p>ChÆ°a cÃ³ há»c liá»u phÃ¹ há»£p.</p>
    </c:if>
</body>
</html>

