<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page import="com.sypos.domain.entities.BillLineItem" %>

<%
    Bill bill = (Bill) request.getAttribute("bill");
%>

<html>
<head>
    <title>Bill Details</title>
</head>
<body>

<h1>Bill Details</h1>

<p>
    <strong>Serial:</strong> <%= bill.getSerialNumber() %><br>
    <strong>Date:</strong> <%= bill.getDate() %>
</p>

<table border="1" cellpadding="5">
    <tr>
        <th>Item Code</th>
        <th>Name</th>
        <th>Qty</th>
        <th>Total</th>
    </tr>

    <% for (BillLineItem line : bill.getItems()) { %>
    <tr>
        <td><%= line.getItem().getCode().getValue() %></td>
        <td><%= line.getItem().getName() %></td>
        <td><%= line.getQuantity().getValue() %></td>
        <td><%= line.getLineTotal().getAmount() %></td>
    </tr>
    <% } %>
</table>

<h3>Total: <%= bill.getTotal().getAmount() %></h3>
<h3>Final: <%= bill.getFinalTotal().getAmount() %></h3>

<a href="<%= request.getContextPath() %>/pos?action=viewBills">← Back</a>

</body>
</html>