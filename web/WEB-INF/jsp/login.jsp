<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Objects" %>
<%
    String error = (String) request.getAttribute("error");
    boolean registered = Boolean.TRUE.equals(request.getAttribute("registered"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Petra — Login</title>
    <style>
        body{margin:0;min-height:100vh;display:flex;align-items:center;justify-content:center;padding:24px;background:#faf9f6;font-family:system-ui,Segoe UI,Arial,sans-serif}
        .login-box{width:100%;max-width:360px;padding:clamp(16px,2.5vw,24px);border-radius:8px;border:1px solid #eee;background:#fff;position:relative;z-index:1}
        .login-box h2{margin:0 0 12px;font-size:20px;text-align:center;color:#333}
        .msg{color:#a33;text-align:center;margin-top:8px;font-size:14px}
        .msg-success{color:#3a3;text-align:center;margin-top:8px;font-size:14px}
        .role-toggle{display:flex;gap:8px;justify-content:center;margin-bottom:12px}
        .role-btn{padding:8px 16px;border-radius:20px;border:1px solid #e6e6e6;background:#f7f7f7;cursor:pointer;font-family:inherit;font-size:14px;font-weight:600;color:#666;transition:all .12s}
        .role-btn.active{background:#5a3913;color:#fff;border-color:#5a3913}
        .role-btn:hover{opacity:0.85}
        .field{margin-bottom:12px}
        .field input{width:100%;padding:10px;border:1px solid #ddd;border-radius:6px;font-family:inherit;font-size:14px;box-sizing:border-box}
        .field input:focus{outline:none;border-color:#5a3913}
        .actions{display:flex;gap:8px;justify-content:flex-end;align-items:center;margin-top:16px}
        .link-btn{background:transparent;color:#5a3913;text-decoration:none;padding:8px 12px;border-radius:6px;border:1px solid transparent;font-size:14px;font-family:inherit;cursor:pointer}
        .link-btn:hover{background:#f5f5f5}
        .btn{display:inline-flex;align-items:center;gap:6px;padding:10px 24px;border-radius:8px;border:none;font-weight:600;cursor:pointer;font-size:14px;font-family:inherit;text-decoration:none;background:#5a3913;color:#fff;transition:opacity .12s}
        .btn:hover{opacity:0.85}
        .small-link{display:block;text-align:center;margin-top:14px;color:#999;cursor:pointer;font-size:14px}
        .modal{position:fixed;inset:0;background:rgba(0,0,0,.4);display:none;align-items:center;justify-content:center;z-index:9999}
        .modal.open{display:flex}
        .modal-box{background:#fff;padding:18px;border-radius:8px;width:90%;max-width:320px;border:1px solid #eee;z-index:10000}
        .modal-box h3{margin:0 0 12px;text-align:center;color:#333}
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Login</h2>

        <% if (error != null) { %><div class="msg"><%= error %></div><% } %>
        <% if (registered) { %><div class="msg-success">Account created. You can now sign in.</div><% } %>

        <div class="role-toggle" role="tablist">
            <button type="button" id="role-customer" class="role-btn active" onclick="setRole('customer')">CUSTOMER</button>
            <button type="button" id="role-staff" class="role-btn" onclick="setRole('staff')">STAFF</button>
        </div>

        <form action="<%= request.getContextPath() %>/login" method="post">
            <input type="hidden" name="role" id="roleInput" value="customer">
            <div class="field"><input type="text" name="username" placeholder="Username" required></div>
            <div class="field"><input type="password" name="password" placeholder="Password" required></div>
            <div class="actions">
                <a class="link-btn" href="<%= request.getContextPath() %>/">Back</a>
                <button class="btn" type="submit">Sign in</button>
            </div>
        </form>

        <div id="createWrapper">
            <span class="small-link" id="createAccount">Create account.</span>
        </div>
    </div>

    <div id="regModal" class="modal" aria-hidden="true">
        <div class="modal-box">
            <h3>Create Customer Account</h3>
            <form action="<%= request.getContextPath() %>/login" method="post">
                <input type="hidden" name="action" value="register">
                <div class="field"><input type="text" name="name" placeholder="Full name" required></div>
                <div class="field"><input type="text" name="username" placeholder="Username" required></div>
                <div class="field"><input type="email" name="email" placeholder="Email" required></div>
                <div class="field"><input type="text" name="address" placeholder="Address"></div>
                <div class="field"><input type="text" name="phone" placeholder="Phone number"></div>
                <div class="field"><input type="password" name="password" placeholder="Password" required></div>
                <div class="field"><input type="password" name="password2" placeholder="Confirm password" required></div>
                <div style="display:flex;gap:8px;justify-content:flex-end">
                    <button type="button" class="link-btn" id="regCancel">Cancel</button>
                    <button type="submit" class="btn">Create</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function setRole(role) {
            document.getElementById('roleInput').value = role;
            document.getElementById('role-customer').className = 'role-btn' + (role === 'customer' ? ' active' : '');
            document.getElementById('role-staff').className = 'role-btn' + (role === 'staff' ? ' active' : '');
            document.getElementById('createWrapper').style.display = role === 'customer' ? 'block' : 'none';
        }

        document.getElementById('createAccount').addEventListener('click', function() {
            document.getElementById('regModal').classList.add('open');
        });
        document.getElementById('regCancel').addEventListener('click', function() {
            document.getElementById('regModal').classList.remove('open');
        });

        const params = new URLSearchParams(location.search);
        if (params.get('error')) alert(params.get('error'));
        if (params.get('registered')) alert('Account created. You can now sign in.');
    </script>
</body>
</html>
