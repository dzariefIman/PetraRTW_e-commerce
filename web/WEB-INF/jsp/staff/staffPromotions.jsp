<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Promotion, petra.model.Product, java.text.SimpleDateFormat" %>
<%
    request.setAttribute("pageTitle", "Promotions");
    request.setAttribute("activePage", "promotions");
    List<Promotion> drafts = (List<Promotion>) request.getAttribute("drafts");
    if (drafts == null) drafts = new ArrayList<Promotion>();
    List<Promotion> published = (List<Promotion>) request.getAttribute("published");
    if (published == null) published = new ArrayList<Promotion>();
    List<Product> products = (List<Product>) request.getAttribute("products");
    if (products == null) products = new ArrayList<Product>();
    String msg = (String) request.getAttribute("msg");
    String ctx = request.getContextPath();
    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
%>
<jsp:include page="/WEB-INF/jsp/staff/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/staff/_sidebar.jsp" />
    <main class="staff-main">
        <h2>Manage Promotion</h2>

        <% if (msg != null && !msg.isEmpty()) { %>
            <div class="msg msg-success"><%= msg %></div>
        <% } %>

        <div class="card">
            <h2>Upload Promotion Content</h2>
            <form method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label>Promotion Title</label>
                    <input type="text" name="title" required class="form-input">
                </div>
                <div class="form-group">
                    <label>Description</label>
                    <textarea name="description" required class="form-textarea"></textarea>
                </div>
                <div class="form-inline-group">
                    <div class="form-group flex-1">
                        <label>Start Date</label>
                        <input type="date" name="start_date" required class="form-input">
                    </div>
                    <div class="form-group flex-1">
                        <label>End Date</label>
                        <input type="date" name="end_date" required class="form-input">
                    </div>
                </div>
                <div class="form-inline-group">
                    <div class="form-group flex-1">
                        <label>Page Link (Product)</label>
                        <select name="product_id" id="productSelect" onchange="toggleFields()" class="form-select">
                            <option value="">-- Select Product --</option>
                            <% for (Product prod : products) { %>
                                <option value="<%= prod.getShopProductId() %>"><%= prod.getShopProductTitle() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group flex-1">
                        <label>Or Select Group</label>
                        <select name="group_name" id="groupSelect" onchange="toggleFields()" class="form-select">
                            <option value="">-- Select Group --</option>
                            <option value="Newly Released">Newly Released</option>
                            <option value="Lebaran 2026">Lebaran 2026</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label>Upload File</label>
                    <input type="file" name="image" accept="image/*" class="form-input upload-input">
                </div>
                <div class="form-actions form-actions-submit">
                    <button type="submit" name="action" value="draft" class="btn btn-sm btn-cancel btn-draft">Submit</button>
                </div>
            </form>
        </div>

        <% if (!drafts.isEmpty()) { %>
        <div class="card promotion-card-section">
            <h2>Draft Promotions</h2>
            <div class="promotion-grid">
                <% for (Promotion draft : drafts) {
                    String startFmt = "", endFmt = "";
                    try { startFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(draft.getStartDate())); } catch (Exception e) { startFmt = draft.getStartDate(); }
                    try { endFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(draft.getEndDate())); } catch (Exception e) { endFmt = draft.getEndDate(); }
                    String desc = draft.getAdsDesc();
                    if (desc != null && desc.length() > 80) desc = desc.substring(0, 80) + "...";
                %>
                    <div class="promotion-card draft">
                        <% if (draft.getProductImage() != null && !draft.getProductImage().isEmpty()) { %>
                            <div class="promotion-image">
                                <img src="<%= draft.getProductImage().startsWith("http") ? draft.getProductImage() : ctx + "/" + draft.getProductImage() %>" alt="<%= draft.getAdsTitle() %>">
                            </div>
                        <% } else { %>
                            <div class="promotion-image no-image">No image</div>
                        <% } %>
                        <div class="promotion-body">
                            <div class="promotion-badge draft">DRAFT</div>
                            <div class="promotion-title"><%= draft.getAdsTitle() %></div>
                            <div class="promotion-desc"><%= desc != null ? desc.replace("\n", "<br>") : "" %></div>
                            <div class="promotion-date">Valid from: <strong><%= startFmt %></strong></div>
                            <div class="promotion-date">Until: <strong><%= endFmt %></strong></div>
                            <div class="promotion-actions">
                                <button type="button" class="action-btn btn-edit" onclick="openEditModal(<%= draft.getAdsId() %>, '<%= draft.getAdsTitle().replace("'", "\\'") %>', '<%= draft.getAdsDesc() != null ? draft.getAdsDesc().replace("'", "\\'").replace("\n", "\\n") : "" %>', '<%= draft.getStartDate() %>', '<%= draft.getEndDate() %>', '<%= draft.getProductId() != null ? draft.getProductId() : "" %>', '<%= draft.getCollection() != null ? draft.getCollection().replace("'", "\\'") : "" %>')">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg> Edit
                                </button>
                                <form method="post">
                                    <input type="hidden" name="publish_id" value="<%= draft.getAdsId() %>">
                                    <button type="submit" class="action-btn btn-publish" onclick="return confirm('Publish this promotion?')">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg> Publish
                                    </button>
                                </form>
                                <form method="post" id="deleteForm<%= draft.getAdsId() %>">
                                    <input type="hidden" name="delete_id" value="<%= draft.getAdsId() %>">
                                    <button type="button" class="action-btn btn-delete" onclick="confirmDelete(<%= draft.getAdsId() %>)">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg> Delete
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
        <% } %>

        <% if (!published.isEmpty()) { %>
        <div class="card promotion-card-section">
            <h2>Current Promotions</h2>
            <div class="promotion-grid">
                <% for (Promotion promo : published) {
                    String startFmt = "", endFmt = "";
                    try { startFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(promo.getStartDate())); } catch (Exception e) { startFmt = promo.getStartDate(); }
                    try { endFmt = sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(promo.getEndDate())); } catch (Exception e) { endFmt = promo.getEndDate(); }
                    String desc = promo.getAdsDesc();
                    if (desc != null && desc.length() > 80) desc = desc.substring(0, 80) + "...";
                %>
                    <div class="promotion-card">
                        <% if (promo.getProductImage() != null && !promo.getProductImage().isEmpty()) { %>
                            <div class="promotion-image">
                                <img src="<%= promo.getProductImage().startsWith("http") ? promo.getProductImage() : ctx + "/" + promo.getProductImage() %>" alt="<%= promo.getAdsTitle() %>">
                            </div>
                        <% } else { %>
                            <div class="promotion-image no-image">No image</div>
                        <% } %>
                        <div class="promotion-body">
                            <div class="promotion-title"><%= promo.getAdsTitle() %></div>
                            <div class="promotion-desc"><%= desc != null ? desc.replace("\n", "<br>") : "" %></div>
                            <div class="promotion-date">Valid from: <strong><%= startFmt %></strong></div>
                            <div class="promotion-date">Until: <strong><%= endFmt %></strong></div>
                            <div class="promotion-actions">
                                <form method="post" id="deletePubForm<%= promo.getAdsId() %>">
                                    <input type="hidden" name="delete_id" value="<%= promo.getAdsId() %>">
                                    <button type="button" class="action-btn btn-delete" onclick="confirmDelete(<%= promo.getAdsId() %>)">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg> Delete
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>
        </div>
        <% } %>
    </main>
</div>

<div id="deleteModal" class="modal">
    <div class="modal-box modal-box-narrow">
        <h3>Confirm Delete</h3>
        <p class="delete-modal-text">Are you sure you want to delete this promotion? This action cannot be undone.</p>
        <div class="modal-actions">
            <button id="deleteNo" class="btn btn-cancel">Cancel</button>
            <button id="deleteYes" class="btn btn-danger">Yes, Delete</button>
        </div>
    </div>
</div>

<div id="editModal" class="modal">
    <div class="modal-box edit-modal-box">
        <button class="modal-close" onclick="closeEditModal()">&times;</button>
        <h2>Edit Draft Promotion</h2>
        <form method="post" enctype="multipart/form-data">
            <input type="hidden" name="edit_id" id="editId">

            <div class="form-group">
                <label>Promotion Title</label>
                <input type="text" name="title" id="editTitle" required class="form-input">
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="description" id="editDescription" required class="form-textarea"></textarea>
            </div>
            <div class="form-inline-group">
                    <div class="form-group flex-1">
                        <label>Start Date</label>
                        <input type="date" name="start_date" id="editStartDate" required class="form-input">
                    </div>
                    <div class="form-group flex-1">
                        <label>End Date</label>
                        <input type="date" name="end_date" id="editEndDate" required class="form-input">
                    </div>
                </div>
            <div class="form-inline-group">
                <div class="form-group flex-1">
                    <label>Page Link (Product)</label>
                    <select name="product_id" id="editProductId" onchange="toggleEditFields()" class="form-select">
                        <option value="">-- Select Product --</option>
                        <% for (Product prod : products) { %>
                            <option value="<%= prod.getShopProductId() %>"><%= prod.getShopProductTitle() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group flex-1">
                    <label>Or Select Group</label>
                    <select name="group_name" id="editGroupName" onchange="toggleEditFields()" class="form-select">
                        <option value="">-- Select Group --</option>
                        <option value="Newly Released">Newly Released</option>
                        <option value="Lebaran 2026">Lebaran 2026</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <label>Upload New Image (optional)</label>
                <input type="file" name="image" accept="image/*" class="form-input upload-input">
            </div>
            <div class="edit-modal-actions">
                <button type="button" class="btn btn-cancel" onclick="closeEditModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<script>
function toggleFields() {
    var productSelect = document.getElementById('productSelect');
    var groupSelect = document.getElementById('groupSelect');
    if (productSelect.value !== '') {
        groupSelect.disabled = true;
        groupSelect.value = '';
    } else {
        groupSelect.disabled = false;
    }
    if (groupSelect.value !== '') {
        productSelect.disabled = true;
        productSelect.value = '';
    } else {
        productSelect.disabled = false;
    }
}

function toggleEditFields() {
    var productSelect = document.getElementById('editProductId');
    var groupSelect = document.getElementById('editGroupName');
    if (productSelect.value !== '') {
        groupSelect.disabled = true;
        groupSelect.value = '';
    } else {
        groupSelect.disabled = false;
    }
    if (groupSelect.value !== '') {
        productSelect.disabled = true;
        productSelect.value = '';
    } else {
        productSelect.disabled = false;
    }
}

window.addEventListener('DOMContentLoaded', function() {
    toggleFields();
});

function openEditModal(id, title, description, startDate, endDate, productId, groupName) {
    document.getElementById('editId').value = id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editDescription').value = description;
    document.getElementById('editStartDate').value = startDate;
    document.getElementById('editEndDate').value = endDate;
    document.getElementById('editProductId').value = productId || '';
    document.getElementById('editGroupName').value = groupName || '';
    toggleEditFields();
    document.getElementById('editModal').classList.add('open');
}

function closeEditModal() {
    document.getElementById('editModal').classList.remove('open');
}

document.addEventListener('click', function(e) {
    var modal = document.getElementById('editModal');
    if (e.target === modal) {
        closeEditModal();
    }
});

var deleteFormId = null;
function confirmDelete(id) {
    deleteFormId = 'deleteForm' + id;
    var pubForm = document.getElementById('deletePubForm' + id);
    if (pubForm) deleteFormId = 'deletePubForm' + id;
    document.getElementById('deleteModal').classList.add('open');
}

document.getElementById('deleteNo').addEventListener('click', function() {
    document.getElementById('deleteModal').classList.remove('open');
    deleteFormId = null;
});

document.getElementById('deleteYes').addEventListener('click', function() {
    if (deleteFormId) {
        document.getElementById(deleteFormId).submit();
    }
});
</script>
</body>
</html>
