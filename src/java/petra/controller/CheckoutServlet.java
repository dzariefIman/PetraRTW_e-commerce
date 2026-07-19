package petra.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import petra.dao.CartDAO;
import petra.dao.PurchaseDAO;
import petra.model.CartDBItem;

public class CheckoutServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        List<CartDBItem> items;
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
        for (CartDBItem item : items) {
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

        List<CartDBItem> items;
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
            int purchaseId = purchaseDAO.createOrder(custId, paymentMethod, items);
            cartDAO.clearCart(custId);
            resp.sendRedirect(req.getContextPath() + "/purchases/detail?id=" + purchaseId);
        } catch (SQLException e) {
            req.setAttribute("error", "Checkout failed: " + e.getMessage());
            doGet(req, resp);
        }
    }
}