package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import petra.dao.StaffDAO;
import petra.model.Staff;

public class StaffDashboardServlet extends HttpServlet {

    private StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int staffId = (int) session.getAttribute("userId");

        try {
            Staff staff = staffDAO.findById(staffId);
            req.setAttribute("staff", staff);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
