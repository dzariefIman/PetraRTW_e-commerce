<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Customer" %>
<%
    request.setAttribute("pageTitle", "View Customers");
    request.setAttribute("activePage", "customers");
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    if (customers == null) customers = new ArrayList<Customer>();
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    if (totalPages == null) totalPages = 1;
    if (currentPage == null) currentPage = 1;
    String searchId = request.getParameter("customer_id");
    String searchName = request.getParameter("customer_name");
    String searchEmail = request.getParameter("customer_email");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <h2>View Customers</h2>

        <% boolean hasFilters = (searchId != null && !searchId.isEmpty())
                          || (searchName != null && !searchName.isEmpty())
                          || (searchEmail != null && !searchEmail.isEmpty()); %>
        <form method="get" class="search-form">
            <div class="search-row">
                <div class="search-field">
                    <label for="customer_id">Search by ID</label>
                    <input type="text" id="customer_id" name="customer_id" placeholder="Enter customer ID..." value="<%= searchId != null ? searchId : "" %>">
                </div>
                <div class="search-field">
                    <label for="customer_name">Search by Name</label>
                    <input type="text" id="customer_name" name="customer_name" placeholder="Enter customer name..." value="<%= searchName != null ? searchName : "" %>">
                </div>
                <div class="search-field">
                    <label for="customer_email">Search by Email</label>
                    <input type="text" id="customer_email" name="customer_email" placeholder="Enter customer email..." value="<%= searchEmail != null ? searchEmail : "" %>">
                </div>
                <div class="search-actions">
                    <button type="submit" class="btn-search">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                        Search
                    </button>
                    <% if (hasFilters) { %>
                        <a href="<%= ctx %>/staff/customers" class="btn-clear">Clear</a>
                    <% } %>
                </div>
            </div>
        </form>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Customer ID</th>
                        <th>Name</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Address</th>
                        <th>Phone</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Customer c : customers) { %>
                    <tr>
                        <td>CUST<%= c.getCustId() %></td>
                        <td><%= c.getCustName() != null ? c.getCustName() : "" %></td>
                        <td><%= c.getUsername() != null ? c.getUsername() : "" %></td>
                        <td><%= c.getCustEmail() != null ? c.getCustEmail() : "" %></td>
                        <td><%= c.getCustAddress() != null ? c.getCustAddress().replace("\n", "<br>") : "" %></td>
                        <td><%= c.getCustPhoneNum() != null ? c.getCustPhoneNum() : "" %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <% if (customers.isEmpty()) { %>
            <div class="empty-state">No customers found.</div>
        <% } %>

        <% if (totalPages > 1) { %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="<%= ctx %>/staff/customers?p=<%= currentPage - 1 %><%= searchId != null && !searchId.isEmpty() ? "&customer_id=" + searchId : "" %><%= searchName != null && !searchName.isEmpty() ? "&customer_name=" + searchName : "" %><%= searchEmail != null && !searchEmail.isEmpty() ? "&customer_email=" + searchEmail : "" %>">&laquo; Prev</a>
                <% } %>
                <% for (int i = 1; i <= totalPages; i++) { %>
                    <% if (i == currentPage) { %>
                        <strong><%= i %></strong>
                    <% } else { %>
                        <a href="<%= ctx %>/staff/customers?p=<%= i %><%= searchId != null && !searchId.isEmpty() ? "&customer_id=" + searchId : "" %><%= searchName != null && !searchName.isEmpty() ? "&customer_name=" + searchName : "" %><%= searchEmail != null && !searchEmail.isEmpty() ? "&customer_email=" + searchEmail : "" %>"><%= i %></a>
                    <% } %>
                <% } %>
                <% if (currentPage < totalPages) { %>
                    <a href="<%= ctx %>/staff/customers?p=<%= currentPage + 1 %><%= searchId != null && !searchId.isEmpty() ? "&customer_id=" + searchId : "" %><%= searchName != null && !searchName.isEmpty() ? "&customer_name=" + searchName : "" %><%= searchEmail != null && !searchEmail.isEmpty() ? "&customer_email=" + searchEmail : "" %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>
    </main>
</div>
</body>
</html>
