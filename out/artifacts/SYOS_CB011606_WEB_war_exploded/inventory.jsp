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
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        table {
            width: 80%;
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

<h1>Inventory Viewer</h1>

<a href="add-item.jsp">
    <button>
        Add Item
    </button>
</a>

<table>

    <thead>

    <tr>
        <th>Item Code</th>
        <th>Name</th>
        <th>Unit Price</th>
        <th>Stock Qty</th>
        <th>Status</th>
        <th>Action</th>
    </tr>
    </thead>

    <tbody>

    <% if (items == null || items.isEmpty()) { %>

    <tr>
        <td colspan="3">
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

            String rowColor =
                    qty < 50
                            ? "#ffcccc"
                            : "white";
        %>

            <tr style="background-color: <%= rowColor %>;">
                <td>
                    <%= item.getCode().getValue() %>
                </td>

                <td>
                    <%= item.getName() %>
                </td>

                <td>
                    <%= item.getUnitPrice().getAmount() %>
                </td>

                <td>
                    <%= stockMap.getOrDefault(
                            item.getCode().getValue(),
                            0
                    ) %>
                </td>

                <td>

                    <% if (qty < 50) { %>

                        <strong style="color:red;">
                            LOW STOCK
                        </strong>

                    <% } else { %>

                        <span style="color:green;">
                            AVAILABLE
                        </span>

                    <% } %>

                </td>

                <td>

                    <form action="restock.jsp"
                          method="GET">

                        <input type="hidden"
                               name="itemCode"
                               value="<%= item.getCode().getValue() %>">

                        <button type="submit">
                            Restock
                        </button>

                    </form>

                    <form action="pos"
                          method="GET"
                          style="display:inline;">

                        <input type="hidden"
                               name="action"
                               value="viewBatches">

                        <input type="hidden"
                               name="itemCode"
                               value="<%= item.getCode().getValue() %>">

                        <button type="submit">
                            View Batches
                        </button>

                    </form>

                </td>
            </tr>

        <% } %>

    <% } %>

    </tbody>

</table>

<br>

<a href="admin.jsp">
    ← Back to Admin Panel
</a>

</body>
</html>