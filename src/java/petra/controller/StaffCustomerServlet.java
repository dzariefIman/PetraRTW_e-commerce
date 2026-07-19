package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.CustomerDAO;
import petra.model.Customer;

public class StaffCustomerServlet extends HttpServlet {

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String searchId = req.getParameter("customer_id");
        String searchName = req.getParameter("customer_name");
        String searchEmail = req.getParameter("customer_email");

        int page = 1;
        try { page = Integer.parseInt(req.getParameter("p")); } catch (Exception e) {}
        if (page < 1) page = 1;
        int perPage = 10;
        int offset = (page - 1) * perPage;

        try {
            int total = customerDAO.countWithFilters(searchId, searchName, searchEmail);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / perPage));

            List<Customer> customers = customerDAO.findAllWithFilters(searchId, searchName, searchEmail, perPage, offset);

            req.setAttribute("customers", customers);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("currentPage", page);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/customers.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
