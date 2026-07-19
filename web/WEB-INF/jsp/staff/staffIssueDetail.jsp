<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Issue, petra.model.IssueComment" %>
<%
    Issue issue = (Issue) request.getAttribute("issue");
    List<IssueComment> comments = (List<IssueComment>) request.getAttribute("comments");
    if (comments == null) comments = new ArrayList<IssueComment>();
    String msg = (String) request.getAttribute("msg");
    request.setAttribute("pageTitle", issue != null ? "Issue #" + issue.getTicketNum() : "Issue Detail");
    request.setAttribute("activePage", "issues");
    Integer staffId = (Integer) session.getAttribute("userId");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <a href="<%= ctx %>/staff/issues" class="back-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
            BACK
        </a>

        <% if (msg != null && !msg.isEmpty()) { %>
            <div class="msg"><%= msg %></div>
        <% } %>

        <% if (issue == null) { %>
            <p>Issue not found.</p>
        <% } else { %>

        <div class="detail-container">
            <div class="ticket-card">
                <div class="ticket-header">Ticket Issue</div>
                <div class="ticket-body">
                    <% if (issue.getItemDescription() != null && !issue.getItemDescription().isEmpty()) { %>
                        <div class="topic">
                            <span class="topic-label">Item Name</span>
                            <span>&nbsp;&nbsp;: <%= issue.getItemDescription() %></span>
                        </div>
                    <% } %>
                    <div class="topic">
                        <span class="topic-label">Topic</span>
                        <span>&nbsp;&nbsp;: <%= issue.getTitle() != null ? issue.getTitle() : "" %></span>
                    </div>
                    <div class="details">
                        <span class="details-label">Details</span>
                        <span>&nbsp;: <%= issue.getDescription() != null ? issue.getDescription().replace("\n", "<br>") : "" %></span>
                    </div>
                    <% if (issue.getImagePath() != null && !issue.getImagePath().isEmpty()) { %>
                        <div class="attachment">
                            <div class="attachment-label">Attachment :</div>
                            <img src="<%= issue.getImagePath().startsWith("http") ? issue.getImagePath() : ctx + "/" + issue.getImagePath() %>" alt="Issue attachment" class="js-attachment-preview">
                        </div>
                    <% } %>
                </div>
            </div>

            <div class="info-card">
                <form method="post" action="<%= ctx %>/staff/issues/detail">
                    <input type="hidden" name="action" value="update_status">
                    <input type="hidden" name="issue_id" value="<%= issue.getIssueId() %>">
                    <table class="info-table">
                        <tr><td>Ticket No</td><td><%= issue.getTicketNum() != null ? issue.getTicketNum() : "-" %></td></tr>
                        <tr><td>Order No</td><td><%= issue.getOrderNum() != null ? issue.getOrderNum() : "-" %></td></tr>
                        <tr><td>Opening Date</td><td><%= issue.getCreatedTime() != null && issue.getCreatedTime().length() >= 10 ? issue.getCreatedTime().substring(0, 10) : "" %></td></tr>
                        <tr><td>Closed Date</td><td><%= issue.getClosedTime() != null ? issue.getClosedTime().length() >= 10 ? issue.getClosedTime().substring(0, 10) : issue.getClosedTime() : "-" %></td></tr>
                        <tr><td>Name</td><td><%= issue.getCustomerName() != null ? issue.getCustomerName() : "Unknown" %></td></tr>
                        <tr><td>Phone</td><td><%= issue.getCustomerPhone() != null ? issue.getCustomerPhone() : "-" %></td></tr>
                        <tr>
                            <td>Status</td>
                            <td>
                                <input type="hidden" name="status" id="statusInput" value="<%= issue.getStatus() != null ? issue.getStatus() : "New" %>">
                                <div class="status-dropdown" id="statusDropdown">
                                    <button type="button" class="status-dropdown-btn" id="statusBtn">
                                        <span class="status-dot <%= issue.getStatus() != null ? issue.getStatus() : "New" %>"></span>
                                        <span class="status-text" id="statusText"><%= issue.getStatus() != null ? issue.getStatus() : "New" %></span>
                                        <span class="dropdown-arrow">▼</span>
                                    </button>
                                    <div class="status-dropdown-menu" id="statusMenu">
                                        <div class="status-dropdown-item <%= "New".equals(issue.getStatus()) ? "selected" : "" %>" data-value="New"><span class="status-dot New"></span><span>New</span></div>
                                        <div class="status-dropdown-item <%= "Processing".equals(issue.getStatus()) ? "selected" : "" %>" data-value="Processing"><span class="status-dot Processing"></span><span>Processing</span></div>
                                        <div class="status-dropdown-item <%= "Pending".equals(issue.getStatus()) ? "selected" : "" %>" data-value="Pending"><span class="status-dot Pending"></span><span>Pending</span></div>
                                        <div class="status-dropdown-item <%= "Closed".equals(issue.getStatus()) ? "selected" : "" %>" data-value="Closed"><span class="status-dot Closed"></span><span>Closed</span></div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <div class="issue-form-padding">
                        <button type="submit" class="save-btn">Update Status</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="comments-section">
            <div class="comments-header">Chat - <%= issue.getCustomerName() != null ? issue.getCustomerName() : "Customer" %></div>
            <div class="comments-body">
                <div class="chat-thread" id="chatThread" data-issue-id="<%= issue.getIssueId() %>">
                    <% if (comments.isEmpty()) { %>
                        <div class="no-comments" id="chatEmpty">No messages yet.</div>
                    <% } else { %>
                        <% String lastDateKey = ""; %>
                        <% for (IssueComment c : comments) {
                            String dateKey = c.getCreatedTime() != null && c.getCreatedTime().length() >= 10 ? c.getCreatedTime().substring(0, 10) : "";
                            if (!dateKey.equals(lastDateKey) && !dateKey.isEmpty()) { %>
                                <div class="chat-date-separator"><span><%= dateKey %></span></div>
                            <% lastDateKey = dateKey;
                            }
                            boolean isStaff = (c.getStaffId() != null);
                            boolean isOwn = isStaff && staffId != null && c.getStaffId() != null && c.getStaffId().intValue() == staffId.intValue();
                            String senderName = c.getSenderName() != null ? c.getSenderName() : (isStaff ? "Staff" : "Customer");
                            String timeStr = c.getCreatedTime() != null && c.getCreatedTime().length() >= 16 ? c.getCreatedTime().substring(11, 16) : "";
                        %>
                        <div class="chat-message <%= isStaff ? "staff" : "customer" %> <%= isOwn ? "own" : "other" %>" id="message-<%= c.getIssueCommentsId() %>" data-comment-id="<%= c.getIssueCommentsId() %>" data-message-sender="<%= senderName %>" data-message-text="<%= c.getCommentText() != null ? c.getCommentText().replace("\"", "&quot;") : "" %>">
                            <div class="chat-meta">
                                <span class="chat-author"><%= senderName %></span>
                            </div>
                            <% if (c.getReplyToCommentId() != null && c.getReplyComment() != null) { %>
                                <div class="chat-reply-quote js-jump-reply" data-target-id="<%= c.getReplyToCommentId() %>" role="button" tabindex="0" title="Jump to replied message">
                                    <span class="reply-author">Replying to <%= c.getReplySenderName() != null ? c.getReplySenderName() : "User" %></span>
                                    <div class="reply-text"><%= c.getReplyComment() %></div>
                                </div>
                            <% } %>
                            <div class="chat-text"><%= c.getCommentText() != null ? c.getCommentText().replace("\n", "<br>") : "" %></div>
                            <% if (c.getIssueAttachment() != null && !c.getIssueAttachment().isEmpty()) {
                                String attSrc = c.getIssueAttachment().startsWith("http") ? c.getIssueAttachment() : ctx + "/" + c.getIssueAttachment();
                                String ext = c.getIssueAttachment().contains(".") ? c.getIssueAttachment().substring(c.getIssueAttachment().lastIndexOf('.') + 1).toLowerCase() : "";
                                boolean isImage = ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") || ext.equals("webp");
                            %>
                                <div class="chat-attachment">
                                    <% if (isImage) { %>
                                        <img src="<%= attSrc %>" alt="Attachment" class="js-attachment-preview">
                                    <% } else { %>
                                        <a href="<%= attSrc %>" target="_blank">
                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="issue-svg-icon"><path d="M21.44 11.05l-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
                                            View attachment
                                        </a>
                                    <% } %>
                                </div>
                            <% } %>
                            <span class="chat-time-inline"><%= timeStr %></span>
                            <div class="message-side-menu">
                                <div class="comment-menu">
                                    <button type="button" class="comment-menu-btn" onclick="toggleCommentMenu(<%= c.getIssueCommentsId() %>)" aria-label="Message options">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="5" r="2"/><circle cx="12" cy="12" r="2"/><circle cx="12" cy="19" r="2"/></svg>
                                    </button>
                                    <div class="comment-menu-dropdown" id="menu-<%= c.getIssueCommentsId() %>">
                                        <div class="comment-menu-item" onclick="setReplyTarget(<%= c.getIssueCommentsId() %>)">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 17 4 12 9 7"/><path d="M20 18v-2a4 4 0 0 0-4-4H4"/></svg>
                                            Reply
                                        </div>
                                        <% if (isOwn) { %>
                                            <div class="comment-menu-item delete" onclick="confirmDeleteComment(<%= c.getIssueCommentsId() %>)">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                                                Delete
                                            </div>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } %>
                </div>

                <form method="post" action="<%= ctx %>/staff/issues/detail" enctype="multipart/form-data" id="commentForm">
                    <input type="hidden" name="action" value="add_comment">
                    <input type="hidden" name="issue_id" value="<%= issue.getIssueId() %>">
                    <input type="hidden" name="reply_to_message_id" id="replyToMessageId" value="">
                    <div class="chat-compose">
                        <button type="button" class="chat-attach-btn" onclick="document.getElementById('fileInput').click()" title="Attach file" aria-label="Attach file">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.44 11.05l-8.49 8.49a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
                        </button>
                        <input type="file" name="attachment" id="fileInput" class="chat-file" accept="image/jpeg,image/png" onchange="showFileName(this)">
                        <div class="issue-compose-flex">
                            <div class="reply-target" id="replyTarget">
                                <div class="reply-target-meta">
                                    <span class="reply-target-label" id="replyTargetLabel"></span>
                                    <span class="reply-target-text" id="replyTargetText"></span>
                                </div>
                                <button type="button" class="reply-target-close" id="replyTargetClose" aria-label="Cancel reply">&times;</button>
                            </div>
                            <textarea name="comment" class="chat-input comment-input" placeholder="Type a message..." rows="1" required></textarea>
                            <div class="chat-file-name" id="filePreview"></div>
                        </div>
                        <button type="submit" class="send-btn" title="Send">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <% } %>
    </main>
</div>

<div class="attachment-modal" id="attachmentModal" aria-hidden="true">
    <div class="attachment-modal-box">
        <a class="attachment-modal-download" id="attachmentModalDownload" href="#" download aria-label="Download attachment">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        </a>
        <button type="button" class="attachment-modal-close" id="attachmentModalClose" aria-label="Close preview">&times;</button>
        <img id="attachmentModalImage" src="" alt="Attachment preview">
    </div>
</div>

<div id="deleteCommentModal" class="modal" aria-hidden="true">
    <div class="modal-box modal-box-sm">
        <h3>Delete Comment</h3>
        <p>Are you sure you want to delete this comment?</p>
        <form method="post" action="<%= ctx %>/staff/issues/detail" id="deleteCommentForm">
            <input type="hidden" name="action" value="delete_comment">
            <input type="hidden" name="issue_id" value="<%= issue != null ? issue.getIssueId() : "" %>">
            <input type="hidden" name="message_id" id="deleteCommentId">
            <div class="modal-actions">
                <button type="button" class="btn" onclick="closeDeleteModal()">Cancel</button>
                <button type="submit" class="btn btn-danger-sm">Delete</button>
            </div>
        </form>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function(){
    const attachmentModal = document.getElementById('attachmentModal');
    const attachmentModalImage = document.getElementById('attachmentModalImage');
    const attachmentModalClose = document.getElementById('attachmentModalClose');
    const attachmentModalDownload = document.getElementById('attachmentModalDownload');
    const replyInput = document.getElementById('replyToMessageId');
    const replyTarget = document.getElementById('replyTarget');
    const replyTargetLabel = document.getElementById('replyTargetLabel');
    const replyTargetText = document.getElementById('replyTargetText');
    const replyTargetClose = document.getElementById('replyTargetClose');

    function truncateReplyText(value) {
        const text = (value || '').replace(/\s+/g, ' ').trim();
        return text.length <= 90 ? text : text.slice(0, 90) + '...';
    }

    window.setReplyTarget = function(commentId) {
        const source = document.getElementById('message-' + commentId);
        const input = document.querySelector('.comment-input');
        if (!source || !replyInput || !replyTarget || !replyTargetLabel || !replyTargetText) return;
        replyInput.value = String(commentId);
        replyTargetLabel.textContent = 'Replying to ' + (source.dataset.messageSender || 'User');
        replyTargetText.textContent = truncateReplyText(source.dataset.messageText || '');
        replyTarget.classList.add('show');
        document.querySelectorAll('.comment-menu-dropdown').forEach(function(m) { m.classList.remove('open'); });
        if (input) input.focus();
    };

    window.clearReplyTarget = function() {
        if (replyInput) replyInput.value = '';
        if (replyTarget) replyTarget.classList.remove('show');
    };

    if (replyTargetClose) replyTargetClose.addEventListener('click', window.clearReplyTarget);

    function openAttachmentModal(src) {
        if (!src || !attachmentModal || !attachmentModalImage) return;
        attachmentModalImage.src = src;
        if (attachmentModalDownload) {
            attachmentModalDownload.href = src;
            attachmentModalDownload.download = src.split('/').pop() || 'attachment';
        }
        attachmentModal.classList.add('open');
        attachmentModal.setAttribute('aria-hidden', 'false');
    }

    function closeAttachmentModal() {
        if (!attachmentModal || !attachmentModalImage) return;
        attachmentModal.classList.remove('open');
        attachmentModal.setAttribute('aria-hidden', 'true');
        attachmentModalImage.src = '';
        if (attachmentModalDownload) attachmentModalDownload.href = '#';
    }

    document.querySelectorAll('.js-attachment-preview').forEach(function(image) {
        image.addEventListener('click', function() { openAttachmentModal(this.src); });
    });

    if (attachmentModalClose) attachmentModalClose.addEventListener('click', closeAttachmentModal);
    if (attachmentModal) attachmentModal.addEventListener('click', function(event) {
        if (event.target === attachmentModal) closeAttachmentModal();
    });
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') closeAttachmentModal();
        if ((event.key === 'Enter' || event.key === ' ') && event.target.classList && event.target.classList.contains('js-jump-reply')) {
            event.preventDefault();
            event.target.click();
        }
    });

    // Jump to replied message
    document.addEventListener('click', function(e) {
        const replyJumpTrigger = e.target.closest('.js-jump-reply');
        if (replyJumpTrigger) {
            const targetId = replyJumpTrigger.dataset.targetId;
            const targetMessage = document.getElementById('message-' + targetId);
            if (targetMessage) {
                const thread = document.getElementById('chatThread');
                if (thread) {
                    const targetTop = targetMessage.offsetTop - thread.offsetTop;
                    const desiredTop = targetTop - (thread.clientHeight / 2) + (targetMessage.clientHeight / 2);
                    thread.scrollTo({ top: Math.max(0, desiredTop), behavior: 'smooth' });
                }
                targetMessage.classList.add('reply-highlight');
                setTimeout(function() { targetMessage.classList.remove('reply-highlight'); }, 1400);
            }
        }
    });

    // Close comment menus when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.comment-menu')) {
            document.querySelectorAll('.comment-menu-dropdown').forEach(function(m) { m.classList.remove('open'); });
        }
    });

    // Status dropdown
    const statusBtn = document.getElementById('statusBtn');
    const statusMenu = document.getElementById('statusMenu');
    const statusInput = document.getElementById('statusInput');
    const statusText = document.getElementById('statusText');

    if (statusBtn) {
        statusBtn.addEventListener('click', function(e) {
            e.preventDefault();
            statusMenu.classList.toggle('open');
        });
    }
    document.addEventListener('click', function(e) {
        if (statusMenu && !e.target.closest('.status-dropdown')) statusMenu.classList.remove('open');
    });
    document.querySelectorAll('.status-dropdown-item').forEach(function(item) {
        item.addEventListener('click', function() {
            const value = this.dataset.value;
            statusInput.value = value;
            statusText.textContent = value;
            const dot = statusBtn.querySelector('.status-dot');
            dot.className = 'status-dot ' + value;
            document.querySelectorAll('.status-dropdown-item').forEach(function(i) { i.classList.remove('selected'); });
            this.classList.add('selected');
            statusMenu.classList.remove('open');
        });
    });

    // Auto-resize textarea
    const commentInput = document.querySelector('.comment-input');
    if (commentInput) {
        commentInput.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = Math.min(this.scrollHeight, 120) + 'px';
        });
    }

    // Chat thread scroll to bottom
    const chatThread = document.getElementById('chatThread');
    if (chatThread) chatThread.scrollTop = chatThread.scrollHeight;

    // Polling
    if (chatThread) {
        const issueId = chatThread.dataset.issueId;
        let lastMessageCount = chatThread.querySelectorAll('.chat-message').length;
        setInterval(async function() {
            try {
                const resp = await fetch('<%= ctx %>/issueChat?action=list&issue_id=' + encodeURIComponent(issueId), { headers: { 'Accept': 'application/json' } });
                if (!resp.ok) return;
                const data = await resp.json();
                if (data.ok && Array.isArray(data.messages) && data.messages.length !== lastMessageCount) {
                    location.reload();
                }
            } catch(e) {}
        }, 3000);
    }

    // Form submit disable button
    const commentForm = document.getElementById('commentForm');
    if (commentForm) {
        commentForm.addEventListener('submit', function() {
            const field = this.querySelector('textarea[name="comment"]');
            if (field) field.readOnly = true;
        });
    }
});

// Global functions
function toggleCommentMenu(commentId) {
    document.querySelectorAll('.comment-menu-dropdown').forEach(function(menu) {
        if (menu.id !== 'menu-' + commentId) menu.classList.remove('open');
    });
    const menu = document.getElementById('menu-' + commentId);
    if (menu) menu.classList.toggle('open');
}

function confirmDeleteComment(commentId) {
    document.getElementById('deleteCommentId').value = commentId;
    document.getElementById('deleteCommentModal').classList.add('open');
    document.getElementById('deleteCommentModal').setAttribute('aria-hidden', 'false');
    document.querySelectorAll('.comment-menu-dropdown').forEach(function(m) { m.classList.remove('open'); });
}

function closeDeleteModal() {
    document.getElementById('deleteCommentModal').classList.remove('open');
    document.getElementById('deleteCommentModal').setAttribute('aria-hidden', 'true');
}

function showFileName(input) {
    const preview = document.getElementById('filePreview');
    if (input.files && input.files[0]) {
        preview.textContent = input.files[0].name;
        preview.classList.add('show');
    } else {
        preview.textContent = '';
        preview.classList.remove('show');
    }
}
</script>
</body>
</html>
