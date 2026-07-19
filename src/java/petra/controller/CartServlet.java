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
import petra.dao.ProductDAO;
import petra.model.CartDBItem;
import petra.model.Product;

public class CartServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        String action = req.getParameter("action");

        if ("remove".equals(action)) {
            int cartId = parseInt(req.getParameter("cartId"));
            if (cartId > 0) {
                try {
                    cartDAO.removeItem(cartId);
                } catch (SQLException e) {
                    throw new ServletException(e);
                }
            }
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        try {
            List<CartDBItem> items = cartDAO.findByCustId(custId);
            req.setAttribute("cartItems", items);
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/customer/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        int custId = (Integer) session.getAttribute("userId");

        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                int productId = parseInt(req.getParameter("productId"));
                String size = req.getParameter("size") != null ? req.getParameter("size") : "";
                int qty = Math.max(1, parseInt(req.getParameter("quantity")));

                if (productId <= 0 || size.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                    return;
                }

                Product p = productDAO.findById(productId);
                if (p == null) {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                    return;
                }

                int available = p.getStockForSize(size);
                CartDBItem existing = cartDAO.findExisting(custId, productId, size);
                int currentQty = existing != null ? existing.getQuantity() : 0;
                int newTotal = currentQty + qty;

                if (newTotal > available) {
                    newTotal = available;
                }
                if (newTotal <= 0) {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                    return;
                }

                if (existing != null) {
                    cartDAO.updateQuantity(existing.getCartId(), newTotal);
                } else {
                    cartDAO.addItem(custId, productId, size, qty);
                }

            } else if ("update".equals(action)) {
                int cartId = parseInt(req.getParameter("cartId"));
                int qty = Math.max(0, parseInt(req.getParameter("quantity")));
                if (cartId > 0) {
                    if (qty <= 0) {
                        cartDAO.removeItem(cartId);
                    } else {
                        cartDAO.updateQuantity(cartId, qty);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private int parseInt(String val) {
        try { return Integer.parseInt(val); } catch (Exception e) { return 0; }
    }
}