package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import petra.dao.FeedbackDAO;
import petra.dao.IssueDAO;
import petra.dao.OrderDAO;
import petra.dao.PaymentDAO;
import petra.model.Order;
import petra.model.Payment;

public class PurchaseServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private IssueDAO issueDAO = new IssueDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");

        String path = req.getServletPath();

        if ("/purchases/detail".equals(path)) {
            String orderNum = req.getParameter("order");
            if (orderNum == null || orderNum.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/purchases");
                return;
            }

            try {
                Order summary = orderDAO.findOrderSummary(orderNum.trim(), customerId);
                if (summary == null) {
                    resp.sendRedirect(req.getContextPath() + "/purchases");
                    return;
                }
                List<Order> items = orderDAO.findItemsByOrderNum(orderNum.trim(), customerId);
                Map<Integer, Boolean> feedbackMap = new HashMap<Integer, Boolean>();
                Map<Integer, Boolean> issueMap = new HashMap<Integer, Boolean>();
                for (Order item : items) {
                    feedbackMap.put(item.getOrderId(), feedbackDAO.hasFeedback(customerId, item.getOrderId()));
                    issueMap.put(item.getOrderId(), issueDAO.hasIssueForOrder(customerId, item.getOrderId()));
                }
                req.setAttribute("orderSummary", summary);
                req.setAttribute("orderItems", items);
                req.setAttribute("feedbackMap", feedbackMap);
                req.setAttribute("issueMap", issueMap);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/purchaseDetail.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else {
            try {
                String sort = req.getParameter("sort");
                if (sort == null || (!sort.equals("newest") && !sort.equals("oldest"))) {
                    sort = "newest";
                }
                List<Order> orders = orderDAO.findOrdersByCustId(customerId, sort);
                req.setAttribute("purchases", orders);
                req.setAttribute("sort", sort);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/purchases.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");
        String action = req.getParameter("action");
        String orderNum = req.getParameter("order_num");

        if (orderNum == null || orderNum.trim().isEmpty() || action == null) {
            resp.sendRedirect(req.getContextPath() + "/purchases");
            return;
        }
        orderNum = orderNum.trim();

        try {
            if ("cancel".equals(action)) {
                orderDAO.cancelOrder(orderNum, customerId);
                resp.sendRedirect(req.getContextPath() + "/purchases/detail?order=" + orderNum);
            } else if ("change_payment".equals(action)) {
                Order summary = orderDAO.findOrderSummary(orderNum, customerId);
                if (summary == null) {
                    resp.sendRedirect(req.getContextPath() + "/purchases");
                    return;
                }

                Payment oldPayment = paymentDAO.findLatestByOrderNum(orderNum);
                String oldGatewayRef = null;
                if (oldPayment != null) {
                    oldGatewayRef = oldPayment.getGatewayRef();
                    paymentDAO.updateStatus(oldPayment.getPaymentId(), "VOIDED", "CHANGED", "Customer changed payment method");
                }

                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                List<Order> items = orderDAO.findItemsByOrderNum(orderNum, customerId);
                for (Order item : items) {
                    if (item.getItemSubTotal() != null) total = total.add(item.getItemSubTotal());
                }
                if (summary.getShippingFee() != null) total = total.add(summary.getShippingFee());

                Payment newPayment = new Payment();
                newPayment.setOrderId(summary.getOrderId());
                newPayment.setOrderNum(orderNum);
                newPayment.setPaymentMethod(summary.getPaymentMethod());
                newPayment.setGateway("FPX".equals(summary.getPaymentMethod()) ? "FPX" : "MOCK_CARD");
                newPayment.setGatewayRef(oldGatewayRef);
                newPayment.setAmount(total);
                newPayment.setStatus("INITIATED");
                int newPaymentId = paymentDAO.create(newPayment);
                newPayment.setPaymentId(newPaymentId);

                resp.sendRedirect(req.getContextPath() + "/payment/gateway?payment_id=" + newPaymentId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/purchases");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
