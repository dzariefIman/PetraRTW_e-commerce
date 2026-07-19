<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.dao.CartDAO"%>
<%
    String activePage = request.getAttribute("activePage") != null ? (String) request.getAttribute("activePage") : "";
    String ctx = request.getContextPath();
    int cartCount = 0;
    Object userIdObj = session.getAttribute("userId");
    if (userIdObj != null) {
        try {
            int custId = (Integer) userIdObj;
            cartCount = new CartDAO().getItemCount(custId);
        } catch (Exception e) {}
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Petra" %></title>
    <link rel="stylesheet" href="<%= ctx %>/customer/style.css">
</head>
<body>
    <header class="topbar">
        <a class="brand" href="<%= ctx %>/home">PETRA</a>
        <nav class="nav">
            <a class="pill<%= "home".equals(activePage) ? " active" : "" %>" href="<%= ctx %>/home">HOME</a>
            <a class="pill<%= "promotions".equals(activePage) ? " active" : "" %>" href="<%= ctx %>/promotions">PROMOTION</a>
            <a class="pill<%= "purchases".equals(activePage) ? " active" : "" %>" href="<%= ctx %>/purchases">PURCHASE</a>
            <a class="pill<%= "tickets".equals(activePage) ? " active" : "" %>" href="<%= ctx %>/tickets">TICKETS</a>
            <div class="right">
                <a class="round-btn cart-link" href="<%= ctx %>/cart" aria-label="Shopping cart">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                    <% if (cartCount > 0) { %>
                        <span class="cart-badge"><%= cartCount %></span>
                    <% } %>
                </a>
                <a class="pill logout" href="<%= ctx %>/logout">Log out</a>
            </div>
        </nav>
    </header>
    <main>
        <div class="container">