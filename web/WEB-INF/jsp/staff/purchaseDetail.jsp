<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Purchase, java.math.BigDecimal" %>
<%
    request.setAttribute("pageTitle", "Purchase Detail");
    request.setAttribute("activePage", "purchases");
    List<Purchase> items = (List<Purchase>) request.getAttribute("items");
    if (items == null) items = new ArrayList<Purchase>();
    Purchase purchase = (Purchase) request.getAttribute("purchase");
    String orderNumber = (String) request.getAttribute("orderNumber");
    BigDecimal itemSubtotal = (BigDecimal) request.getAttribute("itemSubtotal");
    BigDecimal shippingFee = (BigDecimal) request.getAttribute("shippingFee");
    BigDecimal voucherAmount = (BigDecimal) request.getAttribute("voucherAmount");
    BigDecimal totalPrice = (BigDecimal) request.getAttribute("totalPrice");
    int totalQty = request.getAttribute("totalQty") != null ? (Integer) request.getAttribute("totalQty") : 0;
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <a class="back-btn" href="<%= ctx %>/staff/purchases">&lsaquo; BACK</a>

        <div class="detail-header">
            <h1 class="detail-title">Purchase Detail: <%= orderNumber %></h1>
            <a href="<%= ctx %>/purchases/detail?order=<%= orderNumber %>&print=1" target="_blank" class="print-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 6 2 18 2 18 9"/>
                    <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/>
                    <rect x="6" y="14" width="12" height="8"/>
                </svg>
                Print Receipt
            </a>
        </div>

        <div class="detail-grid">
            <div>
                <h2>Items</h2>
                <% for (Purchase it : items) { %>
                    <div class="item-row">
                        <div class="item-image">
                            <% if (it.getProductImage() != null && !it.getProductImage().isEmpty()) { %>
                                <img src="<%= it.getProductImage().startsWith("http") ? it.getProductImage() : ctx + "/" + it.getProductImage() %>" alt="item">
                            <% } else { %>
                                <div class="no-img">No image</div>
                            <% } %>
                        </div>
                        <div class="item-info">
                            <div class="item-title"><%= it.getPurchaseDesc() != null ? it.getPurchaseDesc() : "" %></div>
                            <div class="item-meta">Size: <%= it.getSize() != null ? it.getSize() : "-" %></div>
                            <div class="item-meta">Qty: <%= it.getQuantity() %></div>
                        </div>
                        <div class="item-price">RM <%= String.format("%.2f", it.getItemSubTotal() != null ? it.getItemSubTotal() : BigDecimal.ZERO) %></div>
                    </div>
                <% } %>
            </div>
            <div>
                <h2>Order Summary</h2>
                <div class="summary-row">
                    <div class="summary-labels">
                        <div class="summary-label">Item subtotal</div>
                        <div class="summary-label">Shipping Fee</div>
                        <div class="summary-label">Voucher applied</div>
                        <div class="summary-total">Order Total</div>
                        <div class="summary-label summary-spacer">Payment Method</div>
                        <div class="summary-label">Customer Name</div>
                        <div class="summary-label">Customer Phone</div>
                        <div class="summary-label">Order Number</div>
                        <div class="summary-label">Quantity</div>
                        <div class="summary-label">Date and Time</div>
                    </div>
                    <div>
                        <div class="summary-value">RM <%= String.format("%.2f", itemSubtotal) %></div>
                        <div class="summary-value">RM <%= String.format("%.2f", shippingFee) %></div>
                        <div class="summary-value summary-voucher">-RM <%= String.format("%.2f", voucherAmount) %></div>
                        <div class="summary-total">RM <%= String.format("%.2f", totalPrice) %></div>
                        <div class="summary-value summary-spacer"><%= purchase != null && purchase.getPaymentMethod() != null ? purchase.getPaymentMethod() : "Manual entry" %></div>
                        <div class="summary-value"><%= purchase != null && purchase.getCustomerName() != null ? purchase.getCustomerName() : "" %></div>
                        <div class="summary-value"><%= purchase != null && purchase.getCustomerPhone() != null ? purchase.getCustomerPhone() : "" %></div>
                        <div class="summary-value"><%= orderNumber %></div>
                        <div class="summary-value"><%= totalQty %></div>
                        <div class="summary-value"><%= purchase != null && purchase.getCreatedTime() != null ? purchase.getCreatedTime() : "" %></div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
