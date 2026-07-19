package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.FeedbackDAO;
import petra.dao.PromotionDAO;
import petra.model.Feedback;
import petra.model.Promotion;

public class PromotionServlet extends HttpServlet {

    private PromotionDAO promotionDAO = new PromotionDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<Promotion> promotions = promotionDAO.findByStatus("published");
            List<Feedback> feedbacks = feedbackDAO.findRecent(3);
            req.setAttribute("promotions", promotions);
            req.setAttribute("feedbacks", feedbacks);
            req.getRequestDispatcher("/WEB-INF/jsp/customer/promotions.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
