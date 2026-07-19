package petra.model;

import java.math.BigDecimal;

public class CartDBItem {
    private int cartId;
    private int custId;
    private int shopProductId;
    private String title;
    private BigDecimal price;
    private String productImage;
    private String size;
    private int quantity;
    private int stock;

    public CartDBItem() {}

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public int getCustId() { return custId; }
    public void setCustId(int custId) { this.custId = custId; }
    public int getShopProductId() { return shopProductId; }
    public void setShopProductId(int shopProductId) { this.shopProductId = shopProductId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public boolean isOutOfStock() { return stock <= 0; }
    public boolean exceedsStock() { return quantity > stock; }
    public BigDecimal getSubtotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
}