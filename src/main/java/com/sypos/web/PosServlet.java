package com.sypos.web;

import com.sypos.adapters.controllers.PosController;
import com.sypos.config.ApplicationConfig;
import com.sypos.domain.entities.Bill;

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
        }

        resp.setContentType("text/html");
        resp.getWriter().println("""
                <html>
                <body>
                    <h1>POS System Running 🚀</h1>
                    <a href="pos?action=start">Start New Sale</a>
                </body>
                </html>
                """);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        Bill bill = (Bill) session.getAttribute("currentBill");

        if (bill == null) {
            resp.sendRedirect("pos?action=error&msg=NoActiveSession");
            return;
        }

        try {
            if ("addItem".equals(action)) {
                String code = req.getParameter("itemCode");
                int qty = Integer.parseInt(req.getParameter("quantity"));

                // This calls AddItemToBillUseCase via the controller
                controller.addItem(bill, code, qty);
                resp.sendRedirect("pos-ui.jsp");

            } else if ("checkout".equals(action)) {
                java.math.BigDecimal tendered = new java.math.BigDecimal(req.getParameter("tendered"));

                // This calls FinalizeCheckoutUseCase which updates DB and Inventory
                var result = controller.checkout(bill, tendered);

                session.setAttribute("lastResult", result);
                session.removeAttribute("currentBill"); // Clear the session bill
                resp.sendRedirect("receipt.jsp");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("pos-ui.jsp").forward(req, resp);
        }
    }
}