package petra.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import petra.dao.IssueDAO;
import petra.dao.OrderDAO;
import petra.model.Issue;
import petra.model.Order;

@javax.servlet.annotation.MultipartConfig
public class ComplaintServlet extends HttpServlet {

    private IssueDAO issueDAO = new IssueDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");

        String orderIdStr = req.getParameter("purchase_id");

        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/purchases");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr.trim());
            Order item = orderDAO.findById(orderId);
            if (item == null || item.getCustId() != customerId) {
                resp.sendRedirect(req.getContextPath() + "/purchases");
                return;
            }
            req.setAttribute("selectedPurchase", item);
            req.getRequestDispatcher("/WEB-INF/jsp/customer/complaint.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");

        // Read the uploaded file part FIRST. On GlassFish, calling getParameter()
        // before getPart() on a multipart request makes getPart() return null.
        String imagePath = null;
        try {
            Part filePart = req.getPart("evidence");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    String appPath = req.getServletContext().getRealPath("/");
                    String savePath = appPath + "uploads" + File.separator + "issues";
                    File saveDir = new File(savePath);
                    saveDir.mkdirs();
                    String uniqueName = System.currentTimeMillis() + "_" + fileName;
                    try (java.io.InputStream is = filePart.getInputStream()) {
                        Files.copy(is, new File(saveDir, uniqueName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    // Also save to source web/ so files survive redeploy
                    try {
                        String srcWebPath = appPath.replace(File.separator + "build" + File.separator + "web" + File.separator, File.separator + "web" + File.separator);
                        String srcSavePath = srcWebPath + "uploads" + File.separator + "issues";
                        File srcDir = new File(srcSavePath);
                        if (!srcDir.exists()) srcDir.mkdirs();
                        File srcFile = new File(srcSavePath + File.separator + uniqueName);
                        if (!srcFile.exists()) {
                            Files.copy(new File(savePath + File.separator + uniqueName).toPath(), srcFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception e) {}
                    imagePath = "uploads/issues/" + uniqueName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int orderId = 0;
        try { orderId = Integer.parseInt(req.getParameter("purchase_id")); } catch (Exception e) {}
        String title = req.getParameter("title");
        String description = req.getParameter("description");

        if (orderId == 0 || title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            req.setAttribute("error", "Please select a product, enter a title, and description.");
            doGet(req, resp);
            return;
        }

        try {
            Order item = orderDAO.findById(orderId);
            if (item == null) {
                req.setAttribute("error", "Invalid product selection.");
                doGet(req, resp);
                return;
            }

            Issue issue = new Issue();
            issue.setCustId(customerId);
            issue.setOrderId(orderId);
            issue.setOrderNum(item.getOrderNum());
            issue.setTitle(title.trim());
            issue.setIssueDesc(description.trim());
            issue.setProductImage(imagePath);

            int issueId = issueDAO.create(issue);
            resp.sendRedirect(req.getContextPath() + "/tickets/detail?id=" + issueId);
        } catch (SQLException e) {
            req.setAttribute("error", "Failed to submit complaint: " + e.getMessage());
            doGet(req, resp);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String cd : contentDisp.split(";")) {
            if (cd.trim().startsWith("filename")) {
                String name = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return name.isEmpty() ? null : name;
            }
        }
        return null;
    }
}
