<%@ page import="java.util.List" %>
<%@ page import="com.sypos.domain.entities.StockBatch" %>

<%
    String itemCode =
            (String) request.getAttribute("itemCode");

    List<StockBatch> batches =
            (List<StockBatch>)
                    request.getAttribute("batches");

    java.time.LocalDate today =
            java.time.LocalDate.now();
%>

<html>

<head>

    <title>Batch Viewer</title>

    <style>

        body {
            margin: 0;
            padding: 40px;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
        }

        h1 {
            margin-bottom: 25px;
        }

        .live-indicator {

            margin-bottom: 20px;

            color: #27ae60;

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
        }

        th {

            background: #34495e;

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

        .expired {
            background: #ffe5e5;
        }

        .near-expiry {
            background: #fff5d6;
        }

        .healthy {
            background: white;
        }

        .status-expired {

            color: #e74c3c;

            font-weight: bold;
        }

        .status-warning {

            color: #f39c12;

            font-weight: bold;
        }

        .status-healthy {

            color: #2ecc71;

            font-weight: bold;
        }

        .remove-btn {

            padding: 10px 14px;

            border: none;

            border-radius: 6px;

            background: #e74c3c;

            color: white;

            font-weight: bold;

            cursor: pointer;

            transition: background 0.2s;
        }

        .remove-btn:hover {
            background: #c0392b;
        }

        .back-link {

            display: inline-block;

            margin-top: 30px;

            text-decoration: none;

            font-weight: bold;

            color: #34495e;
        }

    </style>

</head>

<body>

<h1>

    Batch Viewer -
    <%= itemCode %>

</h1>

<div class="live-indicator">
    ● Live batch synchronization enabled
</div>

<table>

    <thead>

    <tr>

        <th>Batch ID</th>

        <th>Purchase Date</th>

        <th>Expiry Date</th>

        <th>Remaining Qty</th>

        <th>Status</th>

        <th>Action</th>

    </tr>

    </thead>

    <tbody>

    <% if (batches == null || batches.isEmpty()) { %>

    <tr>

        <td colspan="6">

            No batches found.

        </td>

    </tr>

    <% } else { %>

    <% for (StockBatch batch : batches) {

        boolean expired =
                batch.getExpiryDate()
                        .isBefore(today);

        boolean nearExpiry =
                batch.getExpiryDate()
                        .isBefore(
                                today.plusDays(30)
                        );

        String rowClass =
                expired
                        ? "expired"
                        : nearExpiry
                        ? "near-expiry"
                        : "healthy";

    %>

    <tr class="<%= rowClass %>">

        <td>
            <%= batch.getId() %>
        </td>

        <td>
            <%= batch.getPurchaseDate() %>
        </td>

        <td>
            <%= batch.getExpiryDate() %>
        </td>

        <td>
            <%= batch.getQuantity().getValue() %>
        </td>

        <td>

            <% if (expired) { %>

            <span class="status-expired">

                        EXPIRED

                    </span>

            <% } else if (nearExpiry) { %>

            <span class="status-warning">

                        NEAR EXPIRY

                    </span>

            <% } else { %>

            <span class="status-healthy">

                        HEALTHY

                    </span>

            <% } %>

        </td>

        <td>

            <form action="pos"
                  method="POST">

                <input type="hidden"
                       name="action"
                       value="removeBatch">

                <input type="hidden"
                       name="batchId"
                       value="<%= batch.getId() %>">

                <input type="hidden"
                       name="itemCode"
                       value="<%= itemCode %>">

                <input type="hidden"
                       name="remainingQty"
                       value="<%= batch.getQuantity().getValue() %>">

                <button class="remove-btn"
                        type="submit">

                    Remove Batch

                </button>

            </form>

        </td>

    </tr>

    <% } %>

    <% } %>

    </tbody>

</table>

<a class="back-link"
   href="pos?action=inventory">

    ← Back to Inventory

</a>

</body>

</html>