<html>
<head>
    <title>SYOS Admin Panel</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        h1 {
            margin-bottom: 30px;
        }

        .card {
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 20px;
            width: 300px;
            margin-bottom: 20px;
        }

        a {
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>

<body>

<h1>SYOS Admin Interface</h1>

<div class="card">
    <h3>Reports</h3>

    <p>View sales and inventory reports.</p>

    <a href="pos?action=viewBills">
        View Bills
    </a>
</div>

<div class="card">
    <h3>Concurrency Dashboard</h3>

    <p>Monitor queue activity and workers.</p>

    <a href="dashboard.jsp">
        Open Dashboard
    </a>
</div>

<div class="card">
    <h3>Stock Management</h3>

    <p>Manage inventory and stock batches.</p>

    <a href="#">
        Coming Soon
    </a>
</div>

<br>

<a href="index.jsp">
    ← Back to Home
</a>

</body>
</html>