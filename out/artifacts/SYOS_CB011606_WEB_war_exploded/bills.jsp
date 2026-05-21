<%@ page import="com.sypos.application.dto.reports.BillReport" %>
<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>SYOS POS - All Bills</title>
    <style>
        body { font-family: Arial, sans-serif; }
        table { border-collapse: collapse; width: 70%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .empty { color: gray; margin-top: 20px; }
        .nav { margin-top: 20px; }
    </style>
</head>
<body>

<h1>All Bills</h1>

<%
    BillReport report = (BillReport) request.getAttribute("billReport");
%>

<% if (report == null || report.getBills().isEmpty()) { %>

<p class="empty">No bills found.</p>

<% } else { %>

<table>
    <thead>
    <tr>
        <th>Serial No</th>
        <th>Date</th>
        <th>Total</th>
    </tr>
    </thead>
    <tbody>

    <%
        for (Bill bill : report.getBills()) {
    %>
    <tr>
        <td>
            <a href="<%= request.getContextPath() %>/pos?action=viewBillDetails&serial=<%= bill.getSerialNumber() %>">
                <%= bill.getSerialNumber() %>
            </a>
        </td>
        <td><%= bill.getDate() %></td>
        <td><%= bill.getTotal().getAmount() %></td>
    </tr>
    <%
        }
    %>

    </tbody>
</table>

<% } %>

<div class="nav">
    <a href="<%= request.getContextPath() %>/">⬅ Back to Home</a>
</div>

</body>
</html>