package petra.model;

public class IssueChat {
    private int issueChatId;
    private int issueId;
    private Integer staffId;
    private Integer custId;
    private String chatText;
    private String createdTime;
    private String issueAttachment;
    private String updatedTime;
    private Integer replyToChatId;
    private String senderName;
    private String senderRole;
    private String replyChatText;
    private String replySenderName;
    private String replySenderRole;

    public IssueChat() {}
    public int getIssueChatId() { return issueChatId; }
    public void setIssueChatId(int issueChatId) { this.issueChatId = issueChatId; }
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public Integer getCustId() { return custId; }
    public void setCustId(Integer custId) { this.custId = custId; }
    public String getChatText() { return chatText; }
    public void setChatText(String chatText) { this.chatText = chatText; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public String getIssueAttachment() { return issueAttachment; }
    public void setIssueAttachment(String issueAttachment) { this.issueAttachment = issueAttachment; }
    public String getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }
    public Integer getReplyToChatId() { return replyToChatId; }
    public void setReplyToChatId(Integer replyToChatId) { this.replyToChatId = replyToChatId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getReplyChatText() { return replyChatText; }
    public void setReplyChatText(String replyChatText) { this.replyChatText = replyChatText; }
    public String getReplySenderName() { return replySenderName; }
    public void setReplySenderName(String replySenderName) { this.replySenderName = replySenderName; }
    public String getReplySenderRole() { return replySenderRole; }
    public void setReplySenderRole(String replySenderRole) { this.replySenderRole = replySenderRole; }
}
