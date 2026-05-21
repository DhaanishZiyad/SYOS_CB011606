<html>

<head>
    <title>Add Item</title>
</head>

<body>

<h1>Add New Item</h1>

<form action="pos"
      method="POST">

    <input type="hidden"
           name="action"
           value="addInventoryItem">

    <label>Item Code</label>
    <br>

    <input type="text"
           name="code"
           required>

    <br><br>

    <label>Item Name</label>
    <br>

    <input type="text"
           name="name"
           required>

    <br><br>

    <label>Unit Price</label>
    <br>

    <input type="number"
           step="0.01"
           name="unitPrice"
           required>

    <br><br>

    <button type="submit">
        Save Item
    </button>

</form>

<br>

<a href="pos?action=inventory">
    ← Back to Inventory
</a>

</body>

</html>