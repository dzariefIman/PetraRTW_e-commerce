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

public class CheckoutServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        List<Cart> items;
        try {
            items = cartDAO.findByCustId(custId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        if (items.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Cart item : items) {
            if (!item.isOutOfStock()) {
                subtotal = subtotal.add(item.getSubtotal());
            }
        }
        req.setAttribute("cartItems", items);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("shipping", new BigDecimal("4.50"));
        req.setAttribute("total", subtotal.add(new BigDecimal("4.50")));
        req.getRequestDispatcher("/WEB-INF/jsp/customer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        List<Cart> items;
        try {
            items = cartDAO.findByCustId(custId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        if (items.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String paymentMethod = req.getParameter("payment_method");
        if (paymentMethod == null
                || (!paymentMethod.equals("FPX") && !paymentMethod.equals("DEBIT") && !paymentMethod.equals("CREDIT"))) {
            req.setAttribute("error", "Please select a valid payment method.");
            doGet(req, resp);
            return;
        }

        try {
            BigDecimal total = BigDecimal.ZERO;
            for (Cart item : items) {
                if (!item.isOutOfStock()) {
                    total = total.add(item.getSubtotal());
                }
            }
            total = total.add(new BigDecimal("4.50"));

            int orderId = orderDAO.createOrder(custId, paymentMethod, items);

            cartDAO.clearCart(custId);

            String gateway = "FPX".equals(paymentMethod) ? "FPX" : "MOCK_CARD";
            String gatewayRef = "FPX".equals(paymentMethod) ? req.getParameter("bank") : "SIM_" + System.currentTimeMillis();

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setOrderNum(getOrderNum(orderId));
            payment.setPaymentMethod(paymentMethod);
            payment.setGateway(gateway);
            payment.setGatewayRef(gatewayRef);
            payment.setAmount(total);
            payment.setStatus("INITIATED");
            int paymentId = paymentDAO.create(payment);
            payment.setPaymentId(paymentId);

            req.getSession().setAttribute("pendingPaymentId", payment.getPaymentId());
            req.getSession().setAttribute("pendingOrderNum", payment.getOrderNum());

            String paymentPage = req.getContextPath() + "/payment/gateway?payment_id=" + payment.getPaymentId();
            resp.sendRedirect(paymentPage);

        } catch (SQLException e) {
            req.setAttribute("error", "Checkout failed: " + e.getMessage());
            doGet(req, resp);
        }
    }

    private String getOrderNum(int orderId) throws SQLException {
        Order item = orderDAO.findById(orderId);
        return item != null ? item.getOrderNum() : null;
    }
}