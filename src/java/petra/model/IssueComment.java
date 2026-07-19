package petra.model;

public class IssueComment {
    private int issueCommentsId;
    private int issueId;
    private Integer staffId;
    private String commentText;
    private String createdTime;
    private String issueAttachment;
    private String updatedTime;
    private String senderName;
    private String senderRole;
    private Integer replyToCommentId;
    private String replyComment;
    private String replySenderName;

    public IssueComment() {}
    public int getIssueCommentsId() { return issueCommentsId; }
    public void setIssueCommentsId(int issueCommentsId) { this.issueCommentsId = issueCommentsId; }
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public String getIssueAttachment() { return issueAttachment; }
    public void setIssueAttachment(String issueAttachment) { this.issueAttachment = issueAttachment; }
    public String getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public Integer getReplyToCommentId() { return replyToCommentId; }
    public void setReplyToCommentId(Integer replyToCommentId) { this.replyToCommentId = replyToCommentId; }
    public String getReplyComment() { return replyComment; }
    public void setReplyComment(String replyComment) { this.replyComment = replyComment; }
    public String getReplySenderName() { return replySenderName; }
    public void setReplySenderName(String replySenderName) { this.replySenderName = replySenderName; }
}
