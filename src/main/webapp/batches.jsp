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
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        table {
            width: 90%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        a {
            text-decoration: none;
        }

    </style>

</head>

<body>

<h1>
    Batch Viewer -
    <%= itemCode %>
</h1>

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
        <td colspan="5">
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

        String rowColor =
                expired
                        ? "#ffcccc"
                        : nearExpiry
                        ? "#fff3cd"
                        : "white";
    %>

    <tr style="background-color: <%= rowColor %>">

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

            <strong style="color:red;">
                EXPIRED
            </strong>

            <% } else if (nearExpiry) { %>

            <strong style="color:orange;">
                NEAR EXPIRY
            </strong>

            <% } else { %>

            <span style="color:green;">
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

                <button type="submit">
                    Remove Batch
                </button>

            </form>

            <% } %>

        </td>

    </tr>

    <% } %>

    </tbody>

</table>

<br>

<a href="pos?action=inventory">
    ← Back to Inventory
</a>

</body>
</html>