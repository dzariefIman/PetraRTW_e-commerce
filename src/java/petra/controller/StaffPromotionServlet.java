package petra.controller;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import petra.dao.PromotionDAO;
import petra.dao.ProductDAO;
import petra.model.Product;
import petra.model.Promotion;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class StaffPromotionServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads/promotions";
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int LIST_LIMIT = 4;

    private PromotionDAO promotionDAO = new PromotionDAO();
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String msg = req.getParameter("msg");

        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                promotionDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Promotion deleted");
                return;
            }

            List<Product> products = productDAO.findAll();
            List<Promotion> drafts = promotionDAO.findByStatusWithLimit("draft", LIST_LIMIT);
            List<Promotion> published = promotionDAO.findByStatusWithLimit("published", LIST_LIMIT);

            req.setAttribute("products", products);
            req.setAttribute("drafts", drafts);
            req.setAttribute("published", published);
            if (msg != null) req.setAttribute("msg", msg);

            req.getRequestDispatcher("/WEB-INF/jsp/staff/staffPromotions.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            String publishId = req.getParameter("publish_id");
            String deleteId = req.getParameter("delete_id");
            String editId = req.getParameter("edit_id");
            String action = req.getParameter("action");

            if (publishId != null && !publishId.isEmpty()) {
                handlePublish(req, resp, Integer.parseInt(publishId));
            } else if (deleteId != null && !deleteId.isEmpty()) {
                promotionDAO.delete(Integer.parseInt(deleteId));
                resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Promotion deleted");
            } else if (editId != null && !editId.isEmpty()) {
                handleEdit(req, resp, Integer.parseInt(editId));
            } else if ("draft".equals(action)) {
                handleAdd(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/staff/promotions");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=" + URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        String startDate = trim(req.getParameter("start_date"));
        String endDate = trim(req.getParameter("end_date"));

        validateDates(title, description, startDate, endDate);

        Integer productId = parseInt(req.getParameter("product_id"));
        String groupName = trim(req.getParameter("group_name"));

        String imagePath = handleFileUpload(req);

        Promotion p = new Promotion();
        p.setAdsTitle(title);
        p.setAdsDesc(description);
        p.setProductImage(imagePath);
        p.setProductId(productId);
        p.setCollection(groupName);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setStatus("draft");
        promotionDAO.insert(p);

        resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Promotion saved as draft.");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, int editId)
            throws Exception {
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        String startDate = trim(req.getParameter("start_date"));
        String endDate = trim(req.getParameter("end_date"));

        validateDates(title, description, startDate, endDate);

        Promotion old = promotionDAO.findById(editId);
        if (old == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Promotion not found");
            return;
        }

        Integer productId = parseInt(req.getParameter("product_id"));
        String groupName = trim(req.getParameter("group_name"));

        String imagePath = handleFileUpload(req);
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = old.getProductImage();
        }

        Promotion p = new Promotion();
        p.setAdsId(editId);
        p.setAdsTitle(title);
        p.setAdsDesc(description);
        p.setProductImage(imagePath);
        p.setProductId(productId);
        p.setCollection(groupName);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setStatus("draft");
        promotionDAO.update(p);

        resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Draft updated successfully.");
    }

    private void handlePublish(HttpServletRequest req, HttpServletResponse resp, int publishId)
            throws Exception {
        Promotion p = promotionDAO.findById(publishId);
        if (p != null) {
            p.setStatus("published");
            promotionDAO.update(p);
        }
        resp.sendRedirect(req.getContextPath() + "/staff/promotions?msg=Promotion published.");
    }

    private void validateDates(String title, String description, String startDate, String endDate) throws Exception {
        if (title == null || title.isEmpty()) throw new Exception("Title is required.");
        if (description == null || description.isEmpty()) throw new Exception("Description is required.");
        if (startDate == null || startDate.isEmpty()) throw new Exception("Start date is required.");
        if (endDate == null || endDate.isEmpty()) throw new Exception("End date is required.");
        if (startDate.compareTo(endDate) > 0) {
            throw new Exception("Start date must be before end date.");
        }
    }

    private String handleFileUpload(HttpServletRequest req) throws Exception {
        Part filePart;
        try {
            filePart = req.getPart("image");
        } catch (ServletException e) {
            return null;
        }

        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String contentType = filePart.getContentType();
        boolean allowed = false;
        for (String t : ALLOWED_TYPES) {
            if (t.equals(contentType)) { allowed = true; break; }
        }
        if (!allowed) throw new Exception("Only image files (jpg, png, gif, webp) are allowed.");

        if (filePart.getSize() > MAX_FILE_SIZE) throw new Exception("File too large. Maximum 5MB allowed.");

        String fileName = extractFileName(filePart);
        if (fileName == null) fileName = "image";

        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) ext = fileName.substring(dotIdx);

        String uniqueName = "promo_" + System.currentTimeMillis() + "_"
                          + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ext;

        String appPath = req.getServletContext().getRealPath("");
        String savePath = appPath + File.separator + UPLOAD_DIR;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) saveDir.mkdirs();

        try (InputStream is = filePart.getInputStream()) {
            Files.copy(is, new File(savePath + File.separator + uniqueName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // Also save to source web/ so files survive redeploy
        try {
            String srcWebPath = appPath.replace(File.separator + "build" + File.separator + "web", File.separator + "web");
            String srcSavePath = srcWebPath + File.separator + "uploads" + File.separator + "promotions";
            File srcDir = new File(srcSavePath);
            if (!srcDir.exists()) srcDir.mkdirs();
            File srcFile = new File(srcSavePath + File.separator + uniqueName);
            if (!srcFile.exists()) {
                Files.copy(new File(savePath + File.separator + uniqueName).toPath(), srcFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {}

        return UPLOAD_DIR + "/" + uniqueName;
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return null;
        for (String cd : contentDisp.split(";")) {
            cd = cd.trim();
            if (cd.startsWith("filename")) {
                String name = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return name.isEmpty() ? null : name;
            }
        }
        return null;
    }

    private Integer parseInt(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        try { return Integer.parseInt(val.trim()); } catch (Exception e) { return null; }
    }

    private String trim(String s) {
        return s != null ? s.trim() : "";
    }
}
