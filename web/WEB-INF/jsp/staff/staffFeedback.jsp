<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Feedback" %>
<%
    request.setAttribute("pageTitle", "View Feedback");
    request.setAttribute("activePage", "feedback");
    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks");
    if (feedbacks == null) feedbacks = new ArrayList<Feedback>();
    int currentPage = ((Integer) request.getAttribute("currentPage")).intValue();
    int totalPages = ((Integer) request.getAttribute("totalPages")).intValue();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <h1>Customer Feedback</h1>

        <% if (feedbacks.isEmpty()) { %>
            <div class="empty-state">No feedback yet.</div>
        <% } else { %>
            <% for (Feedback fb : feedbacks) { %>
                <div class="feedback-card">
                    <div class="feedback-avatar">
                        <%= fb.getCustomerName() != null && !fb.getCustomerName().isEmpty() ? fb.getCustomerName().substring(0,1).toUpperCase() : "A" %>
                    </div>
                    <div class="feedback-body">
                        <div class="feedback-meta">
                            <div>
                                <div class="feedback-product"><%= fb.getProductName() != null ? fb.getProductName() : "No item" %></div>
                                <div class="feedback-customer"><%= fb.getCustomerName() != null ? fb.getCustomerName() : "Anonymous" %></div>
                                <div class="mt-10">
                                    <% for (int i = 1; i <= 5; i++) { %>
                                        <span class="star <%= i <= fb.getRating() ? "star-filled" : "star-empty" %>">★</span>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <div class="feedback-text"><%= fb.getFeedbackText() %></div>
                    </div>
                    <% if (fb.getProductImage() != null && !fb.getProductImage().isEmpty()) { %>
                        <div class="feedback-image" onclick="openImageModal('<%= request.getContextPath() %>/<%= fb.getProductImage() %>')">
                            <img src="<%= request.getContextPath() %>/<%= fb.getProductImage() %>" alt="feedback image">
                        </div>
                    <% } else { %>
                        <div class="feedback-no-image">No image</div>
                    <% } %>
                </div>
            <% } %>

            <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a href="?p=1">&laquo;&laquo;</a>
                        <a href="?p=<%= currentPage - 1 %>">&laquo;</a>
                    <% } %>
                    <% for (int i = Math.max(1, currentPage - 1); i <= Math.min(totalPages, currentPage + 1); i++) { %>
                        <% if (i == currentPage) { %>
                            <strong><%= i %></strong>
                        <% } else { %>
                            <a href="?p=<%= i %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                        <a href="?p=<%= currentPage + 1 %>">&raquo;</a>
                        <a href="?p=<%= totalPages %>">&raquo;&raquo;</a>
                    <% } %>
                </div>
            <% } %>
        <% } %>
    </main>
</div>

<div class="modal-overlay" id="imageModal" onclick="if(event.target===this)closeImageModal()">
    <div class="modal-content">
        <button class="modal-close" onclick="closeImageModal()">&times;</button>
        <img id="staffModalImage" src="" alt="preview" class="modal-img">
    </div>
</div>

<script>
    function openImageModal(src) {
        document.getElementById('staffModalImage').src = src;
        document.getElementById('imageModal').style.display = 'flex';
    }
    function closeImageModal() {
        document.getElementById('imageModal').style.display = 'none';
    }
</script>
</body>
</html>
