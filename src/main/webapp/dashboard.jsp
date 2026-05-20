<%@ page import="com.sypos.concurrency.SystemMetrics" %>
<%@ page import="com.sypos.concurrency.CheckoutQueueManager" %>

<html>
<head>
    <title>SYOS System Dashboard</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 30px;
        }

        .card {
            border: 1px solid #ccc;
            padding: 20px;
            margin-bottom: 20px;
            width: 300px;
            border-radius: 10px;
        }

        h1 {
            margin-bottom: 30px;
        }

        .metric {
            font-size: 24px;
            font-weight: bold;
        }
    </style>
</head>

<body>

<h1>SYOS Concurrency Dashboard</h1>

<div class="card">
    <h3>Total HTTP Requests</h3>

    <div class="metric">
        <%= SystemMetrics.totalHttpRequests.get() %>
    </div>
</div>

<div class="card">
    <h3>Total Queued Bills</h3>

    <div class="metric">
        <%= SystemMetrics.totalQueuedBills.get() %>
    </div>
</div>

<div class="card">
    <h3>Total Processed Bills</h3>

    <div class="metric">
        <%= SystemMetrics.totalProcessedBills.get() %>
    </div>
</div>

<div class="card">
    <h3>Current Queue Size</h3>

    <div class="metric">
        <%= CheckoutQueueManager.getQueue().size() %>
    </div>
</div>

<div class="card">
    <h3>Worker Threads</h3>

    <div class="metric">
        3
    </div>
</div>

</body>
</html>