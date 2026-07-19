<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.math.BigDecimal" %>
<%
    request.setAttribute("pageTitle", "Checkout");
    request.setAttribute("activePage", "cart");
    String error = (String) request.getAttribute("error");
    BigDecimal subtotal = (BigDecimal) request.getAttribute("subtotal");
    BigDecimal shipping = (BigDecimal) request.getAttribute("shipping");
    BigDecimal total = (BigDecimal) request.getAttribute("total");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<div class="page-header">
    <h1>Checkout</h1>
</div>

<% if (error != null) { %>
    <div class="msg msg-error"><%= error %></div>
<% } %>

<div class="checkout-layout">
    <div class="card">
        <div class="card-header">Payment Method</div>
        <form action="<%= ctx %>/checkout" method="post">
            <div class="payment-options">
                <div class="payment-option">
                    <input type="radio" name="payment_method" value="FPX" id="fpx" required>
                    <label for="fpx">FPX Online Banking</label>
                </div>
                <div class="payment-option">
                    <input type="radio" name="payment_method" value="DEBIT" id="debit">
                    <label for="debit">Debit Card</label>
                </div>
                <div class="payment-option">
                    <input type="radio" name="payment_method" value="CREDIT" id="credit">
                    <label for="credit">Credit Card</label>
                </div>
            </div>
            <div class="form-actions mt-20">
                <a href="<%= ctx %>/cart" class="btn btn-cancel">Back to Cart</a>
                <button type="submit" class="btn btn-primary">Place Order</button>
            </div>
        </form>
    </div>

    <div class="cart-summary">
        <div class="card-header">Order Summary</div>
        <div class="cart-summary-row">
            <span>Subtotal</span>
            <span>RM <%= String.format("%.2f", subtotal) %></span>
        </div>
        <div class="cart-summary-row">
            <span>Shipping</span>
            <span>RM <%= String.format("%.2f", shipping) %></span>
        </div>
        <div class="cart-summary-total">
            <span>Total</span>
            <span>RM <%= String.format("%.2f", total) %></span>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
