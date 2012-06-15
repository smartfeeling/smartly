package org.smartly.packages.http.impl.handlers.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a simple testing servlet that respond with "Hello World".
 */
public class HelloSmartlyServlet
        extends HttpServlet {

    private String _greeting = "Hello Smartly!";
    private String _params = "";

    public HelloSmartlyServlet() {

    }

    public HelloSmartlyServlet(final Object params) {
        _params = null!=params?params.toString():"";
    }

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>" + _greeting + "</h1><br>");
        response.getWriter().println("Parameters passed to constructor: <code>" + _params + "</code>");
        response.getWriter().println("session=" + request.getSession(true).getId());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
