<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) activePage = "";
    String ctx = request.getContextPath();
%>
<nav class="staff-sidebar">
    <div class="sidebar-label">Menu</div>
    <a href="<%= ctx %>/salesAdmin/home" class="<%= "dashboard".equals(activePage) ? "active" : "" %>">Home</a>
    <a href="<%= ctx %>/salesAdmin/customers" class="<%= "customers".equals(activePage) ? "active" : "" %>">View Customers</a>
    <a href="<%= ctx %>/salesAdmin/products" class="<%= "products".equals(activePage) ? "active" : "" %>">Manage Products</a>
    <a href="<%= ctx %>/salesAdmin/purchases" class="<%= "purchases".equals(activePage) ? "active" : "" %>">View Purchase History</a>
    <a href="<%= ctx %>/salesAdmin/advertisements" class="<%= "advertisements".equals(activePage) ? "active" : "" %>">Manage Advertisement</a>
    <a href="<%= ctx %>/salesAdmin/issues" class="<%= "issues".equals(activePage) ? "active" : "" %>">Manage Issues</a>
    <a href="<%= ctx %>/salesAdmin/feedback" class="<%= "feedback".equals(activePage) ? "active" : "" %>">View Feedback</a>
</nav>
