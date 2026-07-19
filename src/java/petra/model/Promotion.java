package petra.model;

public class Promotion {
    private int adsId;
    private String adsTitle;
    private String adsDesc;
    private String productImage;
    private Integer productId;
    private String startDate;
    private String endDate;
    private String status;
    private String createdTime;
    private String updatedTime;
    private String collection;
    private Integer staffId;

    public Promotion() {}
    public int getAdsId() { return adsId; }
    public void setAdsId(int adsId) { this.adsId = adsId; }
    public String getAdsTitle() { return adsTitle; }
    public void setAdsTitle(String adsTitle) { this.adsTitle = adsTitle; }
    public String getAdsDesc() { return adsDesc; }
    public void setAdsDesc(String adsDesc) { this.adsDesc = adsDesc; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public String getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }
    public String getCollection() { return collection; }
    public void setCollection(String collection) { this.collection = collection; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
}
