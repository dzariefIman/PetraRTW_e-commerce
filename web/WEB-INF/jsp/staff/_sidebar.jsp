<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) activePage = "";
    String ctx = request.getContextPath();
%>
<nav class="staff-sidebar">
    <div class="sidebar-label">Menu</div>
    <a href="<%= ctx %>/staff/home" class="<%= "dashboard".equals(activePage) ? "active" : "" %>">Home</a>
    <a href="<%= ctx %>/staff/customers" class="<%= "customers".equals(activePage) ? "active" : "" %>">View Customers</a>
    <a href="<%= ctx %>/staff/products" class="<%= "products".equals(activePage) ? "active" : "" %>">Manage Products</a>
    <a href="<%= ctx %>/staff/purchases" class="<%= "purchases".equals(activePage) ? "active" : "" %>">View Purchase History</a>
    <a href="<%= ctx %>/staff/promotions" class="<%= "promotions".equals(activePage) ? "active" : "" %>">Manage Promotion</a>
    <a href="<%= ctx %>/staff/issues" class="<%= "issues".equals(activePage) ? "active" : "" %>">Manage Issues</a>
    <a href="<%= ctx %>/staff/feedback" class="<%= "feedback".equals(activePage) ? "active" : "" %>">View Feedback</a>
</nav>
