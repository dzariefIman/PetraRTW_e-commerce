<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.Order"%>
<%
    String orderNum = (String) request.getAttribute("orderNum");
    Order summary = (Order) request.getAttribute("orderSummary");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Complete</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #e8ecef; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; }
        .card { background: #fff; border-radius: 10px; box-shadow: 0 4px 16px rgba(0,0,0,0.1); padding: 40px; max-width: 440px; width: 90%; text-align: center; }
        .check-circle { width: 80px; height: 80px; border-radius: 50%; background: #27ae60; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; }
        .check-circle svg { width: 40px; height: 40px; fill: #fff; }
        .card h1 { font-size: 24px; color: #222; margin-bottom: 8px; }
        .card p { font-size: 14px; color: #666; margin-bottom: 24px; }
        .order-num { font-size: 16px; font-weight: 700; color: #27ae60; background: #eafaf1; padding: 10px 20px; border-radius: 6px; display: inline-block; margin-bottom: 24px; }
        .btn { display: block; width: 100%; padding: 13px; border: none; border-radius: 6px; font-size: 15px; font-weight: 600; cursor: pointer; text-align: center; margin-bottom: 10px; text-decoration: none; }
        .btn-primary { background: #1a5276; color: #fff; }
        .btn-primary:hover { background: #154360; }
        .btn-print { background: #27ae60; color: #fff; }
        .btn-print:hover { background: #219a52; }
        .footer { margin-top: 24px; font-size: 12px; color: #999; }
    </style>
</head>
<body>
    <div class="card">
        <div class="check-circle">
            <svg viewBox="0 0 24 24"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/></svg>
        </div>
        <h1>Payment Complete</h1>
        <p>Your order has been successfully placed and payment received.</p>
        <div class="order-num">Order #<%= orderNum %></div>

        <a href="<%= ctx %>/purchases/detail?order=<%= orderNum %>" class="btn btn-primary">Back to Merchant</a>
        <a href="<%= ctx %>/receipt?order=<%= orderNum %>" target="_blank" class="btn btn-print">Print Receipt</a>

        <div class="footer">Thank you for shopping with PETRA RTW</div>
    </div>
</body>
</html>
