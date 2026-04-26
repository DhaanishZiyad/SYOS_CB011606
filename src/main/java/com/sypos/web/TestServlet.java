package com.sypos.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class TestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.getWriter().println("Servlet is working!");
    }
}