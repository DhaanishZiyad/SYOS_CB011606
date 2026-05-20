<html>
<head>
    <title>SYOS POS System</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        h1 {
            margin-bottom: 30px;
        }

        .card-container {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }

        .card {
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 20px;
            width: 300px;
        }

        a {
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>

<body>

<h1>SYOS POS System</h1>

<div class="card-container">

    <div class="card">
        <h2>Cashier Interface</h2>

        <p>
            Start and manage POS billing sessions.
        </p>

        <a href="pos?action=start">
            Open Cashier
        </a>
    </div>

    <div class="card">
        <h2>Admin Interface</h2>

        <p>
            View reports, dashboard, and inventory tools.
        </p>

        <a href="admin.jsp">
            Open Admin Panel
        </a>
    </div>

    <div class="card">
        <h2>Customer Shop</h2>

        <p>
            Browse products and shop online.
        </p>

        <a href="#">
            Coming Soon
        </a>
    </div>

</div>

</body>
</html>