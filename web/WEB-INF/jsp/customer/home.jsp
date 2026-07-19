<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, petra.model.Product, java.math.BigDecimal" %>
<%
    request.setAttribute("pageTitle", "Home");
    request.setAttribute("activePage", "home");
    Map<String, List<Product>> productGroups = (Map<String, List<Product>>) request.getAttribute("productGroups");
    boolean empty = (productGroups == null || productGroups.isEmpty());
    String ctx = request.getContextPath();
%>
<jsp:include page="/WEB-INF/jsp/customer/_header.jsp" />

<% if (empty) { %>
    <section style="padding:1.2vw;background:#fff;border:1px solid #eee;border-radius:0.6vw">
        <div style="padding:3vw;text-align:center;color:#999">
            <p>No products available yet.</p>
        </div>
    </section>
<% } else { %>
    <% for (Map.Entry<String, List<Product>> entry : productGroups.entrySet()) {
        String groupName = entry.getKey();
        List<Product> products = entry.getValue();
    %>
    <section style="padding:1.2vw;background:#fff;border:1px solid #eee;border-radius:0.6vw;margin-bottom:1.1vw">
        <div style="display:flex;align-items:stretch;gap:2%;width:100%">
            <aside style="flex:0 0 27%;max-width:27%;display:flex;flex-direction:column;justify-content:center;padding:1.2vw 1vw">
                <a href="<%= ctx %>/home?group=<%= java.net.URLEncoder.encode(groupName, "UTF-8") %>" style="text-decoration:none;color:inherit;display:block;margin-bottom:1vw">
                    <h1 style="margin:0;font-size:2.2vw;line-height:1.1"><%= groupName %></h1>
                </a>
                <a href="<%= ctx %>/home?group=<%= java.net.URLEncoder.encode(groupName, "UTF-8") %>" style="display:inline-block;padding:0.75vw 1.6vw;border:1px solid #9f9f9f;background:transparent;color:#5a5a5a;text-decoration:none;font-size:0.78vw;letter-spacing:0.12vw;text-transform:uppercase;white-space:nowrap">Browse All Collections</a>
            </aside>
            <div style="flex:0 0 71%;max-width:71%;overflow:hidden">
                <div class="group-strip" style="display:flex;align-items:flex-start;gap:1.5%;overflow-x:auto;scroll-behavior:smooth;scrollbar-width:none;padding-bottom:0.3vw">
                    <% if (products != null) { for (Product p : products) { %>
                        <div style="flex:0 0 24%;min-width:24%;background:#fff;border:none;border-radius:0.4vw;padding:0;display:flex;flex-direction:column;gap:0.45vw;overflow:hidden">
                            <div style="width:100%;aspect-ratio:3/4;display:flex;align-items:center;justify-content:center;background:#fafafa;overflow:hidden;border-radius:0.4vw;cursor:pointer"
                                 onclick="window.location.href='<%= ctx %>/product?id=<%= p.getShopProductId() %>'">
                                <% if (p.getProductImage() != null && !p.getProductImage().isEmpty()) { %>
                                    <img src="<%= ctx + "/" + p.getProductImage() %>" alt="" style="width:100%;height:100%;object-fit:cover;display:block">
                                <% } else { %>
                                    <div style="color:#999">No image</div>
                                <% } %>
                            </div>
                            <div style="font-weight:700;font-size:0.95vw;line-height:1.25;color:#333"><%= p.getShopProductTitle() %></div>
                            <div style="color:#666;font-size:0.95vw">RM<%= String.format("%.2f", p.getShopProductPrice()) %></div>
                        </div>
                    <% } } %>
                </div>
            </div>
        </div>
    </section>
    <% } %>
<% } %>

<jsp:include page="/WEB-INF/jsp/customer/_footer.jsp" />
