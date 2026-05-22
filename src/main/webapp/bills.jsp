<%@ page import="com.sypos.application.dto.reports.BillReport" %>
<%@ page import="com.sypos.domain.entities.Bill" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>

    <title>SYOS POS - All Bills</title>

    <style>

        body {

            margin: 0;

            padding: 40px;

            font-family: Arial, sans-serif;

            background: #f4f6f9;

            color: #333;
        }

        h1 {

            margin-bottom: 25px;

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

        table {

            width: 100%;

            border-collapse: collapse;

            background: white;

            border-radius: 15px;

            overflow: hidden;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);
        }

        th {

            background: #2a5298;

            color: white;

            padding: 15px;

            text-align: left;
        }

        td {

            padding: 15px;

            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #f9fbff;
        }

        .bill-link {

            color: #2a5298;

            text-decoration: none;

            font-weight: bold;
        }

        .bill-link:hover {
            text-decoration: underline;
        }

        .empty {

            background: white;

            padding: 25px;

            border-radius: 10px;

            color: #777;

            box-shadow:
                    0 4px 12px rgba(0,0,0,0.08);
        }

        .back-link {

            display: inline-block;

            margin-top: 30px;

            text-decoration: none;

            font-weight: bold;

            color: #2a5298;
        }

    </style>

</head>

<body>

<h1>Sales Reports & Bills</h1>

<div class="info-box">

    View completed transaction history
    and inspect individual bill details.

</div>

<%

    BillReport report =
            (BillReport)
                    request.getAttribute(
                            "billReport"
                    );

%>

<% if (report == null || report.getBills().isEmpty()) { %>

<div class="empty">

    No bills found.

</div>

<% } else { %>

<table>

    <thead>

    <tr>

        <th>Serial No</th>

        <th>Date</th>

        <th>Total Amount</th>

    </tr>

    </thead>

    <tbody>

    <%

        for (Bill bill : report.getBills()) {

    %>

    <tr>

        <td>

            <a class="bill-link"
               href="<%= request.getContextPath() %>/pos?action=viewBillDetails&serial=<%= bill.getSerialNumber() %>">

                #<%= bill.getSerialNumber() %>

            </a>

        </td>

        <td>
            <%= bill.getDate() %>
        </td>

        <td>

            Rs.
            <%= bill.getTotal().getAmount() %>

        </td>

    </tr>

    <%

        }

    %>

    </tbody>

</table>

<% } %>

<a class="back-link"
   href="<%= request.getContextPath() %>/">

    ← Back to Home

</a>

</body>

</html>