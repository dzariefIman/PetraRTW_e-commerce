<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="petra.model.SalesAdmin" %>
<%
    request.setAttribute("pageTitle", "Dashboard");
    request.setAttribute("activePage", "dashboard");
    SalesAdmin staff = (SalesAdmin) request.getAttribute("staff");
    if (staff == null) staff = new SalesAdmin();
    String ctx = request.getContextPath();
    String msg = (String) request.getAttribute("msg");
    String msgType = (String) request.getAttribute("msgType");
    if (msgType == null) msgType = "success";
    boolean editMode = "1".equals(request.getParameter("edit"));
%>
<jsp:include page="/WEB-INF/jsp/salesAdmin/_header.jsp" />
<div class="staff-layout">
    <jsp:include page="/WEB-INF/jsp/salesAdmin/_sidebar.jsp" />
    <main class="staff-main">
        <div class="card">
            <% if (editMode) { %>
                <h1>Edit Profile</h1>
                <% if (msg != null) { %>
                    <div class="msg msg-<%= msgType %>"><%= msg %></div>
                <% } %>
                <form action="<%= ctx %>/salesAdmin/home" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="edit" value="1">
                    <div class="form-group">
                        <label>Profile Picture</label>
                        <% if (staff.getProfilePicture() != null && !staff.getProfilePicture().isEmpty()) { %>
                            <div style="margin-bottom:8px">
                                <img src="<%= ctx + "/" + staff.getProfilePicture() %>" style="width:80px;height:80px;border-radius:50%;object-fit:cover">
                            </div>
                        <% } %>
                        <input type="file" name="profile_picture" accept="image/*" class="form-input" style="padding:8px">
                        <div class="form-hint">Maximum 5MB (jpg, png, gif, webp)</div>
                    </div>
                    <div class="form-group">
                        <label>Full name</label>
                        <input type="text" name="name" class="form-input" value="<%= staff.getStaffName() != null ? staff.getStaffName() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Username</label>
                        <input type="text" name="username" class="form-input" value="<%= staff.getUsername() != null ? staff.getUsername() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-input" value="<%= staff.getStaffEmail() != null ? staff.getStaffEmail() : "" %>" required>
                    </div>
                    <div class="form-group">
                        <label>Address</label>
                        <input type="text" name="address" class="form-input" value="<%= staff.getStaffAddress() != null ? staff.getStaffAddress() : "" %>">
                    </div>
                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text" name="phone" class="form-input" value="<%= staff.getStaffPhoneNum() != null ? staff.getStaffPhoneNum() : "" %>">
                    </div>
                    <div class="form-group">
                        <label>New Password (leave blank to keep)</label>
                        <input type="password" name="password" class="form-input">
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <input type="password" name="password2" class="form-input">
                    </div>
                    <div style="display:flex;gap:8px;justify-content:flex-end">
                        <a href="<%= ctx %>/salesAdmin/home" class="btn btn-cancel">Cancel</a>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                </form>
            <% } else { %>
                <h1>Home</h1>
                <% if (msg != null) { %>
                    <div class="msg msg-<%= msgType %>"><%= msg %></div>
                <% } %>
                <div style="margin-bottom:12px;text-align:center">
                    <% if (staff.getProfilePicture() != null && !staff.getProfilePicture().isEmpty()) { %>
                        <img src="<%= ctx + "/" + staff.getProfilePicture() %>" style="width:120px;height:120px;border-radius:50%;object-fit:cover;margin-bottom:12px">
                    <% } else { %>
                        <% 
                            String nameStr = staff.getStaffName();
                            char firstLetter = nameStr != null && !nameStr.isEmpty() ? nameStr.toUpperCase().charAt(0) : 'S';
                        %>
                        <div style="width:120px;height:120px;border-radius:50%;background:#5a3913;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:48px;font-weight:600;margin-bottom:12px"><%= firstLetter %></div>
                    <% } %>
                </div>
                <div style="margin-bottom:12px;line-height:2">
                    <strong>Full name:</strong> <%= staff.getStaffName() != null ? staff.getStaffName() : "" %><br>
                    <strong>Username:</strong> <%= staff.getUsername() != null ? staff.getUsername() : "" %><br>
                    <strong>Email:</strong> <%= staff.getStaffEmail() != null ? staff.getStaffEmail() : "" %><br>
                    <strong>Address:</strong> <%= staff.getStaffAddress() != null ? staff.getStaffAddress() : "" %><br>
                    <strong>Phone:</strong> <%= staff.getStaffPhoneNum() != null ? staff.getStaffPhoneNum() : "" %>
                </div>
                <div style="text-align:right"><a href="<%= ctx %>/salesAdmin/home?edit=1" class="btn btn-primary">Edit Profile</a></div>
            <% } %>
        </div>
    </main>
</div>
</body>
</html>
