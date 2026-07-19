<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.Staff" %>
<%
    request.setAttribute("pageTitle", "Dashboard");
    request.setAttribute("activePage", "dashboard");
    Staff staff = (Staff) request.getAttribute("staff");
    String name = staff != null ? staff.getStaffName() : "Staff";
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <h2>Welcome, <%= name %></h2>
    </main>
</div>
</body>
</html>
