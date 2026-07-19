package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.IssueDAO;
import petra.model.Issue;

public class StaffIssueServlet extends HttpServlet {

    private static final int PER_PAGE = 8;
    private IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String ticketNumber = req.getParameter("ticket_number");
        String customerName = req.getParameter("customer_name");
        String status = req.getParameter("status");

        int page = 1;
        if (req.getParameter("p") != null) {
            try { page = Math.max(1, Integer.parseInt(req.getParameter("p"))); }
            catch (NumberFormatException e) { page = 1; }
        }

        try {
            int total = issueDAO.countAllWithFilters(ticketNumber, customerName, status);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PER_PAGE));
            if (page > totalPages) page = totalPages;

            List<Issue> issues = issueDAO.findAllWithFilters(ticketNumber, customerName, status, page, PER_PAGE);
            req.setAttribute("issues", issues);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/staffIssues.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
