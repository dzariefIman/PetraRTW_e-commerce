package petra.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import petra.dao.ProductDAO;
import petra.model.Product;

public class HomeServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String group = req.getParameter("group");

        if (group != null && !group.isEmpty()) {
            try {
                List<Product> products = productDAO.findByGroup(group);
                req.setAttribute("groupName", group);
                req.setAttribute("products", products);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/homeSales.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else {
            try {
                List<String> groups = productDAO.findAllCollections();
                Map<String, List<Product>> productGroups = new LinkedHashMap<String, List<Product>>();
                for (String g : groups) {
                    productGroups.put(g, productDAO.findByGroup(g));
                }
                List<Product> uncategorized = productDAO.findUncategorized();
                if (uncategorized != null && !uncategorized.isEmpty()) {
                    productGroups.put("New Arrivals", uncategorized);
                }
                req.setAttribute("productGroups", productGroups);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/home.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }
}
