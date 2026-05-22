<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page import="com.sypos.domain.entities.BillLineItem" %>

<%

    Bill bill =
            (Bill)
                    request.getAttribute(
                            "bill"
                    );

%>

<html>

<head>

    <title>Bill Details</title>

    <style>

        body {

            margin: 0;

            padding: 40px;

            font-family: Arial, sans-serif;

            background: #f4f6f9;

            color: #333;
        }

        .container {

            max-width: 900px;

            margin: auto;

            background: white;

            padding: 40px;

            border-radius: 15px;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);
        }

        h1 {

            margin-top: 0;

            margin-bottom: 25px;

            color: #2a5298;
        }

        .bill-info {

            background: #eef5ff;

            padding: 20px;

            border-radius: 10px;

            margin-bottom: 30px;

            line-height: 1.8;
        }

        table {

            width: 100%;

            border-collapse: collapse;

            margin-top: 20px;
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

        .totals {

            margin-top: 30px;

            text-align: right;
        }

        .totals h3 {

            margin: 10px 0;

            color: #2a5298;
        }

        .back-link {

            display: inline-block;

            margin-top: 30px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Bill Details</h1>

    <div class="bill-info">

        <strong>Bill Serial:</strong>
        #<%= bill.getSerialNumber() %>

        <br>

        <strong>Date:</strong>
        <%= bill.getDate() %>

    </div>

    <table>

        <thead>

        <tr>

            <th>Item Code</th>

            <th>Item Name</th>

            <th>Quantity</th>

            <th>Line Total</th>

        </tr>

        </thead>

        <tbody>

        <% for (BillLineItem line : bill.getItems()) { %>

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
                <%= line.getLineTotal().getAmount() %>

            </td>

        </tr>

        <% } %>

        </tbody>

    </table>

    <div class="totals">

        <h3>

            Total:
            Rs.
            <%= bill.getTotal().getAmount() %>

        </h3>

        <h3>

            Final Total:
            Rs.
            <%= bill.getFinalTotal().getAmount() %>

        </h3>

    </div>

    <a class="back-link"
       href="<%= request.getContextPath() %>/pos?action=viewBills">

        ← Back to Reports

    </a>

</div>

</body>

</html>