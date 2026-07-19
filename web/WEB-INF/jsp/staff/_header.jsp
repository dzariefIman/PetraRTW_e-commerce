<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String staffName = (String) session.getAttribute("username");
    if (staffName == null) staffName = "Staff";
    char firstLetter = staffName.toUpperCase().charAt(0);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Staff" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/staff/style.css">
</head>
<body>
<header class="staff-header">
    <div class="brand">Petra<span>RTW</span></div>
    <div class="right">
        <div class="staff-avatar"><%= firstLetter %></div>
        <span class="staff-name"><%= staffName %></span>
        <a href="<%= request.getContextPath() %>/logout" class="logout-link">Sign out</a>
    </div>
</header>
