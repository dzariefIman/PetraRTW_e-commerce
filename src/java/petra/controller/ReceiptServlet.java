package petra.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.OrderDAO;
import petra.model.Order;

public class ReceiptServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderNum = req.getParameter("order");
        if (orderNum == null || orderNum.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing order number");
            return;
        }
        orderNum = orderNum.trim();

        try {
            List<Order> items = orderDAO.findByOrderNum(orderNum);
            if (items.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            Order first = items.get(0);
            BigDecimal itemSubtotal = BigDecimal.ZERO;
            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal voucherAmount = BigDecimal.ZERO;
            BigDecimal totalPrice = BigDecimal.ZERO;
            int totalQty = 0;

            for (Order it : items) {
                if (it.getItemSubTotal() != null) itemSubtotal = itemSubtotal.add(it.getItemSubTotal());
                if (it.getShippingFee() != null) shippingFee = shippingFee.add(it.getShippingFee());
                if (it.getVoucherAmount() != null) voucherAmount = voucherAmount.add(it.getVoucherAmount());
                if (it.getTotalPrice() != null) totalPrice = totalPrice.add(it.getTotalPrice());
                totalQty += it.getQuantity();
            }

            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.println(buildReceiptHtml(orderNum, first, items, itemSubtotal, shippingFee, voucherAmount, totalPrice, totalQty));
            out.close();

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String buildReceiptHtml(String orderNum, Order first, List<Order> items,
            BigDecimal itemSubtotal, BigDecimal shippingFee, BigDecimal voucherAmount,
            BigDecimal totalPrice, int totalQty) {

        String customerName = first.getCustomerName() != null ? first.getCustomerName() : "Walk-in Customer";
        String customerPhone = first.getCustomerPhone() != null ? first.getCustomerPhone() : "";
        String paymentMethod = first.getPaymentMethod() != null ? first.getPaymentMethod() : "Not specified";
        String dateTime = first.getCreatedTime() != null ? first.getCreatedTime() : "";

        StringBuilder itemRows = new StringBuilder();
        for (Order it : items) {
            String desc = it.getItemDesc() != null ? it.getItemDesc() : "";
            String size = it.getSize() != null ? it.getSize() : "-";
            int qty = it.getQuantity();
            BigDecimal sub = it.getItemSubTotal() != null ? it.getItemSubTotal() : BigDecimal.ZERO;
            BigDecimal unitPrice = qty > 0 ? sub.divide(BigDecimal.valueOf(qty), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

            itemRows.append("<div class='item'>");
            itemRows.append("<div class='item-name'>").append(escHtml(desc)).append("</div>");
            itemRows.append("<div class='item-detail'>Size: ").append(escHtml(size)).append("</div>");
            itemRows.append("<div class='item-line'>");
            itemRows.append("<span>Qty: ").append(qty).append(" &times; RM ").append(String.format("%.2f", unitPrice)).append("</span>");
            itemRows.append("<span>RM ").append(String.format("%.2f", sub)).append("</span>");
            itemRows.append("</div></div>");
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        html.append("<title>Receipt - ").append(escHtml(orderNum)).append("</title>");
        html.append("<style>");
        html.append("*{margin:0;padding:0;box-sizing:border-box}");
        html.append("body{font-family:'Courier New',monospace;background:#f5f5f5;padding:20px}");
        html.append("@media print{body{background:#fff;padding:0}.no-print{display:none}}");
        html.append(".receipt{max-width:400px;margin:0 auto;background:#fff;padding:30px 20px;border:1px solid #ddd}");
        html.append(".header{text-align:center;margin-bottom:20px;border-bottom:2px solid #333;padding-bottom:15px}");
        html.append(".header-title{font-size:24px;font-weight:bold;letter-spacing:2px;color:#5a3913}");
        html.append(".header-subtitle{font-size:12px;color:#666;margin-top:5px}");
        html.append(".header-info{font-size:11px;color:#666;margin-top:8px;line-height:1.6}");
        html.append(".section{margin:15px 0}");
        html.append(".label{font-size:11px;color:#999;text-transform:uppercase;letter-spacing:1px}");
        html.append(".value{font-size:12px;font-weight:bold;color:#333;margin-top:3px}");
        html.append(".divider{border-top:1px dashed #ccc;margin:15px 0}");
        html.append(".item{margin:8px 0}");
        html.append(".item-name{font-size:12px;font-weight:bold;color:#333}");
        html.append(".item-detail{font-size:11px;color:#666;margin-top:2px}");
        html.append(".item-line{display:flex;justify-content:space-between;font-size:12px;margin-top:4px}");
        html.append(".total-line{display:flex;justify-content:space-between;font-size:13px;font-weight:bold;margin:8px 0;color:#1b8b3b}");
        html.append(".footer{text-align:center;font-size:10px;color:#999;margin-top:20px;border-top:2px solid #333;padding-top:15px}");
        html.append(".print-btn{display:inline-flex;align-items:center;gap:8px;padding:10px 20px;background:#5a3913;color:#fff;border:none;border-radius:4px;cursor:pointer;font-size:14px;margin-bottom:20px}");
        html.append(".print-btn:hover{background:#6b4a1a}");
        html.append("</style></head><body>");

        html.append("<button class='print-btn no-print' onclick='window.print()'>");
        html.append("<svg width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'>");
        html.append("<polyline points='6 9 6 2 18 2 18 9'/><path d='M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2'/><rect x='6' y='14' width='12' height='8'/>");
        html.append("</svg>PRINT RECEIPT</button>");

        html.append("<div class='receipt'>");

        html.append("<div class='header'>");
        html.append("<div class='header-title'>PETRA RTW</div>");
        html.append("<div class='header-subtitle'>Ready-to-Wear Fashion</div>");
        html.append("<div class='header-info'>");
        html.append("20a, Jalan Plumbum P7/P<br>Seksyen 7, 40000 Shah Alam, Selangor<br>");
        html.append("Phone: +6014 2325 208<br>Email: petra.rtw@gmail.com<br>");
        html.append("Opening Times: Monday &ndash; Sunday (10AM &ndash; 8PM)");
        html.append("</div></div>");

        html.append("<div class='section'><div class='label'>Receipt #</div><div class='value'>").append(escHtml(orderNum)).append("</div></div>");
        html.append("<div class='section'><div class='label'>Date &amp; Time</div><div class='value'>").append(escHtml(dateTime)).append("</div></div>");
        html.append("<div class='section'><div class='label'>Customer</div><div class='value'>").append(escHtml(customerName)).append("</div>");
        if (!customerPhone.isEmpty()) {
            html.append("<div style='font-size:11px;color:#666;margin-top:3px'>").append(escHtml(customerPhone)).append("</div>");
        }
        html.append("</div>");

        html.append("<div class='divider'></div>");
        html.append("<div class='section'><div class='label'>Items</div>");
        html.append(itemRows.toString());
        html.append("</div>");

        html.append("<div class='divider'></div>");
        html.append("<div class='section'>");
        html.append("<div class='item-line'><span>Subtotal</span><span>RM ").append(String.format("%.2f", itemSubtotal)).append("</span></div>");
        html.append("<div class='item-line'><span>Shipping Fee</span><span>RM ").append(String.format("%.2f", shippingFee)).append("</span></div>");
        if (voucherAmount.compareTo(BigDecimal.ZERO) > 0) {
            html.append("<div class='item-line' style='color:#c33'><span>Voucher Discount</span><span>-RM ").append(String.format("%.2f", voucherAmount)).append("</span></div>");
        }
        html.append("<div class='divider'></div>");
        html.append("<div class='total-line'><span>TOTAL</span><span>RM ").append(String.format("%.2f", totalPrice)).append("</span></div>");
        html.append("</div>");

        html.append("<div class='section'><div class='label'>Payment Method</div><div class='value'>").append(escHtml(paymentMethod)).append("</div></div>");

        html.append("<div class='footer'>");
        html.append("<p style='margin-bottom:8px'>Thank you for your purchase!</p>");
        html.append("<p>For inquiries, contact us at petra.rtw@gmail.com</p>");
        html.append("<p style='margin-top:10px;font-size:9px'>Generated on: ").append(escHtml(dateTime)).append("</p>");
        html.append("</div>");

        html.append("</div></body></html>");
        return html.toString();
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
