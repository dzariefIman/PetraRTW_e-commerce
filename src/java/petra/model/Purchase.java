package petra.model;

import java.math.BigDecimal;

public class Purchase {
    private int purchaseId;
    private int custId;
    private String orderNum;
    private String paymentMethod;
    private String purchaseDesc;
    private String size;
    private int quantity;
    private BigDecimal itemSubTotal;
    private BigDecimal shippingFee;
    private BigDecimal voucherAmount;
    private BigDecimal totalPrice;
    private String productImage;
    private String createdTime;
    private Integer shopProductId;
    private String customerName;
    private String customerPhone;
    private int totalQty;
    private int itemCount;
    private String firstItemDesc;
    private String firstSize;
    private String productTitle;

    public Purchase() {}
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getCustId() { return custId; }
    public void setCustId(int custId) { this.custId = custId; }
    public String getOrderNum() { return orderNum; }
    public void setOrderNum(String orderNum) { this.orderNum = orderNum; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPurchaseDesc() { return purchaseDesc; }
    public void setPurchaseDesc(String purchaseDesc) { this.purchaseDesc = purchaseDesc; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getItemSubTotal() { return itemSubTotal; }
    public void setItemSubTotal(BigDecimal itemSubTotal) { this.itemSubTotal = itemSubTotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public BigDecimal getVoucherAmount() { return voucherAmount; }
    public void setVoucherAmount(BigDecimal voucherAmount) { this.voucherAmount = voucherAmount; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }
    public Integer getShopProductId() { return shopProductId; }
    public void setShopProductId(Integer shopProductId) { this.shopProductId = shopProductId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public int getTotalQty() { return totalQty; }
    public void setTotalQty(int totalQty) { this.totalQty = totalQty; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public String getFirstItemDesc() { return firstItemDesc; }
    public void setFirstItemDesc(String firstItemDesc) { this.firstItemDesc = firstItemDesc; }
    public String getFirstSize() { return firstSize; }
    public void setFirstSize(String firstSize) { this.firstSize = firstSize; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
}
