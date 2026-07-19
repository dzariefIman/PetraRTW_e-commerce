<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Purchase" %>
<%
    request.setAttribute("pageTitle", "Submit Issues");
    request.setAttribute("activePage", "tickets");
    String error = (String) request.getAttribute("error");
    Purchase selectedPurchase = (Purchase) request.getAttribute("selectedPurchase");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<% if (error != null) { %>
    <div class="msg msg-error"><%= error %></div>
<% } %>

<div class="card" style="max-width:600px;margin:0 auto;">
    <form action="<%= ctx %>/complaint" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label>Product</label>
            <div style="padding:10px 12px;border:1px solid #e6e0da;border-radius:6px;background:#f9f8f6;color:#333;">
                <%= selectedPurchase.getProductTitle() != null && !selectedPurchase.getProductTitle().isEmpty() ? selectedPurchase.getProductTitle() : selectedPurchase.getPurchaseDesc() %><%= selectedPurchase.getSize() != null && !selectedPurchase.getSize().isEmpty() ? " (" + selectedPurchase.getSize() + ")" : "" %>
                <div style="font-size:12px;color:#888;margin-top:2px;">Order #<%= selectedPurchase.getOrderNum() %></div>
            </div>
            <input type="hidden" name="purchase_id" value="<%= selectedPurchase.getPurchaseId() %>">
        </div>

        <div class="form-group">
            <label for="title">Complaint Title</label>
            <input type="text" name="title" id="title" class="form-input" placeholder="Short title for the complaint" required>
        </div>

        <div class="form-group">
            <label for="evidence">Upload Image (optional)</label>
            <input type="file" name="evidence" id="evidence" accept="image/*" class="form-input">
        </div>

        <div class="form-group">
            <label for="description">Issue Description</label>
            <textarea name="description" id="description" rows="6" class="form-textarea" placeholder="Describe the issue" required></textarea>
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

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
