<%
    String itemCode =
            request.getParameter("itemCode");
%>

<html>

<head>
    <title>Restock Item</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        form {
            margin-top: 20px;
        }

        input {
            padding: 8px;
            margin-bottom: 10px;
        }

        button {
            padding: 10px;
        }
    </style>
</head>

<body>

<h1>Restock Inventory</h1>

<form action="pos?action=restock"
      method="POST">

    <label>
        Item Code
    </label>

    <br>

    <input type="text"
           name="itemCode"
           value="<%= itemCode %>"
           readonly>

    <br><br>

    <label>
        Quantity Received
    </label>

    <br>

    <input type="number"
           name="quantity"
           min="1"
           required>

    <br><br>

    <label>
        Purchase Date
    </label>

    <br>

    <input type="date"
           name="purchaseDate"
           required>

    <br><br>

    <label>
        Expiry Date
    </label>

    <br>

    <input type="date"
           name="expiryDate"
           required>

    <br><br>

    <button type="submit">
        Add Stock Batch
    </button>

</form>

<br>

<a href="pos?action=inventory">
    ← Back to Inventory
</a>

</body>
</html>