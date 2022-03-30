package cn.safe6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {




    @RequestMapping("/index")
    public String test(){
        return "index";
    }
}
