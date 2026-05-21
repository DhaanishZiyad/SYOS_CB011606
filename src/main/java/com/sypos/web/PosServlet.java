package com.sypos.web;

import com.sypos.adapters.controllers.PosController;
import com.sypos.config.ApplicationConfig;
import com.sypos.domain.entities.Bill;
import com.sypos.concurrency.SystemMetrics;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException; // Critical fix for the error in your image

@WebServlet("/pos")
public class PosServlet extends HttpServlet {

    private PosController controller;

    @Override
    public void init() throws ServletException {
        // Correctly assigns the controller using your ApplicationConfig
        controller = ApplicationConfig.createController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        HttpSession session = req.getSession();

        // Simple routing for session-based sale handling
        if ("start".equals(action)) {
            Bill newBill = controller.startNewSale(); // Uses CreateBillUseCase
            session.setAttribute("currentBill", newBill);
            resp.sendRedirect("pos-ui.jsp");
            return;
        } else if ("viewBills".equals(action)) {
            var report = controller.generateBillReport();

            req.setAttribute("billReport", report);
            req.getRequestDispatcher("bills.jsp").forward(req, resp);
        } else if ("viewBillDetails".equals(action)) {
            int serial = Integer.parseInt(req.getParameter("serial"));

            Bill bill = controller.findBillBySerial(serial);

            req.setAttribute("bill", bill);
            req.getRequestDispatcher("bill-details.jsp").forward(req, resp);
            return;
        } else if ("inventory".equals(action)) {

            var items = controller.getAllItems();

            var stockMap =
                    controller.getAllShelfStock();

            req.setAttribute("items", items);

            req.setAttribute(
                    "stockMap",
                    stockMap
            );

            req.getRequestDispatcher("inventory.jsp")
                    .forward(req, resp);

            return;
        } else if ("viewBatches".equals(action)) {

            String itemCode =
                    req.getParameter("itemCode");

            var batches =
                    controller.getBatchesForItem(
                            itemCode
                    );

            req.setAttribute(
                    "itemCode",
                    itemCode
            );

            req.setAttribute(
                    "batches",
                    batches
            );

            req.getRequestDispatcher(
                    "batches.jsp"
            ).forward(req, resp);

            return;
        }

        resp.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        SystemMetrics.totalHttpRequests.incrementAndGet();
        HttpSession session = req.getSession();
        Bill bill = (Bill) session.getAttribute("currentBill");

        try {
            if ("simulateCheckout".equals(action)) {

                Bill simulatedBill = controller.startNewSale();

                controller.addItem(simulatedBill, "MILK001", 2);

                java.math.BigDecimal tendered =
                        new java.math.BigDecimal("5000");

                com.sypos.concurrency.CheckoutQueueManager
                        .getQueue()
                        .put(
                                new com.sypos.concurrency.CheckoutTask(
                                        simulatedBill,
                                        tendered
                                )
                        );
                SystemMetrics.totalQueuedBills.incrementAndGet();

                System.out.println(
                        "Added bill "
                                + simulatedBill.getSerialNumber()
                                + " to queue"
                );

                resp.getWriter().println(
                        "Queued simulated checkout for bill "
                                + simulatedBill.getSerialNumber()
                );

                return;
            } else if ("restock".equals(action)) {

                String itemCode =
                        req.getParameter("itemCode");

                int quantity =
                        Integer.parseInt(
                                req.getParameter("quantity")
                        );

                java.time.LocalDate purchaseDate =
                        java.time.LocalDate.parse(
                                req.getParameter("purchaseDate")
                        );

                java.time.LocalDate expiryDate =
                        java.time.LocalDate.parse(
                                req.getParameter("expiryDate")
                        );

                controller.restockItem(
                        itemCode,
                        quantity,
                        purchaseDate,
                        expiryDate
                );

                resp.sendRedirect(
                        "pos?action=inventory"
                );

                return;
            } else if ("removeBatch".equals(action)) {

                long batchId =
                        Long.parseLong(
                                req.getParameter("batchId")
                        );

                String itemCode =
                        req.getParameter("itemCode");

                int remainingQty =
                        Integer.parseInt(
                                req.getParameter("remainingQty")
                        );

                controller.removeBatch(
                        batchId,
                        itemCode,
                        remainingQty
                );

                resp.sendRedirect(
                        "pos?action=viewBatches&itemCode="
                                + itemCode
                );

                return;
            }else if ("addInventoryItem".equals(action)) {

                String code =
                        req.getParameter("code");

                String name =
                        req.getParameter("name");

                java.math.BigDecimal unitPrice =
                        new java.math.BigDecimal(
                                req.getParameter("unitPrice")
                        );

                controller.createItem(
                        code,
                        name,
                        unitPrice
                );

                resp.sendRedirect(
                        "pos?action=inventory"
                );

                return;
            } else if (bill == null) {
                resp.sendRedirect("pos?action=error&msg=NoActiveSession");
                return;


            } else if ("addItem".equals(action)) {
                String code = req.getParameter("itemCode");
                int qty = Integer.parseInt(req.getParameter("quantity"));

                // This calls AddItemToBillUseCase via the controller
                controller.addItem(bill, code, qty);
                resp.sendRedirect("pos-ui.jsp");
            } else if ("removeItem".equals(action)) {
                int index = Integer.parseInt(req.getParameter("index"));

                controller.removeItem(bill, index);
                resp.sendRedirect("pos-ui.jsp");
            } else if ("checkout".equals(action)) {
                java.math.BigDecimal tendered = new java.math.BigDecimal(req.getParameter("tendered"));

                // This calls FinalizeCheckoutUseCase which updates DB and Inventory
                com.sypos.concurrency.CheckoutQueueManager
                        .getQueue()
                        .put(
                                new com.sypos.concurrency.CheckoutTask(
                                        bill,
                                        tendered
                                )
                        );
                SystemMetrics.totalQueuedBills.incrementAndGet();

                session.removeAttribute("currentBill");

                resp.setContentType("text/html");

                resp.getWriter().println("""
                    <html>
                    <body>
                        <h2>Checkout queued successfully ✅</h2>
                        <p>Your checkout request is being processed.</p>
                
                        <a href="index.jsp">Back Home</a>
                    </body>
                    </html>
                """);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("pos-ui.jsp").forward(req, resp);
        }
    }


}