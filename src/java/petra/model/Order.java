package petra.model;

import java.math.BigDecimal;

public class Order {
    private int orderId;
    private int custId;
    private String orderNum;
    private String paymentMethod;
    private String itemDesc;
    private String size;
    private int quantity;
    private BigDecimal itemSubTotal;
    private BigDecimal shippingFee;
    private BigDecimal voucherAmount;
    private BigDecimal totalPrice;
    private String productImage;
    private String createdTime;
    private Integer productId;
    private String customerName;
    private String customerPhone;
    private int totalQty;
    private int itemCount;
    private String firstItemDesc;
    private String firstSize;
    private String productTitle;
    private String orderStatus;

    public Order() {}

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustId() { return custId; }
    public void setCustId(int custId) { this.custId = custId; }

    public String getOrderNum() { return orderNum; }
    public void setOrderNum(String orderNum) { this.orderNum = orderNum; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getItemDesc() { return itemDesc; }
    public void setItemDesc(String itemDesc) { this.itemDesc = itemDesc; }

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

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

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

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}
