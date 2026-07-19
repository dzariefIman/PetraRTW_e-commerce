<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Purchase, petra.model.Feedback" %>
<%
    request.setAttribute("pageTitle", "Give Feedback");
    request.setAttribute("activePage", "purchases");
    String error = (String) request.getAttribute("error");
    Purchase selectedPurchase = (Purchase) request.getAttribute("selectedPurchase");
    Feedback existingFeedback = (Feedback) request.getAttribute("existingFeedback");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<% if (error != null) { %>
    <div class="msg msg-error"><%= error %></div>
<% } %>

<div class="card" style="max-width:600px;margin:0 auto;">
    <form action="<%= ctx %>/feedback" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label>Product</label>
            <div style="padding:10px 12px;border:1px solid #e6e0da;border-radius:6px;background:#f9f8f6;color:#333;">
                <%= selectedPurchase.getProductTitle() != null && !selectedPurchase.getProductTitle().isEmpty() ? selectedPurchase.getProductTitle() : selectedPurchase.getPurchaseDesc() %><%= selectedPurchase.getSize() != null && !selectedPurchase.getSize().isEmpty() ? " (" + selectedPurchase.getSize() + ")" : "" %>
                <div style="font-size:12px;color:#888;margin-top:2px;">Order #<%= selectedPurchase.getOrderNum() %></div>
            </div>
            <input type="hidden" name="purchase_id" value="<%= selectedPurchase.getPurchaseId() %>">
        </div>

        <div class="form-group">
            <label>Rating</label>
            <input type="hidden" name="rating" id="ratingValue" value="<%= existingFeedback != null ? existingFeedback.getRating() : 0 %>" required>
            <div id="starRating" class="star-display" style="font-size:2rem;margin:8px 0;">
                <span class="star<%= existingFeedback != null && existingFeedback.getRating() >= 1 ? " filled" : "" %>" data-value="1" style="cursor:pointer;">&#9733;</span>
                <span class="star<%= existingFeedback != null && existingFeedback.getRating() >= 2 ? " filled" : "" %>" data-value="2" style="cursor:pointer;">&#9733;</span>
                <span class="star<%= existingFeedback != null && existingFeedback.getRating() >= 3 ? " filled" : "" %>" data-value="3" style="cursor:pointer;">&#9733;</span>
                <span class="star<%= existingFeedback != null && existingFeedback.getRating() >= 4 ? " filled" : "" %>" data-value="4" style="cursor:pointer;">&#9733;</span>
                <span class="star<%= existingFeedback != null && existingFeedback.getRating() >= 5 ? " filled" : "" %>" data-value="5" style="cursor:pointer;">&#9733;</span>
            </div>
            <small class="text-muted">Click to rate (1-5 stars)</small>
        </div>

        <div class="form-group">
            <label for="image">Upload Image (optional)</label>
            <input type="file" name="image" id="image" accept="image/*" class="form-input">
        </div>

        <div class="form-group">
            <label for="feedback_text">Feedback</label>
            <textarea name="feedback_text" id="feedback_text" rows="6" class="form-textarea" placeholder="Your feedback description here" required><%= existingFeedback != null && existingFeedback.getFeedbackText() != null ? existingFeedback.getFeedbackText() : "" %></textarea>
        </div>

        <div class="form-group flex items-center gap-8">
            <input type="checkbox" id="agree" required>
            <label for="agree" style="font-size:13px;font-weight:400;">I accept the terms</label>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Submit</button>
        </div>
    </form>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function(){
        var stars = document.querySelectorAll('.star');
        var ratingValue = document.getElementById('ratingValue');
        stars.forEach(function(star) {
            star.addEventListener('click', function() {
                var value = parseInt(this.getAttribute('data-value'));
                ratingValue.value = value;
                stars.forEach(function(s, i) {
                    if (i < value) { s.classList.add('filled'); } else { s.classList.remove('filled'); }
                });
            });
            star.addEventListener('mouseenter', function() {
                var value = parseInt(this.getAttribute('data-value'));
                stars.forEach(function(s, i) {
                    if (i < value) { s.classList.add('filled'); } else { s.classList.remove('filled'); }
                });
            });
        });
        document.getElementById('starRating').addEventListener('mouseleave', function() {
            var value = parseInt(ratingValue.value);
            stars.forEach(function(s, i) {
                if (i < value) { s.classList.add('filled'); } else { s.classList.remove('filled'); }
            });
        });
    });
</script>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
