<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page import="com.sypos.domain.entities.BillLineItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

    String error =
            (String)
                    request.getAttribute(
                            "error"
                    );

%>

<html>

<head>

    <title>SYOS POS - Sale in Progress</title>

    <style>

        body {

            margin: 0;

            padding: 40px;

            font-family: Arial, sans-serif;

            background: #f4f6f9;

            color: #333;
        }

        .container {

            max-width: 1200px;

            margin: auto;
        }

        h1 {

            margin-top: 0;

            color: #2a5298;
        }

        .bill-info {

            background: white;

            padding: 20px;

            border-radius: 12px;

            margin-bottom: 25px;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);
        }

        .error-box {

            background: #ffe5e5;

            color: #c0392b;

            padding: 15px;

            border-radius: 10px;

            margin-bottom: 20px;

            font-weight: bold;
        }

        table {

            width: 100%;

            border-collapse: collapse;

            background: white;

            border-radius: 15px;

            overflow: hidden;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);

            margin-bottom: 25px;
        }

        th {

            background: #2a5298;

            color: white;

            padding: 15px;

            text-align: left;
        }

        td {

            padding: 15px;

            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #f9fbff;
        }

        .remove-btn {

            padding: 8px 12px;

            border: none;

            border-radius: 6px;

            background: #e74c3c;

            color: white;

            font-weight: bold;

            cursor: pointer;
        }

        .remove-btn:hover {
            background: #c0392b;
        }

        .total-section {

            background: white;

            padding: 20px;

            border-radius: 12px;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);

            font-size: 22px;

            font-weight: bold;

            margin-bottom: 25px;
        }

        .form-card {

            background: white;

            padding: 25px;

            border-radius: 12px;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);

            margin-bottom: 25px;
        }

        .form-card h3 {

            margin-top: 0;

            color: #2a5298;
        }

        input {

            width: 100%;

            padding: 12px;

            margin-top: 8px;

            margin-bottom: 20px;

            border: 1px solid #ccc;

            border-radius: 8px;

            box-sizing: border-box;
        }

        button {

            padding: 12px 18px;

            border: none;

            border-radius: 8px;

            font-weight: bold;

            cursor: pointer;
        }

        .add-btn {

            background: #2a5298;

            color: white;
        }

        .add-btn:hover {
            background: #1e3c72;
        }

        .checkout-btn {

            background: #27ae60;

            color: white;

            width: 100%;

            font-size: 16px;
        }

        .checkout-btn:hover {
            background: #219150;
        }

        .empty {

            padding: 20px;

            background: white;

            border-radius: 10px;
        }

        .back-link {

            display: inline-block;

            margin-top: 15px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>SYOS POS Terminal</h1>

    <% if (error != null) { %>

    <div class="error-box">

        <%= error %>

    </div>

    <% } %>

    <%

        Bill bill =
                (Bill)
                        session.getAttribute(
                                "currentBill"
                        );

    %>

    <% if (bill != null) { %>

    <div class="bill-info">

        <strong>Bill Serial:</strong>
        #<%= bill.getSerialNumber() %>

        <br><br>

        <strong>Date:</strong>
        <%= bill.getDate() %>

    </div>

    <table>

        <thead>

        <tr>

            <th>Item Code</th>

            <th>Name</th>

            <th>Qty</th>

            <th>Unit Price</th>

            <th>Total</th>

            <th>Action</th>

        </tr>

        </thead>

        <tbody>

        <% if (bill.getItems().isEmpty()) { %>

        <tr>

            <td colspan="6">

                No items added yet.

            </td>

        </tr>

        <% } else { %>

        <%

            for (int i = 0;
                 i < bill.getItems().size();
                 i++) {

                BillLineItem line =
                        bill.getItems().get(i);

        %>

        <tr>

            <td>
                <%= line.getItem().getCode().getValue() %>
            </td>

            <td>
                <%= line.getItem().getName() %>
            </td>

            <td>
                <%= line.getQuantity().getValue() %>
            </td>

            <td>

                Rs.
                <%= line.getItem()
                        .getUnitPrice()
                        .getAmount() %>

            </td>

            <td>

                Rs.
                <%= line.getLineTotal()
                        .getAmount() %>

            </td>

            <td>

                <form action="pos?action=removeItem"
                      method="POST">

                    <input type="hidden"
                           name="index"
                           value="<%= i %>">

                    <button class="remove-btn"
                            type="submit">

                        Remove

                    </button>

                </form>

            </td>

        </tr>

        <% } %>

        <% } %>

        </tbody>

    </table>

    <div class="total-section">

        Total Amount:
        Rs.
        <%= bill.getTotal().getAmount() %>

    </div>

    <div class="form-card">

        <h3>Add Item</h3>

        <form action="<%= request.getContextPath() %>/pos?action=addItem"
              method="POST">

            <label>

                Item Code

            </label>

            <input type="text"
                   name="itemCode"
                   required>

            <label>

                Quantity

            </label>

            <input type="number"
                   name="quantity"
                   value="1"
                   min="1"
                   required>

            <button class="add-btn"
                    type="submit">

                Add to Bill

            </button>

        </form>

    </div>

    <div class="form-card">

        <h3>Checkout</h3>

        <form action="<%= request.getContextPath() %>/pos?action=checkout"
              method="POST">

            <label>

                Cash Tendered

            </label>

            <input type="number"
                   step="0.01"
                   name="tendered"
                   required>

            <button class="checkout-btn"
                    type="submit">

                Finalize Sale

            </button>

        </form>

    </div>

    <% } else { %>

    <div class="empty">

        No active sale found.

        <br><br>

        <a class="back-link"
           href="<%= request.getContextPath() %>/pos?action=start">

            Click here to start one

        </a>

    </div>

    <% } %>

</div>

</body>

</html>