package petra.controller;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import petra.dao.ProductDAO;
import petra.model.Product;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class StaffProductServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads/products";
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int PER_PAGE = 8;

    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String searchName = req.getParameter("name");
            String searchGroup = req.getParameter("group");
            int page = 1;
            if (req.getParameter("p") != null) {
                try { page = Math.max(1, Integer.parseInt(req.getParameter("p"))); }
                catch (NumberFormatException e) { page = 1; }
            }

            int total = productDAO.countAll(searchName, searchGroup);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PER_PAGE));
            if (page > totalPages) page = totalPages;

            List<Product> products = productDAO.findAllPaged(page, PER_PAGE, searchName, searchGroup);
            List<String> groups = productDAO.findAllCollections();

            req.setAttribute("products", products);
            req.setAttribute("groups", groups);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total", total);
            req.setAttribute("searchName", searchName != null ? searchName : "");
            req.setAttribute("searchGroup", searchGroup != null ? searchGroup : "");

            String flashMsg = (String) req.getSession().getAttribute("flashMsg");
            if (flashMsg != null) {
                req.setAttribute("flashMsg", flashMsg);
                req.getSession().removeAttribute("flashMsg");
            }
            String flashErr = (String) req.getSession().getAttribute("flashErr");
            if (flashErr != null) {
                req.setAttribute("flashErr", flashErr);
                req.getSession().removeAttribute("flashErr");
            }

            req.getRequestDispatcher("/WEB-INF/jsp/staff/products.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        if (action == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/products");
            return;
        }

        try {
            switch (action) {
                case "add":
                    handleAdd(req, resp);
                    break;
                case "edit":
                    handleEdit(req, resp);
                    break;
                case "delete":
                    handleDelete(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/staff/products");
            }
        } catch (Exception e) {
            if (isAjax(req)) {
                sendJson(resp, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            } else {
                req.getSession().setAttribute("flashErr", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/staff/products");
            }
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        BigDecimal price = parsePrice(req.getParameter("price"));
        String groupName = trim(req.getParameter("group_name"));
        String itemType = trim(req.getParameter("item_type"));
        if (itemType.isEmpty()) itemType = "cloth";

        validateProduct(title, price, null);

        Product existing = productDAO.findByTitle(title);
        if (existing != null) {
            throw new Exception("Product title already exists");
        }

        String imagePath = handleFileUpload(req, "");
        Product p = new Product();
        p.setShopProductTitle(title);
        p.setShopProductDesc(description);
        p.setShopProductPrice(price);
        p.setCollection(groupName);
        p.setProductImage(imagePath);
        p.setItemType(itemType);
        if ("scarves".equals(itemType)) {
            p.setSizeS(parseInt(req.getParameter("size_s")));
        } else {
            p.setSizeS(parseInt(req.getParameter("size_s")));
            p.setSizeM(parseInt(req.getParameter("size_m")));
            p.setSizeL(parseInt(req.getParameter("size_l")));
            p.setSizeXL(parseInt(req.getParameter("size_xl")));
        }
        productDAO.insert(p);

        int productId = p.getShopProductId();
        if (productId == 0) {
            throw new Exception("Failed to get product ID after insert");
        }

        if (isAjax(req)) {
            Product saved = productDAO.findById(productId);
            sendJson(resp, 200, buildProductJson(saved));
        } else {
            req.getSession().setAttribute("flashMsg", "Product added successfully");
            resp.sendRedirect(req.getContextPath() + "/staff/products");
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int productId = parseInt(req.getParameter("product_id"));
        if (productId <= 0) throw new Exception("Invalid product ID");

        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        BigDecimal price = parsePrice(req.getParameter("price"));
        String groupName = trim(req.getParameter("group_name"));
        String itemType = trim(req.getParameter("item_type"));
        if (itemType.isEmpty()) itemType = "cloth";

        validateProduct(title, price, productId);

        Product old = productDAO.findById(productId);
        if (old == null) throw new Exception("Product not found");

        String imagePath = handleFileUpload(req, old.getProductImage() != null ? old.getProductImage() : "");

        Product p = new Product();
        p.setShopProductId(productId);
        p.setShopProductTitle(title);
        p.setShopProductDesc(description);
        p.setShopProductPrice(price);
        p.setCollection(groupName);
        p.setProductImage(imagePath);
        p.setItemType(itemType);
        if ("scarves".equals(itemType)) {
            p.setSizeS(parseIntDefault(req.getParameter("size_s"), old.getSizeS()));
        } else {
            p.setSizeS(parseIntDefault(req.getParameter("size_s"), old.getSizeS()));
            p.setSizeM(parseIntDefault(req.getParameter("size_m"), old.getSizeM()));
            p.setSizeL(parseIntDefault(req.getParameter("size_l"), old.getSizeL()));
            p.setSizeXL(parseIntDefault(req.getParameter("size_xl"), old.getSizeXL()));
        }
        productDAO.update(p);

        if (isAjax(req)) {
            Product saved = productDAO.findById(productId);
            sendJson(resp, 200, buildProductJson(saved));
        } else {
            req.getSession().setAttribute("flashMsg", "Product updated successfully");
            resp.sendRedirect(req.getContextPath() + "/staff/products");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int productId = parseInt(req.getParameter("product_id"));
        if (productId <= 0) throw new Exception("Invalid product ID");
        productDAO.delete(productId);
        req.getSession().setAttribute("flashMsg", "Product deleted successfully");
        resp.sendRedirect(req.getContextPath() + "/staff/products");
    }

    private void validateProduct(String title, BigDecimal price, Integer excludeId) throws Exception {
        if (title == null || title.isEmpty()) throw new Exception("Product title is required");
        if (title.length() < 2) throw new Exception("Product title must be at least 2 characters");
        if (title.length() > 255) throw new Exception("Product title cannot exceed 255 characters");
        if (price == null) throw new Exception("Price must be a valid number");
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new Exception("Price must be greater than 0");
        if (price.compareTo(new BigDecimal("999999.99")) > 0) throw new Exception("Price is too high (max 999999.99)");
    }

    private BigDecimal parsePrice(String val) throws Exception {
        if (val == null || val.trim().isEmpty()) return null;
        try {
            BigDecimal bd = new BigDecimal(val.trim());
            if (bd.scale() > 2) bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd;
        } catch (NumberFormatException e) {
            throw new Exception("Price must be a valid number");
        }
    }

    private String handleFileUpload(HttpServletRequest req, String oldImagePath) throws Exception {
        Part filePart;
        try {
            filePart = req.getPart("product_image");
        } catch (ServletException e) {
            return oldImagePath;
        }

        if (filePart == null || filePart.getSize() == 0) {
            return oldImagePath;
        }

        String contentType = filePart.getContentType();
        boolean allowed = false;
        for (String t : ALLOWED_TYPES) {
            if (t.equals(contentType)) { allowed = true; break; }
        }
        if (!allowed) throw new Exception("Invalid file type. Allowed: JPG, PNG, GIF, WebP");

        if (filePart.getSize() > MAX_FILE_SIZE) throw new Exception("File too large. Maximum 5MB allowed");

        String fileName = extractFileName(filePart);
        if (fileName == null) fileName = "image";

        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) ext = fileName.substring(dotIdx);

        String uniqueName = "product_" + System.currentTimeMillis() + "_" 
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
            String srcSavePath = srcWebPath + File.separator + UPLOAD_DIR;
            File srcDir = new File(srcSavePath);
            if (!srcDir.exists()) srcDir.mkdirs();
            File srcFile = new File(srcSavePath + File.separator + uniqueName);
            if (!srcFile.exists()) {
                Files.copy(new File(savePath + File.separator + uniqueName).toPath(), srcFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {}

        String relativePath = UPLOAD_DIR + "/" + uniqueName;

        if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.equals(relativePath)) {
            File oldFile = new File(appPath + File.separator + oldImagePath);
            if (oldFile.exists() && oldFile.getParent().equals(new File(savePath).getCanonicalPath())) {
                oldFile.delete();
            }
        }

        return relativePath;
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

    private boolean isAjax(HttpServletRequest req) {
        return "1".equals(req.getParameter("ajax"));
    }

    private void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        resp.getWriter().write(json);
    }

    private String buildProductJson(Product p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,");
        sb.append("\"product\":{");
        sb.append("\"id\":").append(p.getShopProductId()).append(",");
        sb.append("\"title\":\"").append(escapeJson(p.getShopProductTitle())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(p.getShopProductDesc() != null ? p.getShopProductDesc() : "")).append("\",");
        sb.append("\"price\":\"").append(p.getShopProductPrice()).append("\",");
        sb.append("\"group_name\":\"").append(escapeJson(p.getCollection() != null ? p.getCollection() : "")).append("\",");
        sb.append("\"item_type\":\"").append(escapeJson(p.getItemType() != null ? p.getItemType() : "cloth")).append("\",");
        sb.append("\"image_path\":\"").append(escapeJson(p.getProductImage() != null ? p.getProductImage() : "")).append("\"");
        sb.append("},");
        sb.append("\"size_s\":").append(p.getSizeS()).append(",");
        sb.append("\"size_m\":").append(p.getSizeM()).append(",");
        sb.append("\"size_l\":").append(p.getSizeL()).append(",");
        sb.append("\"size_xl\":").append(p.getSizeXL()).append(",");
        sb.append("\"total_stock\":").append(p.getTotalStock());
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String trim(String s) {
        return s != null ? s.trim() : "";
    }

    private int parseInt(String val) {
        if (val == null || val.trim().isEmpty()) return 0;
        try { return Integer.parseInt(val.trim()); } catch (Exception e) { return 0; }
    }

    private int parseIntDefault(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) return defaultVal;
        try { return Integer.parseInt(val.trim()); } catch (Exception e) { return defaultVal; }
    }
}
