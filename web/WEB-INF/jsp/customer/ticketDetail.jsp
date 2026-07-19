<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Issue, petra.model.IssueComment" %>
<%
    request.setAttribute("activePage", "tickets");
    Issue issue = (Issue) request.getAttribute("issue");
    List<IssueComment> comments = (List<IssueComment>) request.getAttribute("comments");
    if (comments == null) comments = new ArrayList<IssueComment>();
    String msg = request.getParameter("msg");
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "Ticket #" + (issue != null ? issue.getTicketNum() : ""));
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<a href="<%= ctx %>/tickets" class="back-link" style="margin-bottom:1.25rem">&larr; Back to Tickets</a>

<% if (msg != null && !msg.isEmpty()) { %>
    <div class="page-msg" style="padding:10px 12px;border:1px solid #cfe2ff;background:#eef6ff;color:#084298;border-radius:8px;margin-bottom:16px;font-size:13px"><%= msg %></div>
<% } %>

<div class="ticket-detail-card">
    <div class="ticket-header">
        <div>
            <h1><%= issue.getTitle() != null ? issue.getTitle() : "" %></h1>
            <div style="margin-top:8px;font-size:13px;color:#999">Ticket #<%= issue.getTicketNum() != null ? issue.getTicketNum() : "" %></div>
        </div>
        <span class="ticket-status <%= issue.getStatus() != null ? issue.getStatus() : "New" %>"><%= issue.getStatus() != null ? issue.getStatus() : "New" %></span>
    </div>

    <div class="topic" style="margin-bottom:1.25rem">
        <label class="topic-label" style="font-weight:600;color:#333;margin-bottom:0.5rem;display:block;font-size:0.8125rem;text-transform:uppercase;letter-spacing:0.03125rem">Description</label>
        <div class="topic-content" style="color:#666;line-height:1.6"><%= issue.getDescription() != null ? issue.getDescription().replace("\n", "<br>") : "" %></div>
    </div>

    <% if (issue.getImagePath() != null && !issue.getImagePath().isEmpty()) { %>
        <div class="topic" style="margin-bottom:1.25rem">
            <label class="topic-label" style="font-weight:600;color:#333;margin-bottom:0.5rem;display:block;font-size:0.8125rem;text-transform:uppercase;letter-spacing:0.03125rem">Attachment</label>
            <button type="button" class="attachment-preview-btn js-attachment-preview" data-src="<%= ctx %>/<%= issue.getImagePath() %>" style="display:inline-block;padding:0;border:none;background:none;cursor:pointer">
                <img src="<%= ctx %>/<%= issue.getImagePath() %>" alt="Attachment" style="width:clamp(10rem, 25vw, 11.25rem);max-width:100%;max-height:clamp(10rem, 25vw, 11.25rem);height:auto;object-fit:cover;border-radius:clamp(0.6rem, 1.5vw, 0.75rem);border:1px solid #eee;cursor:pointer">
            </button>
        </div>
    <% } %>

    <div class="topic" style="margin-bottom:1.25rem">
        <label class="topic-label" style="font-weight:600;color:#333;margin-bottom:0.5rem;display:block;font-size:0.8125rem;text-transform:uppercase;letter-spacing:0.03125rem">Submitted On</label>
        <div class="topic-content" style="color:#666;line-height:1.6"><%= issue.getCreatedTime() != null ? issue.getCreatedTime() : "" %></div>
    </div>

    <% if (issue.getOrderNum() != null) { %>
        <div class="topic" style="margin-bottom:1.25rem">
            <label class="topic-label" style="font-weight:600;color:#333;margin-bottom:0.5rem;display:block;font-size:0.8125rem;text-transform:uppercase;letter-spacing:0.03125rem">Order Number</label>
            <div class="topic-content" style="color:#666;line-height:1.6">#<%= issue.getOrderNum() %></div>
        </div>
    <% } %>

    <div class="comments-section" style="margin-top:28px;padding-top:28px;border-top:2px solid #eee">
        <h2 style="margin:0 0 20px;font-size:18px;color:#333">Chat</h2>

        <div class="chat-thread" id="chatThread" data-issue-id="<%= issue.getIssueId() %>" style="border:1px solid #eee;border-radius:8px;">
            <% if (comments.isEmpty()) { %>
                <div class="empty-comments" id="chatEmpty" style="text-align:center;padding:30px;color:#999;font-size:14px">No messages yet. Start the conversation below.</div>
            <% } else { %>
                <% String lastDateKey = ""; %>
                <% int lastStaffSenderId = 0; %>
                <% for (IssueComment c : comments) {
                    String dateKey = c.getCreatedTime() != null && c.getCreatedTime().length() >= 10 ? c.getCreatedTime().substring(0, 10) : "";
                    if (!dateKey.equals(lastDateKey) && !dateKey.isEmpty()) { %>
                        <div class="chat-date-separator"><span><%= dateKey %></span></div>
                    <% lastDateKey = dateKey;
                    }
                    boolean isStaff = (c.getStaffId() != null);
                    boolean isOwn = isStaff ? false : true;
                    String senderName = c.getSenderName() != null ? c.getSenderName() : (isStaff ? "Staff" : "You");
                    String timeStr = c.getCreatedTime() != null && c.getCreatedTime().length() >= 16 ? c.getCreatedTime().substring(11, 16) : "";
                    if (isStaff && c.getStaffId() != null && c.getStaffId().intValue() != lastStaffSenderId) { %>
                        <div class="chat-staff-serve"><span>Staff - <%= senderName %> will serve you.</span></div>
                    <% lastStaffSenderId = c.getStaffId().intValue();
                    }
                %>
                <div class="chat-message <%= isStaff ? "staff" : "customer" %> <%= isOwn ? "own" : "other" %>" id="message-<%= c.getIssueCommentsId() %>" data-message-id="<%= c.getIssueCommentsId() %>" data-message-sender="<%= senderName.replace("\"", "&quot;") %>" data-message-text="<%= c.getCommentText() != null ? c.getCommentText().replace("\"", "&quot;").replace("\n", " ") : "" %>">
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
                                <button type="button" class="attachment-preview-btn js-attachment-preview" data-src="<%= attSrc %>" style="display:inline-block;padding:0;border:none;background:none;cursor:pointer">
                                    <img src="<%= attSrc %>" alt="Attachment" style="width:180px;max-width:100%;max-height:180px;height:auto;object-fit:cover;border-radius:10px;display:block;cursor:pointer">
                                </button>
                            <% } else { %>
                                <a href="<%= attSrc %>" target="_blank" rel="noopener">View attachment</a>
                            <% } %>
                        </div>
                    <% } %>
                    <span class="chat-time-inline"><%= timeStr %></span>
                    <div class="message-side-menu">
                        <div class="comment-menu">
                            <button type="button" class="comment-menu-btn" onclick="toggleCommentMenu(<%= c.getIssueCommentsId() %>)" aria-label="Message options">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><circle cx="12" cy="5" r="2"/><circle cx="12" cy="12" r="2"/><circle cx="12" cy="19" r="2"/></svg>
                            </button>
                            <div class="comment-menu-dropdown" id="menu-<%= c.getIssueCommentsId() %>">
                                <div class="comment-menu-item" onclick="setReplyTarget(<%= c.getIssueCommentsId() %>)">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><polyline points="9 17 4 12 9 7"/><path d="M20 18v-2a4 4 0 0 0-4-4H4"/></svg>
                                    Reply
                                </div>
                                <% if (!isStaff) { %>
                                    <form method="post" action="<%= ctx %>/tickets/detail" class="comment-menu-form" onsubmit="return confirm('Delete this message?');">
                                        <input type="hidden" name="action" value="delete_message">
                                        <input type="hidden" name="issue_id" value="<%= issue.getIssueId() %>">
                                        <input type="hidden" name="message_id" value="<%= c.getIssueCommentsId() %>">
                                        <button type="submit" class="comment-menu-item delete">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                                            Delete
                                        </button>
                                    </form>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            <% } %>
        </div>

        <form class="chat-compose" method="post" action="<%= ctx %>/tickets/detail" enctype="multipart/form-data" id="chatForm">
            <input type="hidden" name="action" value="send_message">
            <input type="hidden" name="issue_id" value="<%= issue.getIssueId() %>">
            <input type="hidden" name="reply_to_message_id" id="replyToMessageId" value="">
            <button type="button" class="chat-attach-btn" id="attachBtn" aria-label="Attach file" title="Attach file">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21.44 11.05l-8.49 8.49a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
            </button>
            <input type="file" name="attachment" class="chat-file" id="chatFile" accept="image/jpeg,image/png">
            <div style="flex:1">
                <div class="reply-target" id="replyTarget">
                    <div class="reply-target-meta">
                        <span class="reply-target-label" id="replyTargetLabel"></span>
                        <span class="reply-target-text" id="replyTargetText"></span>
                    </div>
                    <button type="button" class="reply-target-close" id="replyTargetClose" aria-label="Cancel reply">&times;</button>
                </div>
                <textarea name="comment" id="chatInput" class="chat-input" placeholder="Type a message..." rows="1" required style="width:100%;padding:12px 14px;border:1px solid #ddd;border-radius:14px;font:inherit;resize:vertical;min-height:48px;box-sizing:border-box"></textarea>
                <div class="chat-file-name" id="chatFileName"></div>
            </div>
            <button type="submit" class="send-btn" aria-label="Send message" title="Send message">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
            </button>
        </form>
    </div>
</div>

<div class="attachment-modal" id="attachmentModal" aria-hidden="true">
    <div class="attachment-modal-box">
        <a class="attachment-modal-download" id="attachmentModalDownload" href="#" download aria-label="Download attachment" title="Download attachment">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        </a>
        <button type="button" class="attachment-modal-close" id="attachmentModalClose" aria-label="Close preview">&times;</button>
        <img id="attachmentModalImage" src="" alt="Attachment preview">
    </div>
</div>

<script>
(function() {
    var thread = document.getElementById('chatThread');
    if (!thread) return;

    var issueId = thread.dataset.issueId;
    var form = document.getElementById('chatForm');
    var input = document.getElementById('chatInput');
    var fileInput = document.getElementById('chatFile');
    var fileName = document.getElementById('chatFileName');
    var attachBtn = document.getElementById('attachBtn');
    var modal = document.getElementById('attachmentModal');
    var modalImage = document.getElementById('attachmentModalImage');
    var modalClose = document.getElementById('attachmentModalClose');
    var modalDownload = document.getElementById('attachmentModalDownload');
    var replyInput = document.getElementById('replyToMessageId');
    var replyTarget = document.getElementById('replyTarget');
    var replyTargetLabel = document.getElementById('replyTargetLabel');
    var replyTargetText = document.getElementById('replyTargetText');
    var replyTargetClose = document.getElementById('replyTargetClose');

    function openAttachmentModal(src) {
        if (!src || !modal || !modalImage) return;
        modalImage.src = src;
        if (modalDownload) { modalDownload.href = src; modalDownload.download = src.split('/').pop() || 'attachment'; }
        modal.classList.add('open');
        modal.setAttribute('aria-hidden', 'false');
    }

    function closeAttachmentModal() {
        if (!modal || !modalImage) return;
        modal.classList.remove('open');
        modal.setAttribute('aria-hidden', 'true');
        modalImage.src = '';
    }

    function truncateReplyText(value) {
        var text = (value || '').replace(/\s+/g, ' ').trim();
        return text.length <= 90 ? text : text.slice(0, 90) + '...';
    }

    window.setReplyTarget = function(messageId) {
        var source = document.getElementById('message-' + messageId);
        if (!source || !replyInput || !replyTarget || !replyTargetLabel || !replyTargetText) return;
        replyInput.value = String(messageId);
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

    // Attachment preview click handler
    document.addEventListener('click', function(event) {
        var trigger = event.target.closest('.js-attachment-preview');
        if (trigger) {
            event.preventDefault();
            openAttachmentModal(trigger.dataset.src || (trigger.tagName === 'IMG' ? trigger.src : ''));
        }
    });

    if (modalClose) modalClose.addEventListener('click', closeAttachmentModal);
    if (modal) modal.addEventListener('click', function(event) {
        if (event.target === modal) closeAttachmentModal();
    });

    // Jump to replied message
    document.addEventListener('click', function(e) {
        var replyJumpTrigger = e.target.closest('.js-jump-reply');
        if (replyJumpTrigger) {
            var targetId = replyJumpTrigger.dataset.targetId;
            var targetMessage = document.getElementById('message-' + targetId);
            if (targetMessage) {
                var targetTop = targetMessage.offsetTop - thread.offsetTop;
                var desiredTop = targetTop - (thread.clientHeight / 2) + (targetMessage.clientHeight / 2);
                thread.scrollTo({ top: Math.max(0, desiredTop), behavior: 'smooth' });
                targetMessage.classList.add('reply-highlight');
                setTimeout(function() { targetMessage.classList.remove('reply-highlight'); }, 1400);
            }
        }
    });

    // Close menus when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.comment-menu')) {
            document.querySelectorAll('.comment-menu-dropdown').forEach(function(m) { m.classList.remove('open'); });
        }
    });

    // Escape key
    document.addEventListener('keydown', function(event) {
        if ((event.key === 'Enter' || event.key === ' ') && event.target.classList && event.target.classList.contains('js-jump-reply')) {
            event.preventDefault();
            event.target.click();
            return;
        }
        if (event.key === 'Escape') closeAttachmentModal();
    });

    // Attach button
    if (attachBtn) {
        attachBtn.addEventListener('click', function() { fileInput.click(); });
    }

    // File input change
    if (fileInput) {
        fileInput.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                fileName.textContent = this.files[0].name;
                fileName.classList.add('show');
            } else {
                fileName.textContent = '';
                fileName.classList.remove('show');
            }
        });
    }

    // Auto-resize textarea
    if (input) {
        input.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = Math.min(this.scrollHeight, 120) + 'px';
        });
    }

    // Scroll to bottom
    function scrollToBottom() {
        thread.scrollTop = thread.scrollHeight;
    }
    scrollToBottom();
    window.addEventListener('load', scrollToBottom);

    // Form submit
    if (form) {
        form.addEventListener('submit', function() {
            if (input) input.readOnly = true;
        });
    }

    // Polling
    var lastMessageCount = thread.querySelectorAll('.chat-message').length;
    setInterval(async function() {
        try {
            var resp = await fetch('<%= ctx %>/issueChat?action=list&issue_id=' + encodeURIComponent(issueId), { headers: { 'Accept': 'application/json' } });
            if (!resp.ok) return;
            var data = await resp.json();
            if (data.ok && Array.isArray(data.messages) && data.messages.length !== lastMessageCount) {
                location.reload();
            }
        } catch(e) {}
    }, 3000);
})();

function toggleCommentMenu(commentId) {
    document.querySelectorAll('.comment-menu-dropdown').forEach(function(menu) {
        if (menu.id !== 'menu-' + commentId) menu.classList.remove('open');
    });
    var menu = document.getElementById('menu-' + commentId);
    if (menu) menu.classList.toggle('open');
}
</script>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />