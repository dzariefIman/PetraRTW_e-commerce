<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Issue, java.net.URLEncoder" %>
<%
    request.setAttribute("pageTitle", "Issues");
    request.setAttribute("activePage", "issues");
    List<Issue> issues = (List<Issue>) request.getAttribute("issues");
    if (issues == null) issues = new ArrayList<Issue>();
    String filterTicket = request.getParameter("ticket_number");
    String filterName = request.getParameter("customer_name");
    String filterStatus = request.getParameter("status");
    int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
    int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
    int total = request.getAttribute("total") != null ? (Integer) request.getAttribute("total") : 0;
    String ctx = request.getContextPath();
    boolean hasFilters = (filterTicket != null && !filterTicket.isEmpty())
                      || (filterName != null && !filterName.isEmpty())
                      || (filterStatus != null && !filterStatus.isEmpty());
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <h2>Customer Issues</h2>

        <form method="get" class="search-form">
            <div class="search-row">
                <div class="search-field">
                    <label for="ticket_number">Search by Ticket Number</label>
                    <input type="text" id="ticket_number" name="ticket_number" placeholder="Enter ticket number..." value="<%= filterTicket != null ? filterTicket : "" %>">
                </div>
                <div class="search-field">
                    <label for="customer_name">Search by Customer Name</label>
                    <input type="text" id="customer_name" name="customer_name" placeholder="Enter customer name..." value="<%= filterName != null ? filterName : "" %>">
                </div>
                <div class="search-field">
                    <label for="status">Filter by Status</label>
                    <select id="status" name="status">
                        <option value="">All Status</option>
                        <option value="New" <%= "New".equals(filterStatus) ? "selected" : "" %>>New</option>
                        <option value="Processing" <%= "Processing".equals(filterStatus) ? "selected" : "" %>>Processing</option>
                        <option value="Pending" <%= "Pending".equals(filterStatus) ? "selected" : "" %>>Pending</option>
                        <option value="Closed" <%= "Closed".equals(filterStatus) ? "selected" : "" %>>Closed</option>
                    </select>
                </div>
                <div class="search-actions">
                    <button type="submit" class="btn-search">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                        Search
                    </button>
                    <% if (hasFilters) { %>
                        <a href="<%= ctx %>/staff/issues" class="btn-clear">Clear</a>
                    <% } %>
                </div>
            </div>
        </form>

        <% if (issues.isEmpty()) { %>
            <div class="empty-state">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <p>No issues found</p>
            </div>
        <% } else { %>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Ticket Number</th>
                            <th>Opening Date</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th>Details</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Issue i : issues) { %>
                        <tr>
                            <td><%= i.getTicketNum() %></td>
                            <td><%= i.getCreatedTime() != null && i.getCreatedTime().length() >= 10 ? i.getCreatedTime().substring(0, 10) : "" %></td>
                            <td><%= i.getCustomerName() != null ? i.getCustomerName() : "ID: " + i.getCustId() %></td>
                            <td><span class="status-indicator"><span class="status-dot <%= i.getStatus() != null ? i.getStatus() : "New" %>"></span><%= i.getStatus() != null ? i.getStatus() : "New" %></span></td>
                            <td><%= i.getTitle() != null ? i.getTitle() : "" %></td>
                            <td>
                                <a href="<%= ctx %>/staff/issues/detail?id=<%= i.getIssueId() %>" class="btn-action">Edit</a>
                            </td>
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
                        StringBuilder qs = new StringBuilder();
                        if (filterTicket != null && !filterTicket.isEmpty())
                            qs.append("&ticket_number=").append(URLEncoder.encode(filterTicket, "UTF-8"));
                        if (filterName != null && !filterName.isEmpty())
                            qs.append("&customer_name=").append(URLEncoder.encode(filterName, "UTF-8"));
                        if (filterStatus != null && !filterStatus.isEmpty())
                            qs.append("&status=").append(URLEncoder.encode(filterStatus, "UTF-8"));
                        String qsStr = qs.toString();
                    %>
                    <% if (currentPage > 1) { %>
                        <a href="<%= ctx %>/staff/issues?p=<%= currentPage - 1 %><%= qsStr %>">&laquo; Prev</a>
                    <% } %>
                    <% for (int i = 1; i <= totalPages; i++) { %>
                        <% if (i == currentPage) { %>
                            <strong><%= i %></strong>
                        <% } else { %>
                            <a href="<%= ctx %>/staff/issues?p=<%= i %><%= qsStr %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                        <a href="<%= ctx %>/staff/issues?p=<%= currentPage + 1 %><%= qsStr %>">Next &raquo;</a>
                    <% } %>
                </nav>
            </div>
        <% } %>
    </main>
</div>
</body>
</html>