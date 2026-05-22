<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sypos.domain.entities.Item" %>

<%
    List<Item> items =
            (List<Item>) request.getAttribute("items");

    Map<String, Integer> stockMap =
            (Map<String, Integer>)
                    request.getAttribute("stockMap");
%>

<html>

<head>

    <title>Inventory Management</title>

    <style>

        body {
            margin: 0;
            padding: 40px;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
        }

        h1 {
            margin-bottom: 30px;
        }

        .top-bar {

            display: flex;

            justify-content: space-between;

            align-items: center;

            margin-bottom: 25px;
        }

        .btn {

            padding: 12px 18px;

            border: none;

            border-radius: 8px;

            background: #2a5298;

            color: white;

            font-weight: bold;

            cursor: pointer;

            text-decoration: none;

            transition: background 0.2s;
        }

        .btn:hover {
            background: #1e3c72;
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

        .low-stock {
            background-color: #ffe5e5;
        }

        .healthy-stock {
            background-color: white;
        }

        .status-low {

            color: #e74c3c;

            font-weight: bold;
        }

        .status-ok {

            color: #2ecc71;

            font-weight: bold;
        }

        .action-buttons {

            display: flex;

            gap: 10px;

            flex-wrap: wrap;
        }

        .small-btn {

            padding: 8px 12px;

            border: none;

            border-radius: 6px;

            cursor: pointer;

            font-weight: bold;

            color: white;
        }

        .restock-btn {
            background: #27ae60;
        }

        .batch-btn {
            background: #8e44ad;
        }

        .back-link {

            display: inline-block;

            margin-top: 30px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

        .live-indicator {

            margin-bottom: 20px;

            color: #27ae60;

            font-weight: bold;
        }

    </style>

</head>

<body>

<div class="top-bar">

    <h1>Inventory Management</h1>

    <a class="btn"
       href="add-item.jsp">

        + Add Item

    </a>

</div>

<div class="live-indicator">
    ● Live inventory synchronization enabled
</div>

<table>

    <thead>

    <tr>

        <th>Item Code</th>

        <th>Name</th>

        <th>Unit Price</th>

        <th>Stock Qty</th>

        <th>Status</th>

        <th>Actions</th>

    </tr>

    </thead>

    <tbody>

    <% if (items == null || items.isEmpty()) { %>

    <tr>

        <td colspan="6">

            No inventory items found.

        </td>

    </tr>

    <% } else { %>

    <% for (Item item : items) { %>

    <%

        int qty =
                stockMap.getOrDefault(
                        item.getCode().getValue(),
                        0
                );

        String rowClass =
                qty < 50
                        ? "low-stock"
                        : "healthy-stock";

    %>

    <tr class="<%= rowClass %>">

        <td>
            <%= item.getCode().getValue() %>
        </td>

        <td>
            <%= item.getName() %>
        </td>

        <td>
            Rs.
            <%= item.getUnitPrice().getAmount() %>
        </td>

        <td>
            <%= qty %>
        </td>

        <td>

            <% if (qty < 50) { %>

            <span class="status-low">

                            LOW STOCK

                        </span>

            <% } else { %>

            <span class="status-ok">

                            AVAILABLE

                        </span>

            <% } %>

        </td>

        <td>

            <div class="action-buttons">

                <form action="restock.jsp"
                      method="GET">

                    <input type="hidden"
                           name="itemCode"
                           value="<%= item.getCode().getValue() %>">

                    <button class="small-btn restock-btn"
                            type="submit">

                        Restock

                    </button>

                </form>

                <form action="pos"
                      method="GET">

                    <input type="hidden"
                           name="action"
                           value="viewBatches">

                    <input type="hidden"
                           name="itemCode"
                           value="<%= item.getCode().getValue() %>">

                    <button class="small-btn batch-btn"
                            type="submit">

                        View Batches

                    </button>

                </form>

            </div>

        </td>

    </tr>

    <% } %>

    <% } %>

    </tbody>

</table>

<a class="back-link"
   href="admin.jsp">

    ← Back to Admin Panel

</a>

<script>

    const inventorySocket =
        new WebSocket(
            "ws://"
            + window.location.host
            + "<%= request.getContextPath() %>"
            + "/ws/inventory"
        );

    inventorySocket.onmessage = () => {

        location.reload();
    };

</script>

</body>

</html>