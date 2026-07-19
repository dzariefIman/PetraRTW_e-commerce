package petra.model;

public class Issue {
    private int issueId;
    private Integer ticketNum;
    private Integer custId;
    private Integer purchaseId;
    private String orderNum;
    private String title;
    private String issueDesc;
    private String productImage;
    private String status;
    private String closedTime;
    private String createdTime;
    private Integer staffId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String itemDescription;
    private String lastUpdate;

    public Issue() {}
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public Integer getTicketNum() { return ticketNum; }
    public void setTicketNum(Integer ticketNum) { this.ticketNum = ticketNum; }
    public Integer getCustId() { return custId; }
    public void setCustId(Integer custId) { this.custId = custId; }
    public Integer getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Integer purchaseId) { this.purchaseId = purchaseId; }
    public String getOrderNum() { return orderNum; }
    public void setOrderNum(String orderNum) { this.orderNum = orderNum; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIssueDesc() { return issueDesc; }
    public void setIssueDesc(String issueDesc) { this.issueDesc = issueDesc; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClosedTime() { return closedTime; }
    public void setClosedTime(String closedTime) { this.closedTime = closedTime; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    public String getDescription() { return issueDesc; }
    public void setDescription(String d) { this.issueDesc = d; }
    public String getImagePath() { return productImage; }
    public void setImagePath(String p) { this.productImage = p; }
}
