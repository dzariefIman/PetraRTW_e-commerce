<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Order" %>
<%
    request.setAttribute("pageTitle", "My Purchases");
    request.setAttribute("activePage", "purchases");
    List<Order> purchases = (List<Order>) request.getAttribute("purchases");
    if (purchases == null) purchases = new ArrayList<Order>();
    String sort = (String) request.getAttribute("sort");
    if (sort == null) sort = "newest";
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<div class="page-header">
    <h1>My Purchases</h1>
</div>

<div class="container">
    <div class="purchase-toolbar">
        <span class="purchase-toolbar-label">Sort by</span>
        <a href="<%= ctx %>/purchases?sort=newest" class="sort-btn<%= "newest".equals(sort) ? " active" : "" %>">Newest</a>
        <a href="<%= ctx %>/purchases?sort=oldest" class="sort-btn<%= "oldest".equals(sort) ? " active" : "" %>">Oldest</a>
    </div>

    <% if (purchases.isEmpty()) { %>
        <div class="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>
            </svg>
            <h3>No Purchases Yet</h3>
            <p>Start shopping to see your orders here!</p>
            <a href="<%= ctx %>/home" class="btn btn-primary mt-15">Start Shopping</a>
        </div>
    <% } else { %>
        <div class="purchase-grid">
            <% for (Order p : purchases) { %>
                <a href="<%= ctx %>/purchases/detail?order=<%= p.getOrderNum() %>" class="purchase-card">
                    <div class="purchase-card-body">
                        <div class="purchase-card-title">Order #<%= p.getOrderNum() %></div>
                        <% if (p.getOrderStatus() != null) { %>
                            <% String st = p.getOrderStatus(); %>
                            <% String stColor = "PAID".equals(st) || "COMPLETED".equals(st) ? "#1b8b3b" : "CANCELLED".equals(st) ? "#d32f2f" : "AWAITING_PAYMENT".equals(st) ? "#e67e00" : "#666"; %>
                            <div style="display:inline-block;padding:2px 8px;border-radius:4px;font-size:11px;font-weight:700;letter-spacing:0.3px;margin-bottom:4px;background:<%= stColor %>20;color:<%= stColor %>;"><%= st.replace("_", " ") %></div>
                        <% } %>
                        <div class="purchase-card-desc"><%= p.getFirstItemDesc() != null ? p.getFirstItemDesc() : "" %></div>
                        <div class="purchase-card-meta"><%= p.getItemCount() %> item(s) &middot; <%= p.getTotalQty() %> qty</div>
                        <div class="purchase-card-price">RM <%= String.format("%.2f", p.getTotalPrice()) %></div>
                        <div class="purchase-card-date"><%= p.getCreatedTime() %></div>
                    </div>
                </a>
            <% } %>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
