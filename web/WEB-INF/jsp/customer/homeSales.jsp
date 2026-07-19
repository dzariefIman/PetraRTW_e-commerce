<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Product" %>
<%
    request.setAttribute("pageTitle", "Products");
    request.setAttribute("activePage", "home");
    String groupName = (String) request.getAttribute("groupName");
    List<Product> products = (List<Product>) request.getAttribute("products");
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<section style="max-width:68.75rem;margin:1.75rem auto;padding:1.125rem;background:#fff;border:1px solid #eee;border-radius:0.5rem">
    <div style="display:flex;align-items:center;justify-content:space-between;gap:0.75rem;margin-bottom:1.25rem">
        <h1 style="margin:0"><%= groupName %></h1>
        <a href="<%= ctx %>/home" style="padding:0.5rem 1rem;background:#5a3913;color:#fff;text-decoration:none;border-radius:0.375rem;font-weight:600">&larr; BACK</a>
    </div>

    <% if (products == null || products.isEmpty()) { %>
        <div style="padding:clamp(16px,2vw,40px);text-align:center;color:#999">
            <p>No items found in this group.</p>
            <a href="<%= ctx %>/home">Go back to home</a>
        </div>
    <% } else { %>
        <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(13.75rem,1fr));gap:1.125rem">
            <% for (Product p : products) { %>
                <% boolean outOfStock = p.getTotalStock() <= 0; %>
                <% if (outOfStock) { %>
                    <div style="background:#fff;border:1px solid #eee;border-radius:0.375rem;padding:0.75rem;display:flex;flex-direction:column;gap:0.5rem;opacity:0.55;filter:grayscale(0.4)">
                        <div style="height:13.75rem;display:flex;align-items:center;justify-content:center;background:#fafafa;overflow:hidden;border-radius:0.25rem;position:relative">
                            <% if (p.getProductImage() != null && !p.getProductImage().isEmpty()) { %>
                                <img src="<%= ctx + "/" + p.getProductImage() %>" alt="" style="max-width:100%;max-height:100%;object-fit:contain">
                            <% } else { %>
                                <div style="color:#999">No image</div>
                            <% } %>
                            <span style="position:absolute;bottom:6px;left:6px;background:rgba(211,47,47,0.9);color:#fff;padding:2px 8px;border-radius:8px;font-size:11px;font-weight:600">Out of Stock</span>
                        </div>
                        <div style="font-weight:700"><%= p.getShopProductTitle() %></div>
                        <div style="color:#666;font-size:14px">RM <%= String.format("%.2f", p.getShopProductPrice()) %></div>
                    </div>
                <% } else { %>
                    <a href="<%= ctx %>/product?id=<%= p.getShopProductId() %>" style="text-decoration:none;color:inherit;background:#fff;border:1px solid #eee;border-radius:0.375rem;padding:0.75rem;display:flex;flex-direction:column;gap:0.5rem;transition:box-shadow .2s">
                        <div style="height:13.75rem;display:flex;align-items:center;justify-content:center;background:#fafafa;overflow:hidden;border-radius:0.25rem">
                            <% if (p.getProductImage() != null && !p.getProductImage().isEmpty()) { %>
                                <img src="<%= ctx + "/" + p.getProductImage() %>" alt="" style="max-width:100%;max-height:100%;object-fit:contain">
                            <% } else { %>
                                <div style="color:#999">No image</div>
                            <% } %>
                        </div>
                        <div style="font-weight:700"><%= p.getShopProductTitle() %></div>
                        <div style="color:#666;font-size:14px">RM <%= String.format("%.2f", p.getShopProductPrice()) %></div>
                    </a>
                <% } %>
            <% } %>
        </div>
    <% } %>
</section>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
