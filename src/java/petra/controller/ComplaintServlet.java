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
import petra.dao.PurchaseDAO;
import petra.model.Issue;
import petra.model.Purchase;

@javax.servlet.annotation.MultipartConfig
public class ComplaintServlet extends HttpServlet {

    private IssueDAO issueDAO = new IssueDAO();
    private PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");

        String purchaseIdStr = req.getParameter("purchase_id");

        if (purchaseIdStr == null || purchaseIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/purchases");
            return;
        }

        try {
            int purchaseId = Integer.parseInt(purchaseIdStr.trim());
            Purchase item = purchaseDAO.findById(purchaseId);
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
                    imagePath = "uploads/issues/" + uniqueName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int purchaseId = 0;
        try { purchaseId = Integer.parseInt(req.getParameter("purchase_id")); } catch (Exception e) {}
        String title = req.getParameter("title");
        String description = req.getParameter("description");

        if (purchaseId == 0 || title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            req.setAttribute("error", "Please select a product, enter a title, and description.");
            doGet(req, resp);
            return;
        }

        try {
            Purchase item = purchaseDAO.findById(purchaseId);
            if (item == null) {
                req.setAttribute("error", "Invalid product selection.");
                doGet(req, resp);
                return;
            }

            Issue issue = new Issue();
            issue.setCustId(customerId);
            issue.setPurchaseId(purchaseId);
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
