<html>

<head>

    <title>Add Inventory Item</title>

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

        .info-box {

            background: #eef5ff;

            padding: 15px;

            border-radius: 8px;

            margin-bottom: 25px;

            color: #2a5298;

            font-size: 14px;
        }

        label {

            display: block;

            font-weight: bold;

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

            background: #2a5298;

            color: white;

            font-size: 16px;

            font-weight: bold;

            cursor: pointer;

            transition: background 0.2s;
        }

        .submit-btn:hover {
            background: #1e3c72;
        }

        .back-link {

            display: inline-block;

            margin-top: 25px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Add New Inventory Item</h1>

    <div class="info-box">

        Create a new item that can later
        be stocked, tracked,
        and sold through the POS system.

    </div>

    <form action="pos"
          method="POST">

        <input type="hidden"
               name="action"
               value="addInventoryItem">

        <label>

            Item Code

        </label>

        <input type="text"
               name="code"
               required>

        <label>

            Item Name

        </label>

        <input type="text"
               name="name"
               required>

        <label>

            Unit Price

        </label>

        <input type="number"
               step="0.01"
               name="unitPrice"
               required>

        <button class="submit-btn"
                type="submit">

            Save Item

        </button>

    </form>

    <a class="back-link"
       href="pos?action=inventory">

        ← Back to Inventory

    </a>

</div>

</body>

</html>