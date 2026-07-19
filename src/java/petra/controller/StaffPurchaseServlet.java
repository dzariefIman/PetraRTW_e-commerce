package petra.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.PurchaseDAO;
import petra.model.Purchase;

public class StaffPurchaseServlet extends HttpServlet {

    private static final int PER_PAGE = 8;
    private PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("detail".equals(action)) {
            showDetail(req, resp);
        } else {
            showList(req, resp);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String customerId = req.getParameter("customer_id");
            String date = req.getParameter("purchase_date");

            int page = 1;
            if (req.getParameter("p") != null) {
                try { page = Math.max(1, Integer.parseInt(req.getParameter("p"))); }
                catch (NumberFormatException e) { page = 1; }
            }

            int total = purchaseDAO.countOrderSummaries(customerId, date);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PER_PAGE));
            if (page > totalPages) page = totalPages;

            List<Purchase> orders = purchaseDAO.findOrderSummaries(page, PER_PAGE, customerId, date);

            String flashMsg = (String) req.getSession().getAttribute("flashMsg");
            if (flashMsg != null) {
                req.setAttribute("flashMsg", flashMsg);
                req.getSession().removeAttribute("flashMsg");
            }
            String flashErr = (String) req.getSession().getAttribute("flashErr");
            if (flashErr != null) {
                req.setAttribute("flashErr", flashErr);
                req.getSession().removeAttribute("flashErr");
            }

            req.setAttribute("orders", orders);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
            req.setAttribute("searchCustomerId", customerId != null ? customerId : "");
            req.setAttribute("searchDate", date != null ? date : "");
            req.getRequestDispatcher("/WEB-INF/jsp/staff/purchaseHistory.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String orderNumber = req.getParameter("order");
            if (orderNumber == null || orderNumber.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/staff/purchases");
                return;
            }
            orderNumber = orderNumber.trim();

            List<Purchase> items = purchaseDAO.findByOrderNum(orderNumber);
            if (items.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/staff/purchases");
                return;
            }

            Purchase first = items.get(0);
            BigDecimal itemSubtotal = BigDecimal.ZERO;
            BigDecimal shippingFee = BigDecimal.ZERO;
            BigDecimal voucherAmount = BigDecimal.ZERO;
            BigDecimal totalPrice = BigDecimal.ZERO;
            int totalQty = 0;

            for (Purchase it : items) {
                if (it.getItemSubTotal() != null) itemSubtotal = itemSubtotal.add(it.getItemSubTotal());
                if (it.getShippingFee() != null) shippingFee = shippingFee.add(it.getShippingFee());
                if (it.getVoucherAmount() != null) voucherAmount = voucherAmount.add(it.getVoucherAmount());
                if (it.getTotalPrice() != null) totalPrice = totalPrice.add(it.getTotalPrice());
                totalQty += it.getQuantity();
            }

            req.setAttribute("items", items);
            req.setAttribute("purchase", first);
            req.setAttribute("orderNumber", orderNumber);
            req.setAttribute("itemSubtotal", itemSubtotal);
            req.setAttribute("shippingFee", shippingFee);
            req.setAttribute("voucherAmount", voucherAmount);
            req.setAttribute("totalPrice", totalPrice);
            req.setAttribute("totalQty", totalQty);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/purchaseDetail.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
