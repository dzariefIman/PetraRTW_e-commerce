<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.Product" %>
<%
    request.setAttribute("activePage", "home");
    Product p = (Product) request.getAttribute("product");
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", p.getShopProductTitle());
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<a href="<%= ctx %>/home" class="back-link">&larr; Back</a>

<div class="pd-layout">
    <div class="pd-images">
        <% if (p.getProductImage() != null && !p.getProductImage().isEmpty()) { %>
            <div class="pd-img-card">
                <img src="<%= ctx + "/" + p.getProductImage() %>" alt="<%= p.getShopProductTitle() %>">
            </div>
        <% } else { %>
            <div class="pd-img-card pd-placeholder">
                <%= p.getShopProductTitle() != null && !p.getShopProductTitle().isEmpty() ? p.getShopProductTitle().substring(0, 1).toUpperCase() : "P" %>
            </div>
        <% } %>
    </div>

    <div class="pd-info">
        <h1 class="pd-title"><%= p.getShopProductTitle() %></h1>
        <div class="pd-price">RM <%= String.format("%.2f", p.getShopProductPrice()) %></div>
        <hr class="pd-divider">
        <div class="pd-desc"><%= p.getShopProductDesc() != null ? p.getShopProductDesc() : "" %></div>

        <div class="pd-size-section">
            <div class="pd-size-label">Size</div>
            <div class="pd-size-options" id="sizeOptions">
                <button type="button" class="pd-size-btn<%= p.getSizeS() <= 0 ? " oos" : "" %>" data-size="S" data-qty="<%= p.getSizeS() %>">S</button>
                <button type="button" class="pd-size-btn<%= p.getSizeM() <= 0 ? " oos" : "" %>" data-size="M" data-qty="<%= p.getSizeM() %>">M</button>
                <button type="button" class="pd-size-btn<%= p.getSizeL() <= 0 ? " oos" : "" %>" data-size="L" data-qty="<%= p.getSizeL() %>">L</button>
                <button type="button" class="pd-size-btn<%= p.getSizeXL() <= 0 ? " oos" : "" %>" data-size="XL" data-qty="<%= p.getSizeXL() %>">XL</button>
            </div>
            <div id="sizeStockInfo" class="pd-stock-info"></div>
        </div>

        <div class="pd-cart-row">
            <div class="pd-qty-box">
                <button type="button" class="pd-qty-btn" id="qtyMinus">&minus;</button>
                <input type="number" id="quantity" value="1" min="1" class="pd-qty-input" readonly>
                <button type="button" class="pd-qty-btn" id="qtyPlus">+</button>
            </div>
            <button type="button" class="pd-add-btn" id="addToCartBtn">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                Add to cart
            </button>
        </div>
    </div>
</div>

<script>
(function(){
    var selectedSize = null;
    var sizeBtns = document.querySelectorAll('.pd-size-btn');
    var qtyInput = document.getElementById('quantity');
    var qtyMinus = document.getElementById('qtyMinus');
    var qtyPlus = document.getElementById('qtyPlus');
    var addBtn = document.getElementById('addToCartBtn');
    var stockInfo = document.getElementById('sizeStockInfo');

    function getMaxQty() {
        if (!selectedSize) return 999;
        var btn = document.querySelector('.pd-size-btn[data-size="' + selectedSize + '"]');
        return btn ? parseInt(btn.getAttribute('data-qty') || '0', 10) : 999;
    }

    function clampQty() {
        var max = getMaxQty();
        var v = parseInt(qtyInput.value || '1', 10);
        if (v > max) qtyInput.value = max;
        if (v < 1 || isNaN(v)) qtyInput.value = 1;
    }

    sizeBtns.forEach(function(btn) {
        btn.addEventListener('click', function() {
            if (btn.classList.contains('oos')) {
                stockInfo.textContent = 'Out of stock';
                stockInfo.style.color = '#999';
                addBtn.classList.add('sold-out');
                addBtn.innerHTML = 'Sold out';
                addBtn.disabled = true;
                return;
            }
            sizeBtns.forEach(function(b) { b.classList.remove('selected'); });
            btn.classList.add('selected');
            selectedSize = btn.getAttribute('data-size');
            var qty = parseInt(btn.getAttribute('data-qty') || '0', 10);
            stockInfo.textContent = qty + ' available';
            stockInfo.style.color = '#555';
            addBtn.classList.remove('sold-out');
            addBtn.innerHTML = '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg> Add to cart';
            addBtn.disabled = false;
            clampQty();
        });
    });

    qtyInput.addEventListener('input', clampQty);

    qtyMinus.addEventListener('click', function() {
        var v = parseInt(qtyInput.value || '1', 10);
        if (v > 1) qtyInput.value = v - 1;
    });

    qtyPlus.addEventListener('click', function() {
        var v = parseInt(qtyInput.value || '1', 10);
        var max = getMaxQty();
        if (v < max) qtyInput.value = v + 1;
    });

    addBtn.addEventListener('click', function() {
        if (!selectedSize) {
            alert('Please select a size.');
            return;
        }
        var qty = parseInt(qtyInput.value) || 1;
        if (qty < 1) { alert('Quantity must be at least 1.'); return; }

        var form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= ctx %>/cart';

        var fields = {
            'action': 'add',
            'productId': '<%= p.getShopProductId() %>',
            'title': '<%= p.getShopProductTitle().replace("'", "\\'") %>',
            'price': '<%= p.getShopProductPrice() %>',
            'imagePath': '<%= p.getProductImage() != null ? (ctx + "/" + p.getProductImage()).replace("'", "\\'") : "" %>',
            'size': selectedSize,
            'quantity': qty
        };
        for (var key in fields) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = fields[key];
            form.appendChild(input);
        }
        document.body.appendChild(form);
        form.submit();
    });
})();
</script>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
