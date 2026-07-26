package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.OrderDAO;
import petra.model.Order;

public class PaymentCompleteServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderNum = req.getParameter("order");
        if (orderNum == null || orderNum.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        orderNum = orderNum.trim();

        req.setAttribute("orderNum", orderNum);

        req.getRequestDispatcher("/WEB-INF/jsp/customer/paymentComplete.jsp").forward(req, resp);
    }
}
