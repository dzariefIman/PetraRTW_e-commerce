<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Order" %>
<%
    request.setAttribute("pageTitle", "Order Details");
    request.setAttribute("activePage", "purchases");
    Order summary = (Order) request.getAttribute("orderSummary");
    List<Order> items = (List<Order>) request.getAttribute("orderItems");
    if (items == null) items = new ArrayList<Order>();
    Map<Integer, Boolean> feedbackMap = (Map<Integer, Boolean>) request.getAttribute("feedbackMap");
    if (feedbackMap == null) feedbackMap = new HashMap<Integer, Boolean>();
    Map<Integer, Boolean> issueMap = (Map<Integer, Boolean>) request.getAttribute("issueMap");
    if (issueMap == null) issueMap = new HashMap<Integer, Boolean>();
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<a href="<%= ctx %>/purchases" class="back-link">&larr; Back to Purchases</a>

<div class="card">
    <div class="card-header">
        Order #<%= summary.getOrderNum() %>
        <% if (summary.getOrderStatus() != null) { %>
            <% String st = summary.getOrderStatus(); %>
            <% String stColor = "PAID".equals(st) || "COMPLETED".equals(st) ? "#1b8b3b" : "CANCELLED".equals(st) ? "#d32f2f" : "AWAITING_PAYMENT".equals(st) ? "#e67e00" : "#666"; %>
            <span style="display:inline-block;padding:2px 8px;border-radius:4px;font-size:11px;font-weight:700;letter-spacing:0.3px;margin-left:8px;background:<%= stColor %>20;color:<%= stColor %>;"><%= st.replace("_", " ") %></span>
        <% } %>
    </div>

    <% boolean canAct = "AWAITING_PAYMENT".equals(summary.getOrderStatus()); %>
    <% boolean isCancelled = "CANCELLED".equals(summary.getOrderStatus()); %>

    <% for (Order item : items) { %>
        <div style="padding:16px 0;border-bottom:1px solid #eee;">
            <div class="flex gap-16" style="flex-wrap:wrap;">
                <div class="flex gap-16 items-center" style="flex:1;min-width:0;">
                    <div class="cart-item-image">
                        <% if (item.getProductImage() != null && !item.getProductImage().isEmpty()) { %>
                            <img src="<%= ctx + "/" + item.getProductImage() %>" alt="item">
                        <% } else { %>
                            <div class="product-image placeholder" style="width:86px;height:86px;font-size:1.5rem;">P</div>
                        <% } %>
                    </div>
                    <div style="flex:1;">
                        <div class="cart-item-title"><%= item.getProductTitle() != null && !item.getProductTitle().isEmpty() ? item.getProductTitle() : item.getItemDesc() %></div>
                        <div class="cart-item-meta">Size: <%= item.getSize() != null ? item.getSize() : "-" %></div>
                        <div class="cart-item-meta">Qty: <%= item.getQuantity() %></div>
                    </div>
                </div>
                <div style="font-weight:700;font-size:16px;white-space:nowrap;">RM <%= String.format("%.2f", item.getItemSubTotal()) %></div>
            </div>
            <% if (!isCancelled && !canAct) { %>
                <div class="mt-10" style="display:flex;gap:8px;">
                    <% Boolean itemFeedback = feedbackMap.get(item.getOrderId()); %>
                    <% if (itemFeedback != null && itemFeedback) { %>
                        <span class="btn btn-sm" style="background:#ccc;color:#fff;cursor:not-allowed;opacity:0.6;">Feedback Given</span>
                    <% } else { %>
                        <a href="<%= ctx %>/feedback?purchase_id=<%= item.getOrderId() %>" class="btn btn-primary btn-sm">Give Feedback</a>
                    <% } %>
                    <a href="<%= ctx %>/complaint?purchase_id=<%= item.getOrderId() %>" class="btn btn-danger btn-sm">Report Issue</a>
                </div>
            <% } %>
        </div>
    <% } %>

    <div class="mt-20" style="border-top:1px solid #eee;padding-top:16px;">
        <div class="card-header">Order Details</div>
        <div class="cart-summary-row"><span>Payment Method</span><span><%= summary.getPaymentMethod() %></span></div>
        <div class="cart-summary-row"><span>Status</span><span style="font-weight:700;"><%= summary.getOrderStatus() != null ? summary.getOrderStatus().replace("_", " ") : "N/A" %></span></div>
        <div class="cart-summary-row"><span>Date</span><span><%= summary.getCreatedTime() %></span></div>
    </div>

    <div class="mt-20" style="border-top:1px solid #eee;padding-top:16px;">
        <div class="card-header">Order Summary</div>
        <div class="cart-summary-row"><span>Shipping Fee</span><span>RM <%= String.format("%.2f", summary.getShippingFee()) %></span></div>
        <% if (summary.getVoucherAmount() != null && summary.getVoucherAmount().compareTo(java.math.BigDecimal.ZERO) > 0) { %>
            <div class="cart-summary-row"><span>Voucher Applied</span><span style="color:#d32f2f;">-RM <%= String.format("%.2f", summary.getVoucherAmount()) %></span></div>
        <% } %>
        <div class="cart-summary-total"><span>Total</span><span>RM <%= String.format("%.2f", summary.getTotalPrice()) %></span></div>
    </div>

    <% if (canAct) { %>
        <div class="mt-20" style="border-top:1px solid #eee;padding-top:16px;display:flex;gap:10px;flex-wrap:wrap;">
            <form action="<%= ctx %>/purchases" method="post" onsubmit="return confirm('Change payment method for this order? You will be redirected to the payment page.');">
                <input type="hidden" name="action" value="change_payment">
                <input type="hidden" name="order_num" value="<%= summary.getOrderNum() %>">
                <button type="submit" class="btn btn-primary">Change Payment</button>
            </form>
            <form action="<%= ctx %>/purchases" method="post" onsubmit="return confirm('Are you sure you want to cancel this order?');">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="order_num" value="<%= summary.getOrderNum() %>">
                <button type="submit" class="btn btn-danger">Cancel Order</button>
            </form>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
