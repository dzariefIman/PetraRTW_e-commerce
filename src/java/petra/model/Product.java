package petra.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productTitle;
    private BigDecimal productPrice;
    private String productDesc;
    private String productImage;
    private String createdTime;
    private String collection;
    private int sizeS;
    private int sizeM;
    private int sizeL;
    private int sizeXL;
    private String itemType;
    private Integer advertisementId;

    public Product() {}
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public String getProductDesc() { return productDesc; }
    public void setProductDesc(String productDesc) { this.productDesc = productDesc; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public String getCollection() { return collection; }
    public void setCollection(String collection) { this.collection = collection; }
    public int getSizeS() { return sizeS; }
    public void setSizeS(int sizeS) { this.sizeS = sizeS; }
    public int getSizeM() { return sizeM; }
    public void setSizeM(int sizeM) { this.sizeM = sizeM; }
    public int getSizeL() { return sizeL; }
    public void setSizeL(int sizeL) { this.sizeL = sizeL; }
    public int getSizeXL() { return sizeXL; }
    public void setSizeXL(int sizeXL) { this.sizeXL = sizeXL; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public Integer getAdvertisementId() { return advertisementId; }
    public void setAdvertisementId(Integer advertisementId) { this.advertisementId = advertisementId; }
    public int getTotalStock() { return sizeS + sizeM + sizeL + sizeXL; }
    public int getStockForSize(String size) {
        if ("S".equals(size)) return sizeS;
        if ("M".equals(size)) return sizeM;
        if ("L".equals(size)) return sizeL;
        if ("XL".equals(size)) return sizeXL;
        return 0;
    }
}
