package petra.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import petra.dao.SalesAdminDAO;
import petra.model.SalesAdmin;

@javax.servlet.annotation.MultipartConfig
public class SalesAdminDashboardServlet extends HttpServlet {

    private SalesAdminDAO salesAdminDAO = new SalesAdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int staffId = (int) session.getAttribute("userId");

        try {
            SalesAdmin staff = salesAdminDAO.findById(staffId);
            req.setAttribute("staff", staff);
            String msg = req.getParameter("msg");
            if (msg != null) {
                req.setAttribute("msg", msg);
                req.setAttribute("msgType", "success");
            }
            req.getRequestDispatcher("/WEB-INF/jsp/salesAdmin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession();
        int staffId = (int) session.getAttribute("userId");
        String msg = null;

        // Read file Part FIRST before getParameter (GlassFish bug workaround)
        Part filePart = null;
        try { filePart = req.getPart("profile_picture"); } catch (Exception e) {}

        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");

        if (name == null || username == null || email == null
                || name.trim().isEmpty() || username.trim().isEmpty() || email.trim().isEmpty()) {
            msg = "Name, username and email are required.";
        } else if (password != null && password.length() > 0 && !password.equals(password2)) {
            msg = "Passwords do not match.";
        }

        String profilePicturePath = null;
        if (msg == null && filePart != null && filePart.getSize() > 0) {
            String fileName = extractFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                msg = "Invalid file.";
            } else {
                String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                Set<String> allowed = new HashSet<String>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));
                if (!allowed.contains(ext)) {
                    msg = "Only image files (jpg, png, gif, webp) are allowed.";
                } else if (filePart.getSize() > 5000000) {
                    msg = "Image file is too large. Maximum size is 5MB.";
                } else {
                    String appPath = req.getServletContext().getRealPath("/");
                    String savePath = appPath + "uploads" + File.separator + "staff";
                    new File(savePath).mkdirs();
                    String uniqueName = "staff_" + staffId + "_" + System.currentTimeMillis() + "." + ext;
                    try (InputStream is = filePart.getInputStream()) {
                        Files.copy(is, new File(savePath + File.separator + uniqueName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    // Also save to source web/ so files survive redeploy
                    try {
                        String srcWebPath = appPath.replace(File.separator + "build" + File.separator + "web" + File.separator, File.separator + "web" + File.separator);
                        String srcSavePath = srcWebPath + "uploads" + File.separator + "staff";
                        File srcDir = new File(srcSavePath);
                        if (!srcDir.exists()) srcDir.mkdirs();
                        File srcFile = new File(srcSavePath + File.separator + uniqueName);
                        if (!srcFile.exists()) {
                            Files.copy(new File(savePath + File.separator + uniqueName).toPath(), srcFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception e) {}
                    profilePicturePath = "uploads/staff/" + uniqueName;
                }
            }
        }

        if (msg == null) {
            try {
                SalesAdmin u = salesAdminDAO.findByUsername(username.trim());
                if (u != null && u.getStaffId() != staffId) {
                    msg = "Username already in use.";
                } else {
                    u = salesAdminDAO.findByEmail(email.trim());
                    if (u != null && u.getStaffId() != staffId) {
                        msg = "Email already in use.";
                    }
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

        if (msg != null) {
            try {
                SalesAdmin staff = salesAdminDAO.findById(staffId);
                staff.setStaffName(name != null ? name.trim() : staff.getStaffName());
                staff.setUsername(username != null ? username.trim() : staff.getUsername());
                staff.setStaffEmail(email != null ? email.trim() : staff.getStaffEmail());
                staff.setStaffAddress(address != null ? address.trim() : "");
                staff.setStaffPhoneNum(phone != null ? phone.trim() : "");
                req.setAttribute("staff", staff);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            req.setAttribute("msg", msg);
            req.setAttribute("msgType", "error");
            req.getRequestDispatcher("/WEB-INF/jsp/salesAdmin/dashboard.jsp").forward(req, resp);
            return;
        }

        try {
            SalesAdmin s = new SalesAdmin();
            s.setStaffId(staffId);
            s.setStaffName(name.trim());
            s.setUsername(username.trim());
            s.setStaffEmail(email.trim());
            s.setStaffAddress(address != null ? address.trim() : "");
            s.setStaffPhoneNum(phone != null ? phone.trim() : "");
            s.setProfilePicture(profilePicturePath);

            boolean changePassword = password != null && password.length() > 0;
            salesAdminDAO.update(s, changePassword, changePassword ? password : null);
            session.setAttribute("username", s.getUsername());
            resp.sendRedirect(req.getContextPath() + "/salesAdmin/home?msg=" + URLEncoder.encode("Profile updated.", "UTF-8"));
        } catch (SQLException e) {
            throw new ServletException(e);
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
