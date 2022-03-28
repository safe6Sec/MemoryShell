package cn.safe6.servlet;

import javafx.application.Application;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

@WebServlet("/add")
public class AddShellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext servletContext = req.getServletContext();
        try {
            Field appctx =servletContext.getClass().getDeclaredField("context");
            appctx.setAccessible(true);

            //ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);
            //Application


        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write("add success!");
    }
}
