<%@ page import="com.sypos.concurrency.SystemMetrics" %>
<%@ page import="com.sypos.concurrency.CheckoutQueueManager" %>

<html>

<head>

    <title>SYOS System Dashboard</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 40px;
            background-color: #f4f6f9;
        }

        h1 {
            margin-bottom: 30px;
            color: #333;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            transition: transform 0.2s;
        }

        .card:hover {
            transform: translateY(-5px);
        }

        .card h3 {
            margin-top: 0;
            color: #666;
            font-size: 18px;
        }

        .metric {
            font-size: 42px;
            font-weight: bold;
            margin-top: 15px;
        }

        .blue {
            border-left: 8px solid #3498db;
        }

        .orange {
            border-left: 8px solid #f39c12;
        }

        .green {
            border-left: 8px solid #2ecc71;
        }

        .red {
            border-left: 8px solid #e74c3c;
        }

        .purple {
            border-left: 8px solid #9b59b6;
        }

        .footer {
            margin-top: 40px;
            color: #777;
            font-size: 14px;
        }

    </style>

</head>

<body>

<h1>SYOS Concurrency Dashboard</h1>

<div class="dashboard-grid">

    <div class="card blue">

        <h3>Total HTTP Requests</h3>

        <div class="metric" id="httpRequests">
            <%= SystemMetrics.totalHttpRequests.get() %>
        </div>

    </div>

    <div class="card orange">

        <h3>Total Queued Bills</h3>

        <div class="metric" id="queuedBillsCard">
            <%= SystemMetrics.totalQueuedBills.get() %>
        </div>

    </div>

    <div class="card green">

        <h3>Total Processed Bills</h3>

        <div class="metric" id="processedBillsCard">
            <%= SystemMetrics.totalProcessedBills.get() %>
        </div>

    </div>

    <div class="card red">

        <h3>Current Queue Size</h3>

        <div class="metric" id="queueSizeCard">
            <%= CheckoutQueueManager.getQueue().size() %>
        </div>

    </div>

    <div class="card purple">

        <h3>Worker Threads</h3>

        <div class="metric">
            3
        </div>

    </div>

</div>

<div class="footer">
    Live system metrics powered by WebSockets and concurrent worker threads.
</div>

<script>

    const socket =
        new WebSocket(
            "ws://"
            + window.location.host
            + "<%= request.getContextPath() %>"
            + "/ws/metrics"
        );

    socket.onopen = () => {

        console.log(
            "WebSocket connected"
        );
    };

    socket.onmessage = (event) => {

        const data =
            JSON.parse(event.data);

        document
            .getElementById("httpRequests")
            .innerText =
            data.httpRequests;

        document
            .getElementById("queueSizeCard")
            .innerText =
            data.queueSize;

        document
            .getElementById("processedBillsCard")
            .innerText =
            data.processedBills;

        document
            .getElementById("queuedBillsCard")
            .innerText =
            data.queuedBills;
    };

    socket.onclose = () => {

        console.log(
            "WebSocket disconnected"
        );
    };

</script>

</body>

</html>