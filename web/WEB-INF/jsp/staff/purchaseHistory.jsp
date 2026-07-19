<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Purchase, java.math.BigDecimal, java.net.URLEncoder" %>
<%
    request.setAttribute("pageTitle", "Purchase History");
    request.setAttribute("activePage", "purchases");
    List<Purchase> orders = (List<Purchase>) request.getAttribute("orders");
    if (orders == null) orders = new ArrayList<Purchase>();
    String searchCustomerId = (String) request.getAttribute("searchCustomerId");
    if (searchCustomerId == null) searchCustomerId = "";
    String searchDate = (String) request.getAttribute("searchDate");
    if (searchDate == null) searchDate = "";
    int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
    int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
    int total = request.getAttribute("total") != null ? (Integer) request.getAttribute("total") : 0;
    String flashMsg = (String) request.getAttribute("flashMsg");
    String flashErr = (String) request.getAttribute("flashErr");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <% if (flashMsg != null) { %>
            <div class="alert alert-success"><%= flashMsg %></div>
        <% } %>
        <% if (flashErr != null) { %>
            <div class="alert alert-error"><%= flashErr %></div>
        <% } %>

        <h2>Customer Purchase History</h2>

        <form method="get" action="<%= ctx %>/staff/purchases" class="search-form">
            <div class="search-row">
                <div class="search-field">
                    <label for="customer_id">Search by Customer ID</label>
                    <input type="text" id="customer_id" name="customer_id" placeholder="Enter customer ID..." value="<%= searchCustomerId %>">
                </div>
                <div class="search-field">
                    <label for="purchase_date">Search by Date</label>
                    <input type="date" id="purchase_date" name="purchase_date" value="<%= searchDate %>">
                </div>
                <div class="search-actions">
                    <button type="submit" class="btn-search">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                        Search
                    </button>
                    <% if (!searchCustomerId.isEmpty() || !searchDate.isEmpty()) { %>
                        <a href="<%= ctx %>/staff/purchases" class="btn-clear">Clear</a>
                    <% } %>
                </div>
            </div>
        </form>

        <% if (orders.isEmpty()) { %>
            <div class="empty-state">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <p>No purchase history found</p>
            </div>
        <% } else { %>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Order</th>
                            <th>Customer ID</th>
                            <th>Description</th>
                            <th>Date</th>
                            <th>Total Price</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Purchase o : orders) { %>
                        <tr onclick="location.href='<%= ctx %>/staff/purchases?action=detail&order=<%= URLEncoder.encode(o.getOrderNum(), "UTF-8") %>'">
                            <td><%= o.getOrderNum() %></td>
                            <td>CUST<%= o.getCustId() %></td>
                            <td>
                                <%= o.getFirstItemDesc() != null ? o.getFirstItemDesc() : "" %>
                                <% if (o.getFirstSize() != null && !o.getFirstSize().isEmpty()) { %>
                                    (<%= o.getFirstSize() %>)
                                <% } %>
                                <% if (o.getItemCount() > 1) { %>
                                    +<%= o.getItemCount() - 1 %> more
                                <% } %>
                            </td>
                            <td><%= o.getCreatedTime() != null && o.getCreatedTime().length() >= 10 ? o.getCreatedTime().substring(0, 10) : "" %></td>
                            <td>RM <%= String.format("%.2f", o.getTotalPrice()) %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

        <% if (totalPages > 1) { %>
            <div class="pagination-wrap">
                <nav class="pagination" aria-label="Pagination">
                    <% 
                        String qs = "";
                        if (!searchCustomerId.isEmpty()) qs += "&customer_id=" + URLEncoder.encode(searchCustomerId, "UTF-8");
                        if (!searchDate.isEmpty()) qs += "&purchase_date=" + URLEncoder.encode(searchDate, "UTF-8");
                    %>
                    <% if (currentPage > 1) { %>
                        <a href="<%= ctx %>/staff/purchases?p=<%= currentPage - 1 %><%= qs %>">&laquo; Prev</a>
                    <% } %>
                    <% for (int i = 1; i <= totalPages; i++) { %>
                        <% if (i == currentPage) { %>
                            <strong><%= i %></strong>
                        <% } else { %>
                            <a href="<%= ctx %>/staff/purchases?p=<%= i %><%= qs %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                        <a href="<%= ctx %>/staff/purchases?p=<%= currentPage + 1 %><%= qs %>">Next &raquo;</a>
                    <% } %>
                </nav>
            </div>
        <% } %>
    </main>
</div>
</body>
</html>
