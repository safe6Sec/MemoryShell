package com.demo.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DefineTransformer implements ClassFileTransformer {


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        //有类加载就会触发
        //需注册inst.addTransformer(new DefineTransformer(),true);
        //System.out.println(className);
        System.out.println(11111);
        System.out.println("transform");
        String cn = "org.apache.catalina.core.ApplicationFilterChain";
        if (className.equals(cn)){
            System.out.println(cn);
        }


        return classfileBuffer;
    }
}
