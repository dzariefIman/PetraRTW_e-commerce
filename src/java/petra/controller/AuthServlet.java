package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import petra.dao.CustomerDAO;
import petra.dao.SalesAdminDAO;
import petra.model.Customer;
import petra.model.SalesAdmin;

public class AuthServlet extends HttpServlet {

    private CustomerDAO customerDAO = new CustomerDAO();
    private SalesAdminDAO salesAdminDAO = new SalesAdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("register".equals(action)) {
            handleRegister(req, resp);
        } else {
            handleLogin(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String role = req.getParameter("role");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isSalesAdmin = "staff".equals(role);

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Missing fields");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            if (isSalesAdmin) {
                SalesAdmin salesAdmin = salesAdminDAO.authenticate(username, password);
                if (salesAdmin != null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("userId", salesAdmin.getStaffId());
                    session.setAttribute("username", salesAdmin.getUsername());
                    session.setAttribute("userRole", "salesAdmin");
                    resp.sendRedirect(req.getContextPath() + "/salesAdmin/home");
                } else {
                    req.setAttribute("error", "Invalid credentials");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                }
            } else {
                Customer customer = customerDAO.authenticate(username, password);
                if (customer != null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("userId", customer.getCustId());
                    session.setAttribute("username", customer.getUsername());
                    session.setAttribute("userRole", "customer");
                    resp.sendRedirect(req.getContextPath() + "/home");
                } else {
                    req.setAttribute("error", "Invalid credentials");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                }
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");

        if (name == null || username == null || email == null || password == null
                || name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Missing fields");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(password2)) {
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            if (customerDAO.findByUsername(username) != null || customerDAO.findByEmail(email) != null) {
                req.setAttribute("error", "Username or email already exists");
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                return;
            }

            Customer c = new Customer();
            c.setCustName(name);
            c.setUsername(username);
            c.setCustEmail(email);
            c.setCustAddress(address);
            c.setCustPhoneNum(phone);
            c.setPassword(password);

            customerDAO.insert(c);
            req.setAttribute("registered", true);
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("error", "Registration failed: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
