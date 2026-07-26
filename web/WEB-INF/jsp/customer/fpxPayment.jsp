<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.Payment, petra.model.Order"%>
<%
    Payment payment = (Payment) request.getAttribute("payment");
    Order order = (Order) request.getAttribute("order");
    String error = (String) request.getAttribute("error");
    String ctx = request.getContextPath();
    String bankCode = payment != null && payment.getGatewayRef() != null ? payment.getGatewayRef() : "";
    String bankName = bankCode.replace("_", " ");
    if (!bankName.isEmpty()) {
        bankName = bankName.substring(0, 1).toUpperCase() + bankName.substring(1).toLowerCase();
    }
    String bankColor = "#1a5276";
    if (bankCode.contains("MAYBANK")) bankColor = "#FFD700";
    else if (bankCode.contains("CIMB")) bankColor = "#E31937";
    else if (bankCode.contains("PUBLIC")) bankColor = "#003DA5";
    else if (bankCode.contains("RHB")) bankColor = "#C4122E";
    else if (bankCode.contains("HONG")) bankColor = "#005EB8";
    else if (bankCode.contains("AMBANK")) bankColor = "#ED1C24";
    else if (bankCode.contains("BSN")) bankColor = "#006B3F";
    else if (bankCode.contains("ISLAM")) bankColor = "#008751";
    else if (bankCode.contains("RAKYAT")) bankColor = "#004B87";
    else if (bankCode.contains("AFFIN")) bankColor = "#00205B";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= bankName %> - Secure Login</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #e8ecef; min-height: 100vh; display: flex; flex-direction: column; align-items: center; }
        .bank-bar { width: 100%; background: <%= bankColor %>; padding: 12px 24px; display: flex; align-items: center; justify-content: space-between; }
        .bank-bar .bank-title { color: #fff; font-size: 18px; font-weight: 700; letter-spacing: 1px; }
        .bank-bar .secure { color: rgba(255,255,255,0.85); font-size: 11px; display: flex; align-items: center; gap: 5px; }
        .bank-bar .secure::before { content: "\1F512"; font-size: 12px; }
        .fpx-bar { width: 100%; background: #333; color: #fff; padding: 8px 24px; font-size: 12px; display: flex; justify-content: space-between; }
        .container { width: 100%; max-width: 420px; padding: 30px 16px; flex: 1; }
        .login-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); overflow: hidden; }
        .login-header { background: <%= bankColor %>; padding: 24px; text-align: center; }
        .login-header h1 { color: #fff; font-size: 22px; font-weight: 700; }
        .login-header p { color: rgba(255,255,255,0.8); font-size: 13px; margin-top: 6px; }
        .login-body { padding: 28px 24px; }
        .form-group { margin-bottom: 18px; }
        .form-group label { display: block; font-size: 13px; font-weight: 600; color: #444; margin-bottom: 6px; }
        .form-group input { width: 100%; padding: 12px 14px; border: 1px solid #ccc; border-radius: 6px; font-size: 15px; background: #f9f9f9; }
        .form-group input:focus { outline: none; border-color: <%= bankColor %>; background: #fff; }
        .payment-summary { background: #f0f4f8; border: 1px solid #dce3ea; border-radius: 6px; padding: 14px; margin-bottom: 20px; }
        .payment-summary .row { display: flex; justify-content: space-between; font-size: 13px; color: #555; margin-bottom: 5px; }
        .payment-summary .row:last-child { margin-bottom: 0; }
        .payment-summary .row span:last-child { font-weight: 700; color: #222; }
        .btn { display: block; width: 100%; padding: 13px; border: none; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; text-align: center; margin-bottom: 10px; text-decoration: none; }
        .btn-pay { background: <%= bankColor %>; color: #fff; }
        .btn-pay:hover { opacity: 0.9; }
        .btn-cancel { background: #e74c3c; color: #fff; }
        .btn-cancel:hover { background: #c0392b; }
        .btn-back { background: none; color: #666; font-size: 13px; font-weight: 400; border: 1px solid #ccc; }
        .btn-back:hover { background: #f5f5f5; }
        .error { background: #fdecea; color: #c0392b; padding: 10px 14px; border-radius: 6px; font-size: 13px; margin-bottom: 14px; border: 1px solid #f5c6cb; }
        .footer { width: 100%; text-align: center; padding: 14px; font-size: 11px; color: #999; background: #fff; border-top: 1px solid #eee; }
    </style>
</head>
<body>
    <div class="bank-bar">
        <div class="bank-title"><%= bankName %></div>
        <div class="secure">Secure Connection</div>
    </div>
    <div class="fpx-bar">
        <span>FPX Online Banking</span>
        <span>Order #<%= order != null ? order.getOrderNum() : "" %></span>
    </div>

    <div class="container">
        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>

        <div class="login-card">
            <div class="login-header">
                <h1><%= bankName %></h1>
                <p>Online Banking Login</p>
            </div>
            <div class="login-body">
                <form action="<%= ctx %>/payment/gateway" method="post">
                    <input type="hidden" name="payment_id" value="<%= payment.getPaymentId() %>">
                    <input type="hidden" name="bank" value="<%= bankCode %>">

                    <div class="form-group">
                        <label>Username / Customer ID</label>
                        <input type="text" name="fpx_username" placeholder="Enter your username" required autocomplete="off">
                    </div>

                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" name="fpx_password" placeholder="Enter your password" required>
                    </div>

                    <div class="payment-summary">
                        <div class="row"><span>Order</span><span>#<%= order != null ? order.getOrderNum() : "" %></span></div>
                        <div class="row"><span>Merchant</span><span>PETRA RTW</span></div>
                        <div class="row"><span>Amount</span><span>RM <%= payment.getAmount() %></span></div>
                    </div>

                    <button type="submit" name="action" value="pay" class="btn btn-pay">Login &amp; Pay RM <%= payment.getAmount() %></button>
                </form>
                <a href="<%= ctx %>/purchases/detail?order=<%= order != null ? order.getOrderNum() : "" %>" class="btn btn-cancel" style="margin-top:0;">Cancel Transaction</a>
            </div>
        </div>
    </div>

    <div class="footer">Powered by FPX &middot; Secured by Bank Negara Malaysia</div>
</body>
</html>
