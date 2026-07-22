<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Product, java.math.BigDecimal" %>
<%
    request.setAttribute("pageTitle", "Product Management");
    request.setAttribute("activePage", "products");

    List<Product> products = (List<Product>) request.getAttribute("products");
    if (products == null) products = new ArrayList<Product>();
    List<String> groups = (List<String>) request.getAttribute("groups");
    if (groups == null) groups = new ArrayList<String>();

    String searchName = (String) request.getAttribute("searchName");
    if (searchName == null) searchName = "";
    String searchGroup = (String) request.getAttribute("searchGroup");
    if (searchGroup == null) searchGroup = "";

    int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
    int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
    int total = request.getAttribute("total") != null ? (Integer) request.getAttribute("total") : 0;

    String flashMsg = (String) request.getAttribute("flashMsg");
    String flashErr = (String) request.getAttribute("flashErr");

    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <% if (flashMsg != null) { %>
            <div class="alert alert-success"><%= flashMsg %></div>
        <% } %>
        <% if (flashErr != null) { %>
            <div class="alert alert-error"><%= flashErr %></div>
        <% } %>

        <div class="header-action">
            <h1>Product Management</h1>
            <button class="btn-add" onclick="openAddModal()">+ Add Product</button>
        </div>

        <form method="get" action="<%= ctx %>/staff/products" class="search-form">
            <div class="search-row">
                <div class="search-field">
                    <label for="search_name">Search by Product Name</label>
                    <input type="text" id="search_name" name="name" placeholder="Enter product name..." value="<%= searchName %>">
                </div>
                <div class="search-field">
                    <label for="search_group">Filter by Group</label>
                    <select id="search_group" name="group">
                        <option value="">All Groups</option>
                        <% for (String g : groups) { %>
                            <option value="<%= g %>" <%= searchGroup.equals(g) ? "selected" : "" %>><%= g %></option>
                        <% } %>
                    </select>
                </div>
                <div class="search-actions">
                    <button type="submit" class="btn-search">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                        Search
                    </button>
                    <% if (!searchName.isEmpty() || !searchGroup.isEmpty()) { %>
                        <a href="<%= ctx %>/staff/products" class="btn-clear">Clear</a>
                    <% } %>
                </div>
            </div>
        </form>

        <% if (products.isEmpty()) { %>
            <div class="empty-state">
                No products found. <a href="#" onclick="openAddModal();return false">Add the first product</a>
            </div>
        <% } else { %>
            <div class="table-wrap">
                <table class="products-table">
                    <thead>
                        <tr>
                            <th>Product Title</th>
                            <th>Group</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Product product : products) {
                            int pid = product.getShopProductId();
                            String itemType = product.getItemType() != null ? product.getItemType() : "cloth";
                        %>
                        <tr data-product-id="<%= pid %>" data-item-type="<%= itemType %>">
                            <td>
                                <div class="product-name"><%= product.getShopProductTitle() %></div>
                            </td>
                            <td><%= product.getCollection() != null && !product.getCollection().isEmpty() ? product.getCollection() : "Uncategorized" %></td>
                            <td class="price-col"><%= String.format("%.2f", product.getShopProductPrice()) %></td>
                            <td>
                                <div class="total-stock-text"><%= product.getTotalStock() %> unit(s)</div>
                                <div class="stock-sizes-line">
                                    <% if ("scarves".equals(itemType)) { %>
                                    <div class="stock-edit-row" data-product-id="<%= pid %>" data-size="S">
                                        <span class="stock-size-label">One Size:</span>
                                        <span class="stock-size-value"><%= product.getSizeS() %></span>
                                    </div>
                                    <% } else { %>
                                    <div class="stock-edit-row" data-product-id="<%= pid %>" data-size="S">
                                        <span class="stock-size-label">S:</span>
                                        <span class="stock-size-value"><%= product.getSizeS() %></span>
                                    </div>
                                    <div class="stock-edit-row" data-product-id="<%= pid %>" data-size="M">
                                        <span class="stock-size-label">M:</span>
                                        <span class="stock-size-value"><%= product.getSizeM() %></span>
                                    </div>
                                    <div class="stock-edit-row" data-product-id="<%= pid %>" data-size="L">
                                        <span class="stock-size-label">L:</span>
                                        <span class="stock-size-value"><%= product.getSizeL() %></span>
                                    </div>
                                    <div class="stock-edit-row" data-product-id="<%= pid %>" data-size="XL">
                                        <span class="stock-size-label">XL:</span>
                                        <span class="stock-size-value"><%= product.getSizeXL() %></span>
                                    </div>
                                    <% } %>
                                </div>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn-edit" data-product='<%= productToJson(product) %>' onclick='openEditModal(this)'>Edit</button>
                                    <button class="btn-delete" onclick="deleteProduct(<%= pid %>)">Delete</button>
                                </div>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>

        <% if (totalPages > 1) { %>
            <div class="pagination-wrap">
                <nav class="pagination" aria-label="Pagination">
                    <% 
                        String baseUrl = ctx + "/staff/products";
                        String qs = "";
                        if (!searchName.isEmpty()) qs += "&name=" + java.net.URLEncoder.encode(searchName, "UTF-8");
                        if (!searchGroup.isEmpty()) qs += "&group=" + java.net.URLEncoder.encode(searchGroup, "UTF-8");
                    %>
                    <% if (currentPage > 1) { %>
                        <a href="<%= baseUrl %>?p=<%= currentPage - 1 %><%= qs %>">&laquo; Prev</a>
                    <% } %>
                    <% for (int i = 1; i <= totalPages; i++) { %>
                        <% if (i == currentPage) { %>
                            <strong><%= i %></strong>
                        <% } else { %>
                            <a href="<%= baseUrl %>?p=<%= i %><%= qs %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    <% if (currentPage < totalPages) { %>
                        <a href="<%= baseUrl %>?p=<%= currentPage + 1 %><%= qs %>">Next &raquo;</a>
                    <% } %>
                </nav>
            </div>
        <% } %>
    </main>
</div>

<!-- Add/Edit Product Modal -->
<div id="productModal" class="modal" aria-hidden="true">
    <div class="modal-box edit-modal-box">
        <button class="modal-close" onclick="closeProductModal()">&times;</button>
        <h2 id="modalTitle" style="margin-top:0">Add Product</h2>
        <form id="productForm" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="action" id="modalAction" value="add">
            <input type="hidden" name="product_id" id="modalProductId" value="">

            <div class="form-group">
                <label for="productTitle">Product Title *</label>
                <input type="text" id="productTitle" name="title" class="form-input" required maxlength="255" placeholder="Enter product title">
                <div class="validation-error" data-error="title"></div>
            </div>

            <div class="form-group">
                <label for="productPrice">Price (RM) *</label>
                <input type="number" id="productPrice" name="price" class="form-input" step="0.01" min="0.01" max="999999.99" required placeholder="0.00">
                <div class="validation-error" data-error="price"></div>
            </div>

            <div class="form-group">
                <label for="productDescription">Description</label>
                <textarea id="productDescription" name="description" class="form-textarea" placeholder="Write product description..."></textarea>
            </div>

            <div class="form-row-2col">
                <div class="form-group">
                    <label for="productGroup">Group/Category</label>
                    <input type="text" id="productGroup" name="group_name" class="form-input" placeholder="e.g., Petra Lebaran 26">
                </div>
                <div class="form-group">
                    <label for="productItemType">Item Type *</label>
                    <select id="productItemType" name="item_type" class="form-select" required onchange="toggleSizeFields()">
                        <option value="cloth">Cloth (S/M/L/XL)</option>
                        <option value="scarves">Scarves (One Size)</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="productImage">Product Image</label>
                <input type="file" id="productImage" name="product_image" accept="image/jpeg,image/png,image/gif,image/webp">
                <div id="imageError" class="image-error"></div>
                <div class="form-hint">Accepted: JPG, PNG, GIF, WebP (max 5MB)</div>
                <div id="currentImage" class="image-preview"></div>
                <input type="hidden" id="productImagePath" name="image_path" value="">
            </div>

            <div class="form-group" id="clothSizes">
                <label>Sizes and Stock</label>
                <div id="sizesContainer" class="sizes-grid">
                    <div class="size-cell">
                        <span class="stock-size-label">S</span>
                        <input type="number" name="size_s" min="0" class="form-input size-input" value="0">
                    </div>
                    <div class="size-cell">
                        <span class="stock-size-label">M</span>
                        <input type="number" name="size_m" min="0" class="form-input size-input" value="0">
                    </div>
                    <div class="size-cell">
                        <span class="stock-size-label">L</span>
                        <input type="number" name="size_l" min="0" class="form-input size-input" value="0">
                    </div>
                    <div class="size-cell">
                        <span class="stock-size-label">XL</span>
                        <input type="number" name="size_xl" min="0" class="form-input size-input" value="0">
                    </div>
                </div>
            </div>

            <div class="form-group" id="scarvesSizes" style="display:none">
                <label>Stock</label>
                <div class="sizes-grid">
                    <div class="size-cell">
                        <span class="stock-size-label">One Size</span>
                        <input type="number" name="size_one" min="0" class="form-input size-input" value="0">
                    </div>
                </div>
            </div>

            <div class="edit-modal-actions">
                <button type="button" class="btn-cancel" onclick="closeProductModal()">Cancel</button>
                <button type="submit" class="btn-save">Save Product</button>
            </div>
        </form>
    </div>
</div>

<!-- Delete Product Modal -->
<div id="deleteProductModal" class="modal" aria-hidden="true">
    <div class="modal-box modal-box-narrow">
        <h3>Delete Product</h3>
        <p>Are you sure you want to delete this product?</p>
        <div class="modal-actions">
            <button class="btn-cancel" onclick="closeDeleteModal()">Cancel</button>
            <button id="deleteYes" class="btn-delete btn-delete-sm">Yes, delete</button>
        </div>
    </div>
</div>

<script>
var pendingDeleteProductId = null;

document.addEventListener('DOMContentLoaded', function(){
    // Form submit via AJAX
    var productForm = document.getElementById('productForm');
    if (productForm) {
        productForm.addEventListener('submit', function(e){
            e.preventDefault();
            if (!validateForm()) return;
            var form = this;
            var fd = new FormData(form);
            fd.append('ajax', '1');
            if (fd.get('item_type') === 'scarves') {
                fd.set('size_s', fd.get('size_one') || '0');
                fd.set('size_m', '0');
                fd.set('size_l', '0');
                fd.set('size_xl', '0');
            }
            var btn = form.querySelector('.btn-save');
            btn.textContent = 'Saving...';
            btn.disabled = true;
            fetch('<%= ctx %>/staff/products', { method: 'POST', body: fd })
                .then(function(resp){ return resp.json(); })
                .then(function(data){
                    btn.textContent = 'Save Product';
                    btn.disabled = false;
                    if (data && data.success) {
                        showTempAlert('Product saved', 'success');
                        closeProductModal();
                        location.reload();
                    } else {
                        alert('Save failed: ' + (data.error || 'Unknown'));
                    }
                })
                .catch(function(err){
                    btn.textContent = 'Save Product';
                    btn.disabled = false;
                    alert('Network error: ' + err.message);
                });
        });
    }

    // Input validation on blur
    document.getElementById('productTitle').addEventListener('blur', function(){
        var err = document.querySelector('[data-error="title"]');
        if (this.value.trim().length < 2) {
            this.classList.add('error');
            if (err) { err.textContent = 'Minimum 2 characters'; err.style.display = 'block'; }
        } else {
            this.classList.remove('error');
            if (err) err.style.display = 'none';
        }
    });
    document.getElementById('productPrice').addEventListener('blur', function(){
        var err = document.querySelector('[data-error="price"]');
        var val = parseFloat(this.value);
        if (!val || val <= 0) {
            this.classList.add('error');
            if (err) { err.textContent = 'Must be greater than 0'; err.style.display = 'block'; }
        } else {
            this.classList.remove('error');
            if (err) err.style.display = 'none';
        }
    });
    document.getElementById('productImage').addEventListener('change', function(){
        var err = document.getElementById('imageError');
        if (this.files.length > 0) {
            var file = this.files[0];
            var validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
            if (validTypes.indexOf(file.type) === -1) {
                this.classList.add('error');
                if (err) { err.textContent = 'Invalid format. Use JPG, PNG, GIF, or WebP'; err.style.display = 'block'; }
            } else if (file.size > 5 * 1024 * 1024) {
                this.classList.add('error');
                if (err) { err.textContent = 'File too large. Maximum 5MB'; err.style.display = 'block'; }
            } else {
                this.classList.remove('error');
                if (err) err.style.display = 'none';
            }
        }
    });

    // Delete modal
    document.getElementById('deleteYes').addEventListener('click', function(){
        if (!pendingDeleteProductId) return;
        var form = document.createElement('form');
        form.method = 'POST';
        form.innerHTML = '<input type="hidden" name="action" value="delete"><input type="hidden" name="product_id" value="' + pendingDeleteProductId + '">';
        document.body.appendChild(form);
        form.submit();
    });
});


function validateForm(){
    var valid = true;
    var title = document.getElementById('productTitle').value.trim();
    var price = parseFloat(document.getElementById('productPrice').value);
    var imageInput = document.getElementById('productImage');
    var titleError = document.querySelector('[data-error="title"]');
    if (!title || title.length < 2) {
        document.getElementById('productTitle').classList.add('error');
        if (titleError) { titleError.textContent = title ? 'Minimum 2 characters' : 'Title is required'; titleError.style.display = 'block'; }
        valid = false;
    } else if (title.length > 255) {
        document.getElementById('productTitle').classList.add('error');
        if (titleError) { titleError.textContent = 'Maximum 255 characters'; titleError.style.display = 'block'; }
        valid = false;
    } else {
        document.getElementById('productTitle').classList.remove('error');
        if (titleError) titleError.style.display = 'none';
    }
    var priceError = document.querySelector('[data-error="price"]');
    if (!price || price <= 0) {
        document.getElementById('productPrice').classList.add('error');
        if (priceError) { priceError.textContent = 'Price must be greater than 0'; priceError.style.display = 'block'; }
        valid = false;
    } else if (price > 999999.99) {
        document.getElementById('productPrice').classList.add('error');
        if (priceError) { priceError.textContent = 'Price is too high'; priceError.style.display = 'block'; }
        valid = false;
    } else {
        document.getElementById('productPrice').classList.remove('error');
        if (priceError) priceError.style.display = 'none';
    }
    var imageError = document.getElementById('imageError');
    if (imageInput.files.length > 0) {
        var file = imageInput.files[0];
        var validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (validTypes.indexOf(file.type) === -1) {
            imageInput.classList.add('error');
            imageError.textContent = 'Invalid format. Use JPG, PNG, GIF, or WebP';
            imageError.style.display = 'block';
            valid = false;
        } else if (file.size > 5 * 1024 * 1024) {
            imageInput.classList.add('error');
            imageError.textContent = 'File too large. Maximum 5MB';
            imageError.style.display = 'block';
            valid = false;
        } else {
            imageInput.classList.remove('error');
            imageError.style.display = 'none';
        }
    } else {
        imageInput.classList.remove('error');
        imageError.style.display = 'none';
    }
    return valid;
}

function openAddModal(){
    document.getElementById('modalAction').value = 'add';
    document.getElementById('modalTitle').textContent = 'Add Product';
    document.getElementById('productForm').reset();
    document.getElementById('modalProductId').value = '';
    document.getElementById('currentImage').style.display = 'none';
    document.getElementById('productItemType').value = 'cloth';
    toggleSizeFields();
    document.querySelector('#clothSizes input[name="size_s"]').value = 0;
    document.querySelector('#clothSizes input[name="size_m"]').value = 0;
    document.querySelector('#clothSizes input[name="size_l"]').value = 0;
    document.querySelector('#clothSizes input[name="size_xl"]').value = 0;
    document.querySelector('#scarvesSizes input[name="size_one"]').value = 0;
    clearErrors();
    openModal('productModal');
}

function openEditModal(btn){
    var product;
    try { product = JSON.parse(btn.getAttribute('data-product')); } catch(e) { return; }
    document.getElementById('modalAction').value = 'edit';
    document.getElementById('modalTitle').textContent = 'Edit Product';
    document.getElementById('modalProductId').value = product.id;
    document.getElementById('productTitle').value = product.title;
    document.getElementById('productPrice').value = product.price;
    document.getElementById('productDescription').value = product.description || '';
    document.getElementById('productGroup').value = product.group_name || '';
    document.getElementById('productItemType').value = product.item_type || 'cloth';
    document.getElementById('productImage').value = '';
    document.getElementById('productImagePath').value = product.image_path || '';
    toggleSizeFields();
    if ((product.item_type || 'cloth') === 'scarves') {
        document.querySelector('#scarvesSizes input[name="size_one"]').value = product.size_s || 0;
    } else {
        document.querySelector('#clothSizes input[name="size_s"]').value = product.size_s || 0;
        document.querySelector('#clothSizes input[name="size_m"]').value = product.size_m || 0;
        document.querySelector('#clothSizes input[name="size_l"]').value = product.size_l || 0;
        document.querySelector('#clothSizes input[name="size_xl"]').value = product.size_xl || 0;
    }
    clearErrors();
    var curImg = document.getElementById('currentImage');
    if (product.image_path && product.image_path.trim() !== '') {
        curImg.innerHTML = '<img src="<%= ctx %>/' + product.image_path + '" alt="">';
        curImg.style.display = 'block';
    } else {
        curImg.style.display = 'none';
    }
    openModal('productModal');
}

function closeProductModal(){
    closeModal('productModal');
}

function clearErrors(){
    document.querySelectorAll('.form-group input, .form-group textarea').forEach(function(el){ el.classList.remove('error'); });
    document.querySelectorAll('.validation-error').forEach(function(el){ el.style.display = 'none'; });
    document.getElementById('imageError').style.display = 'none';
}

function toggleSizeFields(){
    var type = document.getElementById('productItemType').value;
    var clothDiv = document.getElementById('clothSizes');
    var scarvesDiv = document.getElementById('scarvesSizes');
    if (type === 'scarves') {
        clothDiv.style.display = 'none';
        scarvesDiv.style.display = 'block';
    } else {
        clothDiv.style.display = 'block';
        scarvesDiv.style.display = 'none';
    }
}

function openModal(id){
    document.getElementById(id).classList.add('open');
    document.getElementById(id).setAttribute('aria-hidden', 'false');
}

function closeModal(id){
    document.getElementById(id).classList.remove('open');
    document.getElementById(id).setAttribute('aria-hidden', 'true');
}

function deleteProduct(productId){
    pendingDeleteProductId = productId;
    openModal('deleteProductModal');
}

function closeDeleteModal(){
    pendingDeleteProductId = null;
    closeModal('deleteProductModal');
}

function updateTableRow(tr, prod, sizeS, sizeM, sizeL, sizeXL, totalStock){
    var nameDiv = tr.querySelector('.product-name');
    if (nameDiv) nameDiv.textContent = prod.title;
    var children = tr.children;
    children[1].textContent = prod.group_name || 'Uncategorized';
    var priceTd = tr.querySelector('.price-col');
    if (priceTd) priceTd.textContent = parseFloat(prod.price).toFixed(2);
    var stockTd = children[3];
    if (stockTd) {
        var html = '<div class="total-stock-text">' + (parseInt(totalStock,10)||0) + ' unit(s)</div>';
        html += '<div class="stock-sizes-line">';
        html += '<div class="stock-edit-row" data-product-id="' + prod.id + '" data-size="S"><span class="stock-size-label">S:</span><span class="stock-size-value">' + (parseInt(sizeS,10)||0) + '</span></div>';
        html += '<div class="stock-edit-row" data-product-id="' + prod.id + '" data-size="M"><span class="stock-size-label">M:</span><span class="stock-size-value">' + (parseInt(sizeM,10)||0) + '</span></div>';
        html += '<div class="stock-edit-row" data-product-id="' + prod.id + '" data-size="L"><span class="stock-size-label">L:</span><span class="stock-size-value">' + (parseInt(sizeL,10)||0) + '</span></div>';
        html += '<div class="stock-edit-row" data-product-id="' + prod.id + '" data-size="XL"><span class="stock-size-label">XL:</span><span class="stock-size-value">' + (parseInt(sizeXL,10)||0) + '</span></div>';
        html += '</div>';
        stockTd.innerHTML = html;
    }
    // Update data-product attribute on edit button
    var editBtn = tr.querySelector('.btn-edit');
    if (editBtn) {
        var newData = JSON.stringify(prod);
        editBtn.setAttribute('data-product', newData);
    }
}

function escapeHtml(str){
    if (!str) return '';
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(String(str)));
    return d.innerHTML;
}

function showTempAlert(text, type){
    var div = document.createElement('div');
    div.style.position = 'fixed';
    div.style.top = '12px';
    div.style.right = '12px';
    div.style.padding = '10px 14px';
    div.style.borderRadius = '6px';
    div.style.zIndex = 10000;
    div.style.color = '#fff';
    div.style.fontWeight = '600';
    div.style.background = type === 'success' ? '#4CAF50' : '#f44336';
    div.textContent = text;
    document.body.appendChild(div);
    setTimeout(function(){ div.remove(); }, 2200);
}
</script>
<%!
    private String productToJson(Product p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(p.getShopProductId()).append(",");
        sb.append("\"title\":\"").append(escapeJson(p.getShopProductTitle())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(p.getShopProductDesc())).append("\",");
        sb.append("\"price\":\"").append(p.getShopProductPrice()).append("\",");
        sb.append("\"group_name\":\"").append(escapeJson(p.getCollection())).append("\",");
        sb.append("\"item_type\":\"").append(escapeJson(p.getItemType() != null ? p.getItemType() : "cloth")).append("\",");
        sb.append("\"image_path\":\"").append(escapeJson(p.getProductImage())).append("\",");
        sb.append("\"size_s\":").append(p.getSizeS()).append(",");
        sb.append("\"size_m\":").append(p.getSizeM()).append(",");
        sb.append("\"size_l\":").append(p.getSizeL()).append(",");
        sb.append("\"size_xl\":").append(p.getSizeXL());
        sb.append("}");
        return sb.toString();
    }
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
%>
</body>
</html>
