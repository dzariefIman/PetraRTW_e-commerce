package petra.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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
import petra.dao.IssueCommentDAO;
import petra.dao.IssueDAO;
import petra.model.Issue;
import petra.model.IssueComment;

@javax.servlet.annotation.MultipartConfig
public class TicketServlet extends HttpServlet {

    private IssueDAO issueDAO = new IssueDAO();
    private IssueCommentDAO commentDAO = new IssueCommentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");
        String path = req.getServletPath();

        if ("/tickets/detail".equals(path)) {
            int id = 0;
            try { id = Integer.parseInt(req.getParameter("id")); } catch (Exception e) {}
            if (id <= 0) {
                resp.sendRedirect(req.getContextPath() + "/tickets");
                return;
            }

            try {
                Issue issue = issueDAO.findByIdAndCustId(id, customerId);
                if (issue == null) {
                    resp.sendRedirect(req.getContextPath() + "/tickets");
                    return;
                }
                List<IssueComment> comments = commentDAO.findByIssueId(id);
                req.setAttribute("issue", issue);
                req.setAttribute("comments", comments);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/ticketDetail.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else {
            String ticketNumber = req.getParameter("ticket_number");
            String status = req.getParameter("status");
            try {
                List<Issue> tickets = issueDAO.findByCustIdWithFilters(customerId, ticketNumber, status);
                req.setAttribute("tickets", tickets);
                req.getRequestDispatcher("/WEB-INF/jsp/customer/tickets.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession();
        int customerId = (int) session.getAttribute("userId");
        String action = req.getParameter("action");

        if ("send_message".equals(action)) {
            int issueId = 0;
            String replyToStr;

            // Read file Part FIRST before getParameter (GlassFish bug workaround)
            Part filePart = null;
            try { filePart = req.getPart("attachment"); } catch (Exception e) {}

            try { issueId = Integer.parseInt(req.getParameter("issue_id")); } catch (Exception e) {}
            String comment = req.getParameter("comment");

            if (issueId <= 0 || comment == null || comment.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/tickets/detail?id=" + issueId);
                return;
            }

            // Verify ownership
            try {
                Issue issue = issueDAO.findByIdAndCustId(issueId, customerId);
                if (issue == null) {
                    resp.sendRedirect(req.getContextPath() + "/tickets");
                    return;
                }

                String attachmentPath = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = extractFileName(filePart);
                    if (fileName != null && !fileName.isEmpty()) {
                        if (!isImageFile(fileName, filePart.getContentType())) {
                            resp.sendRedirect(req.getContextPath() + "/tickets/detail?id=" + issueId + "&msg=" + URLEncoder.encode("Only JPG and PNG images are allowed.", "UTF-8"));
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
                replyToStr = req.getParameter("reply_to_message_id");
                try { replyTo = Integer.parseInt(replyToStr); } catch (Exception e) {}
                if (replyTo != null && replyTo <= 0) replyTo = null;

                commentDAO.addMessage(issueId, null, comment.trim(), attachmentPath, replyTo);
                resp.sendRedirect(req.getContextPath() + "/tickets/detail?id=" + issueId);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else if ("delete_message".equals(action)) {
            int messageId = 0;
            try { messageId = Integer.parseInt(req.getParameter("message_id")); } catch (Exception e) {}
            int issueId = 0;
            try { issueId = Integer.parseInt(req.getParameter("issue_id")); } catch (Exception e) {}
            try {
                commentDAO.deleteMessage(messageId, issueId);
                resp.sendRedirect(req.getContextPath() + "/tickets/detail?id=" + issueId + "&msg=Message deleted");
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/tickets");
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

    private boolean isImageFile(String fileName, String contentType) {
        String lower = fileName.toLowerCase();
        boolean extOk = lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
        boolean mimeOk = contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
        return extOk && mimeOk;
    }
}
