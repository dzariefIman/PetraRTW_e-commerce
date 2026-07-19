<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Issue" %>
<%
    request.setAttribute("pageTitle", "My Tickets");
    request.setAttribute("activePage", "tickets");
    List<Issue> tickets = (List<Issue>) request.getAttribute("tickets");
    if (tickets == null) tickets = new ArrayList<Issue>();
    String searchTicket = request.getParameter("ticket_number");
    String searchStatus = request.getParameter("status");
    if (searchTicket == null) searchTicket = "";
    if (searchStatus == null) searchStatus = "";
    String ctx = request.getContextPath();
    boolean hasFilters = !searchTicket.isEmpty() || !searchStatus.isEmpty();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<div class="page-header">
    <h1>My Tickets</h1>
    <p><%= tickets.size() %> ticket(s) found</p>
</div>

<form method="get" class="search-form">
    <div class="search-row">
        <div class="search-field">
            <label for="ticket_number">Search by Ticket Number</label>
            <input type="text" id="ticket_number" name="ticket_number" placeholder="Enter ticket number..." value="<%= searchTicket %>">
        </div>
        <div class="search-field">
            <label for="status">Filter by Status</label>
            <select id="status" name="status">
                <option value="">All Statuses</option>
                <option value="New" <%= "New".equals(searchStatus) ? "selected" : "" %>>New</option>
                <option value="Pending" <%= "Pending".equals(searchStatus) ? "selected" : "" %>>Pending</option>
                <option value="Processing" <%= "Processing".equals(searchStatus) ? "selected" : "" %>>Processing</option>
                <option value="Closed" <%= "Closed".equals(searchStatus) ? "selected" : "" %>>Closed</option>
            </select>
        </div>
        <div class="search-actions">
            <button type="submit" class="btn-search">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                Search
            </button>
            <% if (hasFilters) { %>
                <a href="<%= ctx %>/tickets" class="btn-clear">Clear</a>
            <% } %>
        </div>
    </div>
</form>

<% if (tickets.isEmpty()) { %>
    <div class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
        </svg>
        <h3>No Tickets Yet</h3>
        <p>Create a ticket to report an issue.</p>
        <a href="<%= ctx %>/complaint" class="btn btn-primary mt-15">Create Ticket</a>
    </div>
<% } else { %>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Opening Date</th>
                    <th>Status</th>
                    <th>Last Update</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (Issue t : tickets) { 
                        String openDate = t.getCreatedTime() != null && t.getCreatedTime().length() >= 16 ? t.getCreatedTime().substring(0, 16) : (t.getCreatedTime() != null ? t.getCreatedTime() : "");
                        String lastUpd = t.getLastUpdate() != null && t.getLastUpdate().length() >= 16 ? t.getLastUpdate().substring(0, 16) : (t.getLastUpdate() != null ? t.getLastUpdate() : "");
                %>
                    <tr>
                        <td>
                            <a href="<%= ctx %>/tickets/detail?id=<%= t.getIssueId() %>" style="color:#5a3913;font-weight:600;text-decoration:none;"><%= t.getTitle() %></a>
                            <small class="text-muted" style="display:block;">Ticket #<%= t.getTicketNum() != null ? t.getTicketNum() : t.getIssueId() %></small>
                        </td>
                        <td><%= openDate %></td>
                        <td>
                            <span class="badge badge-<%= t.getStatus() != null ? t.getStatus().toLowerCase() : "new" %>">
                                <%= t.getStatus() != null ? t.getStatus() : "New" %>
                            </span>
                        </td>
                        <td><%= lastUpd %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
<% } %>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
