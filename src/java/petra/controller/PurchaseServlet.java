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
import petra.dao.PurchaseDAO;
import petra.model.Purchase;

public class PurchaseServlet extends HttpServlet {

    private PurchaseDAO purchaseDAO = new PurchaseDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private IssueDAO issueDAO = new IssueDAO();

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
                Purchase summary = purchaseDAO.findOrderSummary(orderNum.trim(), customerId);
                if (summary == null) {
                    resp.sendRedirect(req.getContextPath() + "/purchases");
                    return;
                }
                List<Purchase> items = purchaseDAO.findItemsByOrderNum(orderNum.trim(), customerId);
                Map<Integer, Boolean> feedbackMap = new HashMap<Integer, Boolean>();
                Map<Integer, Boolean> issueMap = new HashMap<Integer, Boolean>();
                for (Purchase item : items) {
                    feedbackMap.put(item.getPurchaseId(), feedbackDAO.hasFeedback(customerId, item.getPurchaseId()));
                    issueMap.put(item.getPurchaseId(), issueDAO.hasIssueForPurchase(customerId, item.getPurchaseId()));
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
                List<Purchase> orders = purchaseDAO.findOrdersByCustId(customerId, sort);
                req.setAttribute("purchases", orders);
                req.setAttribute("sort", sort);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/purchases.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }
}
