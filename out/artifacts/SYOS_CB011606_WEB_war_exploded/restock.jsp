<%
    String itemCode =
            request.getParameter("itemCode");
%>

<html>

<head>

    <title>Restock Inventory</title>

    <style>

        body {

            margin: 0;

            padding: 40px;

            font-family: Arial, sans-serif;

            background: #f4f6f9;

            color: #333;
        }

        .container {

            max-width: 600px;

            margin: auto;

            background: white;

            padding: 40px;

            border-radius: 15px;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);
        }

        h1 {

            margin-top: 0;

            margin-bottom: 30px;

            color: #2a5298;
        }

        label {

            font-weight: bold;

            display: block;

            margin-bottom: 8px;
        }

        input {

            width: 100%;

            padding: 12px;

            margin-bottom: 20px;

            border: 1px solid #ccc;

            border-radius: 8px;

            box-sizing: border-box;

            font-size: 14px;
        }

        input:focus {

            outline: none;

            border-color: #2a5298;
        }

        .submit-btn {

            width: 100%;

            padding: 14px;

            border: none;

            border-radius: 8px;

            background: #27ae60;

            color: white;

            font-size: 16px;

            font-weight: bold;

            cursor: pointer;

            transition: background 0.2s;
        }

        .submit-btn:hover {
            background: #219150;
        }

        .back-link {

            display: inline-block;

            margin-top: 25px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

        .info-box {

            background: #eef5ff;

            padding: 15px;

            border-radius: 8px;

            margin-bottom: 25px;

            color: #2a5298;

            font-size: 14px;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Restock Inventory</h1>

    <div class="info-box">

        Add a new stock batch with
        purchase and expiry tracking.

    </div>

    <form action="pos?action=restock"
          method="POST">

        <label>

            Item Code

        </label>

        <input type="text"
               name="itemCode"
               value="<%= itemCode %>"
               readonly>

        <label>

            Quantity Received

        </label>

        <input type="number"
               name="quantity"
               min="1"
               required>

        <label>

            Purchase Date

        </label>

        <input type="date"
               name="purchaseDate"
               required>

        <label>

            Expiry Date

        </label>

        <input type="date"
               name="expiryDate"
               required>

        <button class="submit-btn"
                type="submit">

            Add Stock Batch

        </button>

    </form>

    <a class="back-link"
       href="pos?action=inventory">

        ← Back to Inventory

    </a>

</div>

</body>

</html>