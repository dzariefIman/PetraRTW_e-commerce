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
import petra.dao.FeedbackDAO;
import petra.dao.PurchaseDAO;
import petra.model.Feedback;
import petra.model.Purchase;

@javax.servlet.annotation.MultipartConfig
public class FeedbackServlet extends HttpServlet {

    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private PurchaseDAO purchaseDAO = new PurchaseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");

        if ("my".equals(action)) {
            try {
                List<Feedback> myFeedbacks = feedbackDAO.findByCustId(customerId);
                req.setAttribute("myFeedbacks", myFeedbacks);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/myFeedbacks.jsp").forward(req, resp);
                return;
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

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
            Feedback existing = feedbackDAO.findByCustAndPurchase(customerId, purchaseId);
            req.setAttribute("selectedPurchase", item);
            if (existing != null) {
                req.setAttribute("existingFeedback", existing);
            }
            req.getRequestDispatcher("/WEB-INF/jsp/customer/feedback.jsp").forward(req, resp);
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
            Part filePart = req.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    String appPath = req.getServletContext().getRealPath("/");
                    String savePath = appPath + "uploads" + File.separator + "feedback";
                    File saveDir = new File(savePath);
                    saveDir.mkdirs();
                    String uniqueName = System.currentTimeMillis() + "_" + fileName;
                    try (java.io.InputStream is = filePart.getInputStream()) {
                        Files.copy(is, new File(saveDir, uniqueName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    imagePath = "uploads/feedback/" + uniqueName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int purchaseId = 0;
        try { purchaseId = Integer.parseInt(req.getParameter("purchase_id")); } catch (Exception e) {}
        int rating = 0;
        try { rating = Integer.parseInt(req.getParameter("rating")); } catch (Exception e) {}
        String feedbackText = req.getParameter("feedback_text");

        if (purchaseId == 0 || rating < 1 || rating > 5 || feedbackText == null || feedbackText.trim().isEmpty()) {
            req.setAttribute("error", "Please select a product, rating (1-5 stars), and your feedback.");
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

            Feedback existing = feedbackDAO.findByCustAndPurchase(customerId, purchaseId);
            if (existing != null) {
                existing.setProductName((item.getProductTitle() != null && !item.getProductTitle().isEmpty() ? item.getProductTitle() : item.getPurchaseDesc()));
                existing.setOrderNum(item.getOrderNum());
                existing.setRating(rating);
                existing.setFeedbackText(feedbackText.trim());
                if (imagePath != null) {
                    existing.setProductImage(imagePath);
                }
                existing.setStatus("Updated");
                feedbackDAO.update(existing);
            } else {
                Feedback fb = new Feedback();
                fb.setCustId(customerId);
                fb.setPurchaseId(purchaseId);
                fb.setProductName((item.getProductTitle() != null && !item.getProductTitle().isEmpty() ? item.getProductTitle() : item.getPurchaseDesc()));
                fb.setOrderNum(item.getOrderNum());
                fb.setRating(rating);
                fb.setFeedbackText(feedbackText.trim());
                fb.setProductImage(imagePath);
                feedbackDAO.create(fb);
            }

            resp.sendRedirect(req.getContextPath() + "/purchases/detail?order=" + item.getOrderNum());
        } catch (SQLException e) {
            req.setAttribute("error", "Failed to submit feedback: " + e.getMessage());
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
