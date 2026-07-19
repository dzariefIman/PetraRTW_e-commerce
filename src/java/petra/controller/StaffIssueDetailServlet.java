package petra.controller;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import petra.dao.IssueCommentDAO;
import petra.dao.IssueDAO;
import petra.model.Issue;
import petra.model.IssueComment;

@javax.servlet.annotation.MultipartConfig
public class StaffIssueDetailServlet extends HttpServlet {

    private IssueDAO issueDAO = new IssueDAO();
    private IssueCommentDAO commentDAO = new IssueCommentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int id = 0;
        try { id = Integer.parseInt(req.getParameter("id")); } catch (Exception e) {}
        if (id <= 0) {
            resp.sendRedirect(req.getContextPath() + "/staff/issues");
            return;
        }

        String msg = req.getParameter("msg");

        try {
            Issue issue = issueDAO.findById(id);
            if (issue == null) {
                resp.sendRedirect(req.getContextPath() + "/staff/issues");
                return;
            }
            List<IssueComment> comments = commentDAO.findByIssueId(id);
            req.setAttribute("issue", issue);
            req.setAttribute("comments", comments);
            if (msg != null && !msg.isEmpty()) req.setAttribute("msg", msg);
            req.getRequestDispatcher("/WEB-INF/jsp/staff/staffIssueDetail.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession();
        int staffId = (int) session.getAttribute("userId");

        // Read file Part FIRST before getParameter (GlassFish bug workaround)
        Part filePart = null;
        try { filePart = req.getPart("attachment"); } catch (Exception e) {}

        String action = req.getParameter("action");
        int issueId = 0;
        try { issueId = Integer.parseInt(req.getParameter("issue_id")); } catch (Exception e) {}

        String msg = "";
        try {
            Issue issue = issueDAO.findById(issueId);
            if (issue == null) {
                resp.sendRedirect(req.getContextPath() + "/staff/issues");
                return;
            }

            if ("change_status".equals(action) || "update_status".equals(action)) {
                String newStatus = req.getParameter("status");
                if (newStatus != null && !newStatus.isEmpty()) {
                    issueDAO.updateStatus(issueId, newStatus);
                }
                msg = "Status updated successfully.";
            }

            if ("send_message".equals(action) || "add_comment".equals(action)) {
                String comment = req.getParameter("comment");
                if (comment != null && !comment.trim().isEmpty()) {
                    String attachmentPath = null;
                    if (filePart != null && filePart.getSize() > 0) {
                        String fileName = extractFileName(filePart);
                        if (fileName != null && !fileName.isEmpty()) {
                            if (!isImageFile(fileName, filePart.getContentType())) {
                                resp.sendRedirect(req.getContextPath() + "/staff/issues/detail?id=" + issueId + "&msg=" + URLEncoder.encode("Only JPG and PNG images are allowed.", "UTF-8"));
                                return;
                            }
                            String appPath = req.getServletContext().getRealPath("/");
                            String savePath = appPath + "uploads" + File.separator + "issues";
                            new File(savePath).mkdirs();
                            String uniqueName = System.currentTimeMillis() + "_" + fileName;
                            try (InputStream is = filePart.getInputStream()) {
                                Files.copy(is, new File(savePath + File.separator + uniqueName).toPath(), StandardCopyOption.REPLACE_EXISTING);
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
                            attachmentPath = "uploads/issues/" + uniqueName;
                        }
                    }

                    Integer replyTo = null;
                    try { replyTo = Integer.parseInt(req.getParameter("reply_to_message_id")); } catch (Exception e) {}
                    if (replyTo != null && replyTo <= 0) replyTo = null;

                    commentDAO.addMessage(issueId, staffId, comment.trim(), attachmentPath, replyTo);
                }
                resp.sendRedirect(req.getContextPath() + "/staff/issues/detail?id=" + issueId);
                return;
            }

            if ("delete_message".equals(action) || "delete_comment".equals(action)) {
                int messageId = 0;
                try { messageId = Integer.parseInt(req.getParameter("message_id")); } catch (Exception e) {}
                if (messageId > 0) {
                    commentDAO.deleteMessage(messageId, issueId);
                    msg = "Comment deleted successfully.";
                }
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/staff/issues/detail?id=" + issueId
            + (msg.isEmpty() ? "" : "&msg=" + java.net.URLEncoder.encode(msg, "UTF-8")));
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

    private boolean isImageFile(String fileName, String contentType) {
        String lower = fileName.toLowerCase();
        boolean extOk = lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
        boolean mimeOk = contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
        return extOk && mimeOk;
    }
}
