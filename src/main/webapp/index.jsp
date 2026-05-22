<html>

<head>

    <title>SYOS POS System</title>

    <style>

        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
        }

        .hero {
            background: linear-gradient(
                    135deg,
                    #1e3c72,
                    #2a5298
            );

            color: white;
            padding: 60px 40px;
            text-align: center;
        }

        .hero h1 {
            margin: 0;
            font-size: 48px;
        }

        .hero p {
            margin-top: 15px;
            font-size: 18px;
            opacity: 0.9;
        }

        .card-container {
            display: grid;
            grid-template-columns:
                    repeat(
                            auto-fit,
                            minmax(280px, 1fr)
                    );

            gap: 25px;

            padding: 40px;
        }

        .card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);

            transition:
                    transform 0.2s,
                    box-shadow 0.2s;
        }

        .card:hover {
            transform: translateY(-5px);

            box-shadow:
                    0 8px 18px rgba(0,0,0,0.12);
        }

        .card h2 {
            margin-top: 0;
            color: #1e3c72;
        }

        .card p {
            color: #666;
            line-height: 1.5;
            min-height: 60px;
        }

        .btn {
            display: inline-block;

            margin-top: 15px;

            padding: 12px 20px;

            border-radius: 8px;

            background: #2a5298;

            color: white;

            text-decoration: none;

            font-weight: bold;

            transition: background 0.2s;
        }

        .btn:hover {
            background: #1e3c72;
        }

        .coming-soon {
            background: #999;
        }

        .footer {
            text-align: center;
            padding: 25px;
            color: #777;
            font-size: 14px;
        }

    </style>

</head>

<body>

<div class="hero">

    <h1>SYOS POS System</h1>

    <p>
        Concurrent Multi-User Point of Sale
        and Inventory Management Platform
    </p>

</div>

<div class="card-container">

    <div class="card">

        <h2>Cashier Interface</h2>

        <p>
            Start billing sessions, add items,
            and process customer checkouts
            through the concurrent queue system.
        </p>

        <a class="btn"
           href="pos?action=start">

            Open Cashier

        </a>

    </div>

    <div class="card">

        <h2>Admin Interface</h2>

        <p>
            Monitor real-time dashboard metrics,
            manage inventory, batches,
            stock levels, and reports.
        </p>

        <a class="btn"
           href="admin.jsp">

            Open Admin Panel

        </a>

    </div>

    <div class="card">

        <h2>Customer Shop</h2>

        <p>
            Future customer-facing online shop
            integration for SYOS retail systems.
        </p>

        <a class="btn coming-soon"
           href="#">

            Coming Soon

        </a>

    </div>

</div>

<div class="footer">

    Powered by Java, JSP, MySQL,
    Concurrent Workers, and WebSockets

</div>

</body>

</html>