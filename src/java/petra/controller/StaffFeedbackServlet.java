package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.FeedbackDAO;
import petra.model.Feedback;

public class StaffFeedbackServlet extends HttpServlet {

    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        try { page = Integer.parseInt(req.getParameter("p")); } catch (Exception e) {}
        if (page < 1) page = 1;

        int perPage = 6;
        int offset = (page - 1) * perPage;

        try {
            int total = feedbackDAO.countAll();
            int totalPages = (int) Math.ceil((double) total / perPage);
            if (totalPages < 1) totalPages = 1;

            List<Feedback> feedbacks = feedbackDAO.findAllPaginated(offset, perPage);

            req.setAttribute("feedbacks", feedbacks);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/staffFeedback.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
