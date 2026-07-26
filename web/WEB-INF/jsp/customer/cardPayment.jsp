<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.Payment, petra.model.Order"%>
<%
    Payment payment = (Payment) request.getAttribute("payment");
    Order order = (Order) request.getAttribute("order");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
    String method = payment != null ? payment.getPaymentMethod() : "";
    String cardLabel = "CREDIT".equals(method) ? "Credit Card" : "Debit Card";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= cardLabel %> Payment - Secure Checkout</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f4f8; min-height: 100vh; display: flex; flex-direction: column; align-items: center; }
        .bank-header { width: 100%; background: #2c3e50; color: #fff; padding: 16px 24px; text-align: center; }
        .bank-header h1 { font-size: 20px; font-weight: 600; letter-spacing: 1px; }
        .bank-header p { font-size: 12px; opacity: 0.8; margin-top: 4px; }
        .container { width: 100%; max-width: 460px; padding: 24px 16px; flex: 1; }
        .card-box { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); padding: 24px; margin-bottom: 16px; }
        .card-box h2 { font-size: 16px; color: #333; margin-bottom: 16px; }
        .method-badge { display: inline-block; background: #2c3e50; color: #fff; padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; margin-bottom: 16px; letter-spacing: 0.5px; }
        .order-info { background: #f8f9fa; border-radius: 6px; padding: 14px; margin-bottom: 16px; }
        .order-info .row { display: flex; justify-content: space-between; font-size: 14px; color: #555; margin-bottom: 6px; }
        .order-info .row:last-child { margin-bottom: 0; }
        .order-info .row span:last-child { font-weight: 700; color: #2c3e50; }
        .form-group { margin-bottom: 14px; }
        .form-group label { display: block; font-size: 13px; font-weight: 600; color: #444; margin-bottom: 6px; }
        .form-group input { width: 100%; padding: 10px 12px; border: 1px solid #ccc; border-radius: 6px; font-size: 14px; background: #fff; box-sizing: border-box; }
        .form-group input:focus { outline: none; border-color: #2c3e50; }
        .form-row { display: flex; gap: 12px; }
        .form-row .form-group { flex: 1; }
        .hint { font-size: 11px; color: #999; margin-top: 4px; }
        .btn { display: block; width: 100%; padding: 12px; border: none; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; text-align: center; margin-bottom: 10px; text-decoration: none; }
        .btn-primary { background: #27ae60; color: #fff; }
        .btn-primary:hover { background: #219a52; }
        .btn-cancel { background: #e74c3c; color: #fff; }
        .btn-cancel:hover { background: #c0392b; }
        .btn-link { background: none; color: #2c3e50; font-size: 13px; font-weight: 400; }
        .btn-link:hover { text-decoration: underline; }
        .error { background: #fdecea; color: #c0392b; padding: 10px 14px; border-radius: 6px; font-size: 13px; margin-bottom: 14px; }
        .footer { width: 100%; text-align: center; padding: 14px; font-size: 11px; color: #999; }
    </style>
</head>
<body>
    <div class="bank-header">
        <h1>SECURE CARD PAYMENT</h1>
        <p>256-bit SSL Encrypted Transaction</p>
    </div>

    <div class="container">
        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>

        <div class="card-box">
            <div class="method-badge"><%= cardLabel %></div>
            <h2>Enter Card Details</h2>

            <form action="<%= ctx %>/payment/gateway" method="post">
                <input type="hidden" name="payment_id" value="<%= payment.getPaymentId() %>">

                <div class="form-group">
                    <label>Card Number</label>
                    <input type="text" name="card_number" placeholder="1234 5678 9012 3456" maxlength="19" required autocomplete="cc-number">
                    <div class="hint">Test: 4242 4242 4242 4242 (success) or 4000 0000 0000 0002 (decline)</div>
                </div>

                <div class="form-group">
                    <label>Cardholder Name</label>
                    <input type="text" name="cardholder_name" placeholder="JOHN DOE" required autocomplete="cc-name">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Expiry (MM/YY)</label>
                        <input type="text" name="expiry" placeholder="MM/YY" maxlength="5" required autocomplete="cc-exp">
                    </div>
                    <div class="form-group">
                        <label>CVV</label>
                        <input type="password" name="cvv" placeholder="***" maxlength="3" required autocomplete="cc-csc">
                        <div class="hint">3 digits on back of card</div>
                    </div>
                </div>

                <div class="order-info">
                    <div class="row"><span>Order Number</span><span>#<%= order != null ? order.getOrderNum() : "" %></span></div>
                    <div class="row"><span>Amount to Pay</span><span>RM <%= payment.getAmount() %></span></div>
                </div>

                <button type="submit" name="action" value="pay" class="btn btn-primary">Pay Now</button>
            </form>
            <a href="<%= ctx %>/purchases/detail?order=<%= order != null ? order.getOrderNum() : "" %>" class="btn btn-cancel" style="margin-top:0;">Cancel Transaction</a>
        </div>
    </div>

    <div class="footer">Secured by 256-bit SSL Encryption &middot; Your card details are safe with us</div>
</body>
</html>
