package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.FeedbackDAO;
import petra.dao.AdvertisementDAO;
import petra.model.Feedback;
import petra.model.Advertisement;

public class AdvertisementServlet extends HttpServlet {

    private AdvertisementDAO advertisementDAO = new AdvertisementDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<Advertisement> advertisements = advertisementDAO.findByStatus("published");
            List<Feedback> feedbacks = feedbackDAO.findRecent(3);
            req.setAttribute("advertisements", advertisements);
            req.setAttribute("feedbacks", feedbacks);
            req.getRequestDispatcher("/WEB-INF/jsp/customer/advertisements.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
