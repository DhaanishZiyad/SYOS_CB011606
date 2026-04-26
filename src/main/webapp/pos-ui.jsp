<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page import="com.sypos.domain.entities.BillLineItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SYOS POS - Sale in Progress</title>
    <style>
        table { width: 50%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 8px; text-align: left; border: 1px solid #ddd; }
        .total-section { font-weight: bold; font-size: 1.2em; }
    </style>
</head>
<body>
<h1>Current Sale</h1>

<%
    Bill bill = (Bill) session.getAttribute("currentBill");
    if (bill != null) {
%>
<p><strong>Bill Serial:</strong> <%= bill.getSerialNumber() %> | <strong>Date:</strong> <%= bill.getDate() %></p>

<table>
    <thead>
    <tr>
        <th>Item Code</th>
        <th>Name</th>
        <th>Qty</th>
        <th>Unit Price</th>
        <th>Total</th>
    </tr>
    </thead>
    <tbody>
    <% for (BillLineItem line : bill.getItems()) { %>
    <tr>
        <td><%= line.getItem().getCode().getValue() %></td>
        <td><%= line.getItem().getName() %></td>
        <td><%= line.getQuantity().getValue() %></td>
        <td><%= line.getItem().getUnitPrice().getAmount() %></td>
        <td><%= line.getLineTotal().getAmount() %></td>
    </tr>
    <% } %>
    </tbody>
</table>

<div class="total-section">
    Total Amount: <%= bill.getTotal().getAmount() %>
</div>

<hr>
<h3>Add Item</h3>
<form action="pos?action=addItem" method="POST">
    Item Code: <input type="text" name="itemCode" required>
    Quantity: <input type="number" name="quantity" value="1" min="1" required>
    <button type="submit">Add to Bill</button>
</form>

<hr>
<h3>Checkout</h3>
<form action="pos?action=checkout" method="POST">
    Cash Tendered: <input type="number" step="0.01" name="tendered" required>
    <button type="submit" style="background-color: #4CAF50; color: white;">Finalize Sale</button>
</form>

<% } else { %>
<p>No active sale found. <a href="pos?action=start">Click here to start one.</a></p>
<% } %>
</body>
</html>