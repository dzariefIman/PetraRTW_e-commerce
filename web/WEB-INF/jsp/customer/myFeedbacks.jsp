<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Feedback" %>
<%
    request.setAttribute("pageTitle", "My Reviews");
    request.setAttribute("activePage", "purchases");
    List<Feedback> myFeedbacks = (List<Feedback>) request.getAttribute("myFeedbacks");
    if (myFeedbacks == null) myFeedbacks = new ArrayList<Feedback>();
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<div class="page-header">
    <h1>My Reviews</h1>
</div>

<div class="text-center mb-20">
    <a href="<%= ctx %>/feedback" class="btn btn-primary btn-sm">Write New Review</a>
</div>

<% if (myFeedbacks.isEmpty()) { %>
    <div class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
        </svg>
        <h3>No Reviews Yet</h3>
        <p>Write your first review after making a purchase!</p>
    </div>
<% } else { %>
    <div class="feedback-grid">
        <% for (Feedback f : myFeedbacks) { %>
            <div class="feedback-card">
                <div class="feedback-title"><%= f.getProductName() != null ? f.getProductName() : "Product" %></div>
                <div class="star-display">
                    <% for (int i = 1; i <= 5; i++) { %>
                        <span class="star<%= i <= f.getRating() ? " filled" : "" %>">&#9733;</span>
                    <% } %>
                </div>
                <div class="feedback-text"><%= f.getFeedbackText() %></div>
                <% if (f.getProductImage() != null && !f.getProductImage().isEmpty()) { %>
                    <img src="<%= ctx %>/<%= f.getProductImage() %>" alt="Review photo" style="max-width:150px;border-radius:8px;margin-top:8px;">
                <% } %>
                <div class="feedback-footer">
                    <div class="feedback-date"><%= f.getCreatedTime() %></div>
                </div>
            </div>
        <% } %>
    </div>
<% } %>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
