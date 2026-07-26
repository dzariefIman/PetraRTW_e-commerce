package petra.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import petra.dao.CartDAO;
import petra.dao.OrderDAO;
import petra.dao.PaymentDAO;
import petra.model.Cart;
import petra.model.Order;
import petra.model.Payment;

public class PaymentGatewayServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String paymentIdStr = req.getParameter("payment_id");
        if (paymentIdStr == null || paymentIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        int paymentId;
        try {
            paymentId = Integer.parseInt(paymentIdStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        Payment payment;
        try {
            payment = paymentDAO.findById(paymentId);
            if (payment == null) {
                resp.sendRedirect(req.getContextPath() + "/checkout");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        if (!"INITIATED".equals(payment.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        try {
            paymentDAO.updateStatus(paymentId, "REDIRECTED", null, null);
        } catch (SQLException e) {
            // log
        }

        try {
            Order order = orderDAO.findById(payment.getOrderId());
            req.setAttribute("payment", payment);
            req.setAttribute("order", order);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        if ("FPX".equals(payment.getPaymentMethod())) {
            req.getRequestDispatcher("/WEB-INF/jsp/customer/fpxPayment.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/jsp/customer/cardPayment.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        String paymentIdStr = req.getParameter("payment_id");
        String action = req.getParameter("action");

        if (paymentIdStr == null || paymentIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }

        int paymentId = Integer.parseInt(paymentIdStr);

        try {
            Payment payment = paymentDAO.findById(paymentId);
            if (payment == null) {
                resp.sendRedirect(req.getContextPath() + "/checkout");
                return;
            }

            if ("cancel".equals(action)) {
                paymentDAO.updateStatus(paymentId, "CANCELLED", "E3_CANCELLED", "Customer cancelled payment");
                orderDAO.updateOrderStatus(payment.getOrderNum(), "PENDING_PAYMENT");
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }

            if ("change_method".equals(action)) {
                paymentDAO.updateStatus(paymentId, "VOIDED", "E4_CHANGED", "Customer changed payment method");
                orderDAO.updateOrderStatus(payment.getOrderNum(), "PENDING_PAYMENT");
                resp.sendRedirect(req.getContextPath() + "/checkout");
                return;
            }

            String bank = req.getParameter("bank");
            String fpxUser = req.getParameter("fpx_username");
            String fpxPass = req.getParameter("fpx_password");
            String cardNumber = req.getParameter("card_number");
            String expiry = req.getParameter("expiry");
            String cvv = req.getParameter("cvv");
            String cardholderName = req.getParameter("cardholder_name");

            boolean valid = false;
            if ("FPX".equals(payment.getPaymentMethod())) {
                valid = bank != null && !bank.isEmpty()
                        && fpxUser != null && !fpxUser.trim().isEmpty()
                        && fpxPass != null && !fpxPass.trim().isEmpty();
            } else {
                valid = cardNumber != null && cardNumber.replaceAll("\\s", "").matches("\\d{16}")
                        && expiry != null && expiry.matches("(0[1-9]|1[0-2])/\\d{2}")
                        && cvv != null && cvv.matches("\\d{3}")
                        && cardholderName != null && !cardholderName.trim().isEmpty();
            }

            if (!valid) {
                req.setAttribute("payment", payment);
                req.setAttribute("error", "Please fill in all required fields correctly.");
                if ("FPX".equals(payment.getPaymentMethod())) {
                    req.getRequestDispatcher("/WEB-INF/jsp/customer/fpxPayment.jsp").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/WEB-INF/jsp/customer/cardPayment.jsp").forward(req, resp);
                }
                return;
            }

            paymentDAO.updateStatus(paymentId, "PROCESSING", null, null);

            String gatewayRef = "FPX".equals(payment.getPaymentMethod()) ? bank : "SIM_" + System.currentTimeMillis();
            boolean paid = simulatePayment(payment.getPaymentMethod(), cardNumber);

            if (paid) {
                paymentDAO.markPaid(paymentId, gatewayRef);
                orderDAO.updateOrderStatus(payment.getOrderNum(), "PAID");
                orderDAO.updatePaymentInfo(payment.getOrderNum(), payment.getPaymentMethod(), payment.getGateway(), gatewayRef);
                cartDAO.clearCart(custId);
                resp.sendRedirect(req.getContextPath() + "/payment/complete?order=" + payment.getOrderNum());
            } else {
                paymentDAO.updateStatus(paymentId, "FAILED", "E2_DECLINED", "Payment declined by gateway");
                req.setAttribute("payment", payment);
                req.setAttribute("error", "Payment was declined. Please try another card or method.");
                if ("FPX".equals(payment.getPaymentMethod())) {
                    req.getRequestDispatcher("/WEB-INF/jsp/customer/fpxPayment.jsp").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/WEB-INF/jsp/customer/cardPayment.jsp").forward(req, resp);
                }
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private boolean simulatePayment(String method, String cardNumber) {
        if ("DEBIT".equals(method) || "CREDIT".equals(method)) {
            if ("4242424242424242".equals(cardNumber)) return true;
            if ("4000000000000002".equals(cardNumber)) return false;
        }
        return Math.random() > 0.05;
    }
}