<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.CartDBItem, java.math.BigDecimal" %>
<%
    request.setAttribute("pageTitle", "Shopping Cart");
    request.setAttribute("activePage", "cart");
    List<CartDBItem> cartItems = (List<CartDBItem>) request.getAttribute("cartItems");
    if (cartItems == null) { cartItems = new ArrayList<CartDBItem>(); }

    List<CartDBItem> inStockItems = new ArrayList<CartDBItem>();
    List<CartDBItem> outOfStockItems = new ArrayList<CartDBItem>();
    for (CartDBItem item : cartItems) {
        if (item.isOutOfStock()) {
            outOfStockItems.add(item);
        } else {
            inStockItems.add(item);
        }
    }

    BigDecimal subtotal = BigDecimal.ZERO;
    for (CartDBItem item : inStockItems) {
        subtotal = subtotal.add(item.getSubtotal());
    }
    BigDecimal shipping = BigDecimal.valueOf(4.50);
    BigDecimal total = subtotal.add(shipping);

    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<div class="page-header">
    <h1>Shopping Cart</h1>
</div>

<% if (cartItems.isEmpty()) { %>
    <div class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M 1 1 h 4 l 2.68 13.39 a 2 2 0 0 0 2 1.61 h 9.72 a 2 2 0 0 0 2 -1.61 L 23 6 H 6"/>
        </svg>
        <h3>Your Cart is Empty</h3>
        <p>Start shopping to add items to your cart!</p>
        <a href="<%= ctx %>/home" class="btn btn-primary mt-15">Continue Shopping</a>
    </div>
<% } else { %>
    <div class="cart-layout">
        <div>
            <% if (!inStockItems.isEmpty()) { %>
                <% for (CartDBItem item : inStockItems) { %>
                    <div class="cart-item">
                        <div class="cart-item-image">
                            <% if (item.getProductImage() != null && !item.getProductImage().isEmpty()) { %>
                                <img src="<%= ctx + "/" + item.getProductImage() %>" alt="<%= item.getTitle() %>">
                            <% } else { %>
                                <div class="product-image placeholder" style="width:140px;height:140px;font-size:2rem;">
                                    <%= item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle().substring(0, 1).toUpperCase() : "P" %>
                                </div>
                            <% } %>
                        </div>
                        <div class="cart-item-info">
                            <div class="cart-item-title"><%= item.getTitle() %></div>
                            <div class="cart-item-price">RM <%= String.format("%.2f", item.getPrice()) %></div>
                            <% if (item.getSize() != null && !item.getSize().isEmpty()) { %>
                                <div class="cart-item-meta">Size: <%= item.getSize() %></div>
                            <% } %>
                            <div class="cart-item-meta">Stock: <%= item.getStock() %> available</div>
                            <div class="cart-item-actions">
                                <form action="<%= ctx %>/cart" method="post" class="flex items-center gap-8">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="cartId" value="<%= item.getCartId() %>">
                                    <button type="button" class="qty-btn" onclick="this.nextElementSibling.stepDown();this.form.submit();">&minus;</button>
                                    <input type="number" name="quantity" value="<%= item.getQuantity() %>" min="1" max="<%= item.getStock() %>" class="qty-input" readonly>
                                    <button type="button" class="qty-btn" onclick="this.previousElementSibling.stepUp();this.form.submit();">+</button>
                                </form>
                                <a href="<%= ctx %>/cart?action=remove&cartId=<%= item.getCartId() %>" class="cart-item-remove">Remove</a>
                            </div>
                            <div class="cart-item-meta mt-10">Subtotal: RM <%= String.format("%.2f", item.getSubtotal()) %></div>
                        </div>
                    </div>
                <% } %>
            <% } %>

            <% if (!outOfStockItems.isEmpty()) { %>
                <div class="out-of-stock-section">
                    <div class="out-of-stock-header">Out of Stock</div>
                    <% for (CartDBItem item : outOfStockItems) { %>
                        <div class="cart-item out-of-stock-item">
                            <div class="cart-item-image">
                                <% if (item.getProductImage() != null && !item.getProductImage().isEmpty()) { %>
                                    <img src="<%= ctx + "/" + item.getProductImage() %>" alt="<%= item.getTitle() %>">
                                <% } else { %>
                                    <div class="product-image placeholder" style="width:140px;height:140px;font-size:2rem;">
                                        <%= item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle().substring(0, 1).toUpperCase() : "P" %>
                                    </div>
                                <% } %>
                            </div>
                            <div class="cart-item-info">
                                <div class="cart-item-title"><%= item.getTitle() %></div>
                                <div class="cart-item-price">RM <%= String.format("%.2f", item.getPrice()) %></div>
                                <% if (item.getSize() != null && !item.getSize().isEmpty()) { %>
                                    <div class="cart-item-meta">Size: <%= item.getSize() %></div>
                                <% } %>
                                <div class="cart-item-meta">Qty: <%= item.getQuantity() %></div>
                                <a href="<%= ctx %>/cart?action=remove&cartId=<%= item.getCartId() %>" class="cart-item-remove">Remove</a>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>
        </div>

        <div class="cart-summary">
            <h3>Order Summary</h3>
            <div class="cart-summary-row">
                <span>Subtotal</span>
                <span>RM <%= String.format("%.2f", subtotal) %></span>
            </div>
            <div class="cart-summary-row">
                <span>Shipping</span>
                <span>RM <%= String.format("%.2f", shipping) %></span>
            </div>
            <div class="cart-summary-total">
                <span>Total</span>
                <span>RM <%= String.format("%.2f", total) %></span>
            </div>
            <% if (inStockItems.isEmpty()) { %>
                <button type="button" class="btn btn-block mt-15" style="background:#ccc;color:#666;cursor:not-allowed;" disabled>Proceed to Checkout</button>
            <% } else { %>
                <a href="<%= ctx %>/checkout" class="btn btn-primary btn-block mt-15">Proceed to Checkout</a>
            <% } %>
            <a href="<%= ctx %>/home" class="btn btn-outline btn-block mt-10">Continue Shopping</a>
        </div>
    </div>
<% } %>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />